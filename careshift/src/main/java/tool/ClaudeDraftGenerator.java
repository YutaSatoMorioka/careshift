package tool;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Claude（Anthropic の Messages API）で下書きを作る実装。
 * 設定（VM 引数 または 環境変数）:
 *   careshift.ai.apikey  / ANTHROPIC_API_KEY   … APIキー（なければ「利用不可」になり、手入力を案内する）
 *   careshift.ai.model                         … モデル名（既定: claude-haiku-5-5）
 *   careshift.ai.baseurl                       … 接続先（既定: https://api.anthropic.com。試験用の差し替え口）
 * 相談文は外部のAIサービスに送られる。ログには相談文を残さない。
 */
public final class ClaudeDraftGenerator implements DraftGenerator {

    private static final Pattern DATE_LINE = Pattern.compile("(?m)^\\s*日時\\s*[:：]\\s*(.*)$");
    private static final Pattern CONTENT_LINE = Pattern.compile("(?ms)^\\s*内容\\s*[:：]\\s*(.*?)(?=^\\s*所要時間\\s*[:：]|\\z)");
    private static final Pattern DURATION_LINE = Pattern.compile("(?m)^\\s*所要時間\\s*[:：]\\s*(\\d{1,4})");
    private static final Pattern DATETIME = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})[ T](\\d{1,2}):(\\d{2})");

    private final String apiKey;
    private final String model;
    private final String baseUrl;

    public ClaudeDraftGenerator(String apiKey, String model, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }

    /** 設定から作る。APIキーがなければ null（＝利用できない）。 */
    public static ClaudeDraftGenerator fromConfig() {
        String key = System.getProperty("careshift.ai.apikey");
        if (key == null || key.isBlank()) key = System.getenv("ANTHROPIC_API_KEY");
        if (key == null || key.isBlank()) return null;
        return new ClaudeDraftGenerator(key.trim(),
                System.getProperty("careshift.ai.model", "claude-haiku-5-5"),
                System.getProperty("careshift.ai.baseurl", "https://api.anthropic.com"));
    }

    @Override
    public Draft generate(String prompt) throws UnavailableException {
        String body = "{\"model\":" + MiniJson.quote(model) + ",\"max_tokens\":600,"
                + "\"system\":" + MiniJson.quote(systemPrompt()) + ","
                + "\"messages\":[{\"role\":\"user\",\"content\":" + MiniJson.quote(prompt) + "}]}";
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/v1/messages"))
                    .timeout(Duration.ofSeconds(25))
                    .header("content-type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new UnavailableException("AIの応答が正常ではありません（" + response.statusCode() + "）");
            }
            String text = MiniJson.firstString(response.body(), "text");
            if (text == null || text.isBlank()) {
                throw new UnavailableException("AIの応答が空でした");
            }
            return parse(text);
        } catch (IOException e) {
            throw new UnavailableException("AIに接続できませんでした");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UnavailableException("AIへの接続が中断されました");
        } catch (IllegalArgumentException e) {
            throw new UnavailableException("AIの接続先の設定が正しくありません");
        }
    }

    /** AIへの指示。下書きだけを作らせ、医療判断や責任分担の決定をさせない。 */
    static String systemPrompt() {
        LocalDate today = LocalDate.now();
        return "あなたは、在宅介護をする家族が、他の家族に送る「依頼文の下書き」を作る係です。\n"
                + "今日は " + today + "（" + today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.JAPANESE) + "）です。\n"
                + "守ること:\n"
                + "- 依頼文の下書きだけを作る。診断・治療・薬・介護方法などの医療的な助言や判断は、絶対にしない。\n"
                + "- 誰が何を担当すべきかを決めつけない。お願いする文面にとどめる。\n"
                + "- 病名・薬名などの医療情報は、依頼文に含めない。\n"
                + "- 日時が読み取れないときは「未定」とする。相対的な表現（明日など）は今日の日付から計算する。\n"
                + "次の形式だけで出力する（前置き・説明は書かない）:\n"
                + "日時: yyyy-MM-dd HH:mm（または 未定）\n"
                + "内容: 依頼文（丁寧で簡潔に、300文字以内）\n"
                + "所要時間: 分を表す整数（わからなければ 0）";
    }

    /** AIの出力を、日時・内容・所要時間に分ける。内容が取れなければ「利用不可」。 */
    static Draft parse(String text) throws UnavailableException {
        Matcher c = CONTENT_LINE.matcher(text);
        String content = c.find() ? c.group(1).trim() : "";
        if (content.isEmpty()) {
            throw new UnavailableException("AIの応答から依頼文を読み取れませんでした");
        }
        if (content.length() > 500) {
            content = content.substring(0, 500);
        }

        String requestedAt = "";
        Matcher d = DATE_LINE.matcher(text);
        if (d.find()) {
            Matcher t = DATETIME.matcher(d.group(1));
            if (t.find()) {
                try {
                    LocalDateTime at = LocalDateTime.of(Integer.parseInt(t.group(1)), Integer.parseInt(t.group(2)),
                            Integer.parseInt(t.group(3)), Integer.parseInt(t.group(4)), Integer.parseInt(t.group(5)));
                    requestedAt = at.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                } catch (RuntimeException ignore) {
                    requestedAt = ""; // 不正な日付は、空にして利用者に入れてもらう
                }
            }
        }

        Integer duration = null;
        Matcher u = DURATION_LINE.matcher(text);
        if (u.find()) {
            int v = Integer.parseInt(u.group(1));
            if (v >= 5 && v <= 1440) duration = v;
        }
        return new Draft(content, requestedAt, duration);
    }
}
