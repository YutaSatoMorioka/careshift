package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import tool.Options;

/** 依頼の宛先1人分と、その人の回答。 */
public class RequestRecipient {
    private final long userId;
    private final String name;
    private final String response;      // 未回答は null
    private final String alternative;   // 代替案（なければ空）
    private final LocalDateTime respondedAt;

    public RequestRecipient(long userId, String name, String response, String alternative, LocalDateTime respondedAt) {
        this.userId = userId;
        this.name = name;
        this.response = response;
        this.alternative = alternative == null ? "" : alternative;
        this.respondedAt = respondedAt;
    }

    public long getUserId() { return userId; }
    public String getName() { return name; }
    public String getResponse() { return response; }
    public String getAlternative() { return alternative; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public String getRespondedAtLabel() {
        return respondedAt == null ? "" : respondedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }
    public boolean isAnswered() { return response != null; }
    public String getResponseLabel() { return response == null ? "未回答" : Options.RESPONSES.getOrDefault(response, response); }
}
