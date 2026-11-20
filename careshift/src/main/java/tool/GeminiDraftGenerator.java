package tool;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

/**
 * Gemini APIを利用して、家族へ送る依頼文の下書きを作成する。
 *
 * APIキーと利用者が入力した相談内容は、
 * 画面、コンソール、データベースログへ記録しない。
 */
public final class GeminiDraftGenerator implements DraftGenerator {

    /**
     * HTTP 429、503、応答タイムアウト時の最大試行回数。
     *
     * 最初の実行を含むため、再試行は最大1回。
     */
    private static final int MAX_ATTEMPTS = 2;

    /**
     * 接続開始までのタイムアウト。
     */
    private static final Duration CONNECT_TIMEOUT =
            Duration.ofSeconds(20);

    /**
     * 1回の生成リクエストに対するタイムアウト。
     */
    private static final Duration REQUEST_TIMEOUT =
            Duration.ofSeconds(180);

    /**
     * 再試行前の待機時間。
     */
    private static final long RETRY_WAIT_MILLIS =
            2000L;

    /**
     * Care Shiftの依頼内容は最大500文字なので、
     * 出力上限を必要十分な値にする。
     *
     * Geminiの思考トークンもこの上限に含まれる可能性があるため、
     * 余裕を持って2048にする。
     */
    private static final int MAX_OUTPUT_TOKENS =
            2048;

    /**
     * 依頼文の作成は複雑な推論を必要としないため、
     * Gemini 3.8の思考レベルをLOWにする。
     *
     * Gemini 3以降ではthinkingBudgetではなく
     * thinkingLevelを使用する。
     */
    private static final String THINKING_LEVEL =
            "LOW";

    /**
     * 新規ユーザーでも利用可能な既定モデル。
     */
    private static final String DEFAULT_MODEL =
            "gemini-3.8-flash";

    /**
     * Gemini APIの既定接続先。
     */
    private static final String DEFAULT_BASE_URL =
            "https://generativelanguage.googleapis.com";

    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public GeminiDraftGenerator(
            String apiKey,
            String model,
            String baseUrl
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    /**
     * VM引数または環境変数から設定を読み込む。
     *
     * APIキーの優先順位:
     *
     * 1.
     * -Dcareshift.ai.gemini.apikey=APIキー
     *
     * 2.
     * GEMINI_API_KEY
     *
     * モデルを変更する場合:
     *
     * -Dcareshift.ai.gemini.model=gemini-3.8-flash
     *
     * API接続先を変更する場合:
     *
     * -Dcareshift.ai.gemini.baseurl=https://generativelanguage.googleapis.com
     */
    public static GeminiDraftGenerator fromConfig() {

        String key =
                System.getProperty(
                        "careshift.ai.gemini.apikey"
                );

        if (key == null || key.isBlank()) {
            key =
                    System.getenv(
                            "GEMINI_API_KEY"
                    );
        }

        if (key == null || key.isBlank()) {
            return null;
        }

        String model =
                System.getProperty(
                        "careshift.ai.gemini.model",
                        DEFAULT_MODEL
                );

        String baseUrl =
                System.getProperty(
                        "careshift.ai.gemini.baseurl",
                        DEFAULT_BASE_URL
                );

        if (model == null || model.isBlank()) {
            model = DEFAULT_MODEL;
        }

        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = DEFAULT_BASE_URL;
        }

        model =
                normalizeModelName(
                        model
                );

        return new GeminiDraftGenerator(
                key.trim(),
                model,
                baseUrl.trim()
        );
    }

