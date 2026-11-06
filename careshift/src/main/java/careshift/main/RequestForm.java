package careshift.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import tool.Options;
import tool.Validator;

/** S12 依頼作成の入力フォーム。画面に戻せるよう、入力は文字列のまま保持する。 */
public class RequestForm {

    private String requestType = "TASK";
    private String content = "";
    private String requestedAt = "";
    private String duration = "";
    private String taskId = "";
    private final List<String> recipients = new ArrayList<>();

    private LocalDateTime parsedRequestedAt;
    private Integer parsedDuration;
    private List<Long> parsedRecipients = new ArrayList<>();

    public static RequestForm blank() {
        return new RequestForm();
    }

    public static RequestForm fromRequest(HttpServletRequest req) {
        RequestForm f = new RequestForm();
        String type = Validator.trim(req.getParameter("requestType"));
        f.requestType = type.isEmpty() ? "TASK" : type;
        f.content = Validator.trim(req.getParameter("content"));
        f.requestedAt = Validator.trim(req.getParameter("requestedAt"));
        f.duration = Validator.trim(req.getParameter("duration"));
        f.taskId = Validator.trim(req.getParameter("taskId"));
        String[] values = req.getParameterValues("recipients");
        if (values != null) {
            for (String v : values) {
                f.recipients.add(Validator.trim(v));
            }
        }
        return f;
    }

    /** 設計書 S12：宛先は自分以外の家族。宛先・依頼内容は必須、希望日時は未来、内容は500文字以内。 */
    public Map<String, String> validate(long selfId, Set<Long> memberIds) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (!Options.REQUEST_TYPES.containsKey(requestType)) {
            errors.put("requestType", "依頼種別を選択してください");
        }
        if (content.isEmpty()) {
            errors.put("content", "依頼内容を入力してください");
        } else if (content.length() > 500) {
            errors.put("content", "依頼内容は500文字以内で入力してください");
        }
        if (requestedAt.isEmpty()) {
            errors.put("requestedAt", "希望日時・期限を入力してください");
        } else {
            try {
                parsedRequestedAt = LocalDateTime.parse(requestedAt);
                if (!parsedRequestedAt.isAfter(LocalDateTime.now())) {
                    errors.put("requestedAt", "希望日時・期限は現在より後の日時を指定してください");
                }
            } catch (DateTimeParseException e) {
                errors.put("requestedAt", "希望日時・期限は日時の形式で入力してください");
            }
        }
        if (!duration.isEmpty()) {
            if (!duration.matches("\\d{1,6}")) {
                errors.put("duration", "所要時間は正の整数（分）で入力してください");
            } else {
                int d = Integer.parseInt(duration);
                if (d < 5 || d > 24 * 60) {
                    errors.put("duration", "所要時間は5分以上24時間（1440分）以内で入力してください");
                } else {
                    parsedDuration = d;
                }
            }
        }
        parsedRecipients = TaskSupport.parseRecipients(recipients.toArray(new String[0]), selfId, memberIds, errors);
        return errors;
    }

    // ---- 画面（JSP）から参照する値 ----
    public String getRequestType() { return requestType; }
    public String getContent() { return content; }
    public String getRequestedAt() { return requestedAt; }
    public String getDuration() { return duration; }
    public String getTaskId() { return taskId; }
    public List<String> getRecipients() { return recipients; }

    /** チェックボックスの選択状態の判定用 */
    public Set<Long> getRecipientIds() {
        Set<Long> ids = new LinkedHashSet<>();
        for (String r : recipients) {
            if (r.matches("\\d{1,18}")) ids.add(Long.valueOf(r));
        }
        return ids;
    }

    public String getRequestedAtLabel() {
        return parsedRequestedAt == null ? requestedAt
                : parsedRequestedAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    // ---- 検証後の値（Action から使う） ----
    public String type() { return requestType; }
    public LocalDateTime requestedAtValue() { return parsedRequestedAt; }
    public Integer durationValue() { return parsedDuration; }
    public List<Long> recipientValues() { return parsedRecipients; }
    public Long taskIdValue() { return taskId.matches("\\d{1,18}") ? Long.valueOf(taskId) : null; }

    // ---- 画面の初期値づくり ----
    void prefill(String content, Integer duration, String requestedAt, String taskId) {
        this.content = content;
        this.duration = duration == null ? "" : String.valueOf(duration);
        this.requestedAt = requestedAt;
        this.taskId = taskId;
    }
}
