package tool;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Gemini API を使って、依頼の下書きを作る。 */
public final class GeminiDraftGenerator implements DraftGenerator {

    /** 混雑（503）・回数制限（429）のときに、やり直す最大回数（最初の1回を含む） */
    private static final int MAX_ATTEMPTS = 3;

    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public GeminiDraftGenerator(String apiKey, String model, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    /** 環境変数またはVM引数から作る。キーがなければ null。 */
    public static GeminiDraftGenerator fromConfig() {
        String key = System.getProperty("careshift.ai.gemini.apikey");
        if (key == null || key.isBlank()) key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) return null;

        String model = System.getProperty("careshift.ai.gemini.model", "gemini-3.8-flash");
        String baseUrl = System.getProperty("careshift.ai.gemini.baseurl", "https://generativelanguage.googleapis.com");
        return new GeminiDraftGenerator(key.trim(), model.trim(), baseUrl.trim());
    }

    @Override
    public Draft generate(String prompt) throws UnavailableException {
        // 出力枠は大きめにとる（モデルが答える前に「考える」分も、この枠に数えられるため）。実際の出力は短い。
        String body = "{\"systemInstruction\":{\"parts\":[{\"text\":"
                + MiniJson.quote(ClaudeDraftGenerator.systemPrompt())
                + "}]},\"contents\":[{\"role\":\"user\",\"parts\":[{\"text\":"
                + MiniJson.quote(prompt)
                + "}]}],\"generationConfig\":{\"maxOutputTokens\":4096}}";

        try {
            String encodedModel = URLEncoder.encode(model, StandardCharsets.UTF_8).replace("+", "%20");
            String endpoint = trimTrailingSlash(baseUrl) + "/v1beta/models/" + encodedModel + ":generateContent";
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(60))
                    .header("content-type", "application/json; charset=utf-8")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = null;
            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                int s = response.statusCode();
                if (s != 503 && s != 429) break;          // 混雑・回数制限のときだけ、やり直す
                if (attempt < MAX_ATTEMPTS) Thread.sleep(1500L * attempt);
            }

            if (response.statusCode() != 200) {
                AppLog.write("ERROR", "Gemini APIの応答エラー（HTTP " + response.statusCode() + "）");
                throw new UnavailableException("AIの応答が正常ではありません（" + response.statusCode() + "）");
            }
            String text = MiniJson.firstString(response.body(), "text");
            if (text == null || text.isBlank()) {
                AppLog.write("ERROR", "Gemini APIの応答に生成文章がありません");
                throw new UnavailableException("AIの応答から文章を読み取れませんでした");
            }
            return ClaudeDraftGenerator.parse(text);
        } catch (IOException e) {
            // 原因を調べられるよう、例外の種類と理由を記録する（キーも相談文も含まれない）
            AppLog.write("ERROR", "Gemini APIへの通信に失敗しました（"
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + "）");
            throw new UnavailableException("AIに接続できませんでした");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLog.write("ERROR", "Gemini APIへの通信が中断されました");
            throw new UnavailableException("AIへの接続が中断されました");
        } catch (IllegalArgumentException e) {
            AppLog.write("ERROR", "Gemini APIの接続先設定が不正です");
            throw new UnavailableException("AIの接続先の設定が正しくありません");
        }
    }

    private static String trimTrailingSlash(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') end--;
        return value.substring(0, end);
    }
}