    @Override
    public Draft generate(
            String prompt
    ) throws UnavailableException {

        if (prompt == null || prompt.isBlank()) {
            throw new UnavailableException(
                    "相談内容が入力されていません"
            );
        }

        String requestBody =
                createRequestBody(prompt);

        try {
            String endpoint =
                    createEndpoint();

            logInfo(
                    "Gemini APIへ下書き生成を要求します"
                    + "（model="
                    + model
                    + ", maxOutputTokens="
                    + MAX_OUTPUT_TOKENS
                    + ", thinkingLevel="
                    + THINKING_LEVEL
                    + "）"
            );

            HttpClient client =
                    createHttpClient();

            HttpRequest request =
                    createHttpRequest(
                            endpoint,
                            requestBody
                    );

            HttpResponse<String> response =
                    sendWithRetry(
                            client,
                            request
                    );

            int statusCode =
                    response.statusCode();

            if (statusCode < 200
                    || statusCode >= 300) {

                String apiMessage =
                        extractApiErrorMessage(
                                response.body()
                        );

                String logMessage =
                        "Gemini APIの応答エラー"
                        + "（HTTP "
                        + statusCode
                        + ", model="
                        + model;

                if (!apiMessage.isEmpty()) {
                    logMessage +=
                            ", message="
                            + apiMessage;
                }

                logMessage += "）";

                logError(logMessage);

                throw new UnavailableException(
                        createUserSafeHttpMessage(
                                statusCode,
                                apiMessage
                        )
                );
            }

            String responseBody =
                    response.body();

            if (responseBody == null
                    || responseBody.isBlank()) {

                logError(
                        "Gemini APIから空の応答が返されました"
                        + "（model="
                        + model
                        + "）"
                );

                throw new UnavailableException(
                        "AIから空の応答が返されました"
                );
            }

            String generatedText =
                    MiniJson.firstString(
                            responseBody,
                            "text"
                    );

            if (generatedText == null
                    || generatedText.isBlank()) {

                String finishReason =
                        MiniJson.firstString(
                                responseBody,
                                "finishReason"
                        );

                String logMessage =
                        "Gemini APIの応答に生成文章がありません"
                        + "（model="
                        + model;

                if (finishReason != null
                        && !finishReason.isBlank()) {

                    logMessage +=
                            ", finishReason="
                            + sanitizeForLog(
                                    finishReason
                            );
                }

                logMessage += "）";

                logError(logMessage);

                throw new UnavailableException(
                        "AIの応答から依頼文を読み取れませんでした"
                );
            }

            Draft draft;

            try {
                draft =
                        ClaudeDraftGenerator.parse(
                                generatedText
                        );

            } catch (RuntimeException e) {

                logError(
                        "Geminiが生成した文章を下書き形式として解析できませんでした"
                        + "（"
                        + e.getClass().getSimpleName()
                        + "）"
                );

                throw new UnavailableException(
                        "AIが作成した内容を下書きとして読み取れませんでした"
                );
            }

            if (draft == null
                    || draft.content == null
                    || draft.content.isBlank()) {

                logError(
                        "Geminiの解析結果に依頼内容がありません"
                );

                throw new UnavailableException(
                        "AIが作成した依頼内容を読み取れませんでした"
                );
            }

            logInfo(
                    "Gemini APIによる下書き生成が完了しました"
                    + "（model="
                    + model
                    + "）"
            );

            return draft;

        } catch (UnavailableException e) {

            throw e;

        } catch (HttpConnectTimeoutException e) {

            logIOException(
                    "Gemini APIへの接続開始がタイムアウトしました",
                    e
            );

            throw new UnavailableException(
                    "AIサービスへの接続がタイムアウトしました"
            );

        } catch (HttpTimeoutException e) {

            logIOException(
                    "Gemini APIからの応答がタイムアウトしました",
                    e
            );

            throw new UnavailableException(
                    "AIサービスからの応答がタイムアウトしました"
            );

        } catch (UnknownHostException e) {

            logIOException(
                    "Gemini APIの接続先を名前解決できませんでした",
                    e
            );

            throw new UnavailableException(
                    "AIサービスの接続先を名前解決できませんでした"
            );

        } catch (ConnectException e) {

            logIOException(
                    "Gemini APIへの接続が拒否されました",
                    e
            );

            throw new UnavailableException(
                    "AIサービスへの接続が拒否されました"
            );

        } catch (SSLHandshakeException e) {

            logIOException(
                    "Gemini APIとのSSL通信を確立できませんでした",
                    e
            );

            throw new UnavailableException(
                    "AIサービスとの安全な通信を確立できませんでした"
            );

        } catch (SSLException e) {

            logIOException(
                    "Gemini APIとのSSL通信に失敗しました",
                    e
            );

            throw new UnavailableException(
                    "AIサービスとの安全な通信に失敗しました"
            );

        } catch (IOException e) {

            logIOException(
                    "Gemini APIへの通信に失敗しました",
                    e
            );

            throw new UnavailableException(
                    "AIサービスへ接続できませんでした"
                    + "（"
                    + e.getClass().getSimpleName()
                    + "）"
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            logError(
                    "Gemini APIへの通信が中断されました"
            );

            throw new UnavailableException(
                    "AIサービスへの接続が中断されました"
            );

        } catch (IllegalArgumentException e) {

            logError(
                    "Gemini APIの接続先またはモデル設定が不正です"
                    + "（"
                    + e.getClass().getName()
                    + "）"
            );

            throw new UnavailableException(
                    "AIサービスの接続設定が正しくありません"
            );
        }
    }

    /**
     * Gemini 3.8向けのリクエストJSONを作る。
     *
     * Gemini 3以降ではthinkingBudgetを使わず、
     * thinkingLevelを使用する。
     */
    private String createRequestBody(
            String prompt
    ) {

        return "{"
                + "\"systemInstruction\":{"
                + "\"parts\":[{"
                + "\"text\":"
                + MiniJson.quote(
                        ClaudeDraftGenerator.systemPrompt()
                )
                + "}]"
                + "},"
                + "\"contents\":[{"
                + "\"role\":\"user\","
                + "\"parts\":[{"
                + "\"text\":"
                + MiniJson.quote(prompt)
                + "}]"
                + "}],"
                + "\"generationConfig\":{"
                + "\"maxOutputTokens\":"
                + MAX_OUTPUT_TOKENS
                + ","
                + "\"temperature\":0.2,"
                + "\"thinkingConfig\":{"
                + "\"thinkingLevel\":"
                + MiniJson.quote(
                        THINKING_LEVEL
                )
                + "}"
                + "}"
                + "}";
    }

    /**
     * Java標準のHttpClientを作る。
     *
     * ネットワーク機器との互換性を高めるため、
     * HTTP/1.1を明示する。
     */
    private HttpClient createHttpClient() {

        return HttpClient.newBuilder()
                .connectTimeout(
                        CONNECT_TIMEOUT
                )
                .version(
                        HttpClient.Version.HTTP_1_1
                )
                .followRedirects(
                        HttpClient.Redirect.NORMAL
                )
                .build();
    }

    /**
     * Gemini APIへ送信するHTTPリクエストを作る。
     */
    private HttpRequest createHttpRequest(
            String endpoint,
            String requestBody
    ) {

        return HttpRequest.newBuilder(
                        URI.create(endpoint)
                )
                .timeout(
                        REQUEST_TIMEOUT
                )
                .header(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                )
                .header(
                        "Accept",
                        "application/json"
                )
                .header(
                        "X-Goog-Api-Key",
                        apiKey
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                requestBody,
                                StandardCharsets.UTF_8
                        )
                )
                .build();
    }

    /**
     * Gemini APIへ送信する。
     *
     * 次の場合のみ最大1回再試行する。
     *
     * ・HTTP 429
     * ・HTTP 503
     * ・応答タイムアウト
     */
    private HttpResponse<String> sendWithRetry(
            HttpClient client,
            HttpRequest request
    ) throws IOException, InterruptedException {

        HttpResponse<String> response =
                null;

        HttpTimeoutException lastTimeout =
                null;

        for (int attempt = 1;
                attempt <= MAX_ATTEMPTS;
                attempt++) {

            try {
                response =
                        client.send(
                                request,
                                HttpResponse.BodyHandlers.ofString(
                                        StandardCharsets.UTF_8
                                )
                        );

                int statusCode =
                        response.statusCode();

                if (statusCode != 429
                        && statusCode != 503) {

                    return response;
                }

                logError(
                        "Gemini APIが一時的に利用できません"
                        + "（HTTP "
                        + statusCode
                        + ", attempt="
                        + attempt
                        + "/"
                        + MAX_ATTEMPTS
                        + "）"
                );

            } catch (HttpTimeoutException e) {

                lastTimeout =
                        e;

                logError(
                        "Gemini APIからの応答がタイムアウトしました"
                        + "（attempt="
                        + attempt
                        + "/"
                        + MAX_ATTEMPTS
                        + "）"
                );

                if (attempt >= MAX_ATTEMPTS) {
                    throw e;
                }
            }

            if (attempt < MAX_ATTEMPTS) {
                Thread.sleep(
                        RETRY_WAIT_MILLIS
                );
            }
        }

        if (response != null) {
            return response;
        }

        if (lastTimeout != null) {
            throw lastTimeout;
        }

        throw new IOException(
                "Gemini APIから応答を取得できませんでした"
        );
    }

    /**
     * Gemini APIのエンドポイントを作る。
     */
    private String createEndpoint() {

        String encodedModel =
                URLEncoder.encode(
                        model,
                        StandardCharsets.UTF_8
                ).replace(
                        "+",
                        "%20"
                );

        return trimTrailingSlash(baseUrl)
                + "/v1beta/models/"
                + encodedModel
                + ":generateContent";
    }

    /**
     * GeminiのエラーJSONから、
     * 安全な短いエラーメッセージを取得する。
     */
    private String extractApiErrorMessage(
            String responseBody
    ) {

        if (responseBody == null
                || responseBody.isBlank()) {

            return "";
        }

        String message =
                MiniJson.firstString(
                        responseBody,
                        "message"
                );

        if (message == null
                || message.isBlank()) {

            return "";
        }

        return sanitizeForLog(message);
    }

    /**
     * HTTPエラーに応じた利用者向け説明を返す。
     */
    private String createUserSafeHttpMessage(
            int statusCode,
            String apiMessage
    ) {

        switch (statusCode) {

        case 400:

            if (containsIgnoreCase(
                    apiMessage,
                    "thinking"
            )) {
                return "AIモデルの思考設定が受け付けられませんでした";
            }

            return "AIサービスへの要求内容が受け付けられませんでした";

        case 401:
            return "AIサービスのAPIキーを確認してください";

        case 403:
            return "AIサービスを利用する権限がありません";

        case 404:

            if (containsIgnoreCase(
                    apiMessage,
                    "no longer available"
            )) {
                return "設定されているAIモデルは新規利用できません";
            }

            return "設定されているAIモデルが見つかりません";

        case 408:
            return "AIサービスへの接続がタイムアウトしました";

        case 429:
            return "AIサービス側の利用上限に達しています";

        case 500:
        case 502:
        case 503:
        case 504:
            return "AIサービスが一時的に利用できません";

        default:
            return "AIサービスから正常な応答を受け取れませんでした";
        }
    }

    /**
     * VM引数にmodels/付きで指定されても二重にならないよう、
     * モデル名を正規化する。
     */
    private static String normalizeModelName(
            String value
    ) {

        String normalized =
                value == null
                        ? ""
                        : value.trim();

        while (normalized.startsWith("models/")) {
            normalized =
                    normalized.substring(
                            "models/".length()
                    );
        }

        if (normalized.isBlank()) {
            return DEFAULT_MODEL;
        }

        return normalized;
    }

    private static boolean containsIgnoreCase(
            String value,
            String search
    ) {

        if (value == null || search == null) {
            return false;
        }

        return value.toLowerCase()
                .contains(
                        search.toLowerCase()
                );
    }

    /**
     * IOExceptionの種類と短い説明を安全にログへ出す。
     */
    private static void logIOException(
            String prefix,
            IOException exception
    ) {

        String reason =
                exception.getMessage() == null
                        ? ""
                        : sanitizeForLog(
                                exception.getMessage()
                        );

        logError(
                prefix
                + "（"
                + exception.getClass().getName()
                + (reason.isEmpty()
                        ? ""
                        : ": " + reason)
                + "）"
        );
    }

    private static void logInfo(
            String message
    ) {

        System.out.println(
                "[Care Shift AI] "
                + message
        );

        AppLog.write(
                "INFO",
                message
        );
    }

    private static void logError(
            String message
    ) {

        System.err.println(
                "[Care Shift AI] "
                + message
        );

        AppLog.write(
                "ERROR",
                message
        );
    }

    /**
     * ログへ表示する文字列を短くし、
     * 改行、タブ、連続空白を除去する。
     */
    private static String sanitizeForLog(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String sanitized =
                value.replaceAll(
                        "[\\r\\n\\t]+",
                        " "
                ).replaceAll(
                        "\\s{2,}",
                        " "
                ).trim();

        if (sanitized.length() > 300) {
            return sanitized.substring(
                    0,
                    300
            );
        }

        return sanitized;
    }

    private static String trimTrailingSlash(
            String value
    ) {

        int end =
                value.length();

        while (end > 0
                && value.charAt(end - 1) == '/') {

            end--;
        }

        return value.substring(
                0,
                end
        );
    }
}