package careshift.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import tool.Validator;

/** S14 休息リクエストの入力フォーム。 */
public class RestForm {

    private String restDate = "";
    private String startTime = "";
    private String endTime = "";
    private String content = "";
    private final List<String> recipients = new ArrayList<>();

    private LocalDate parsedDate;
    private LocalTime parsedStart;
    private LocalTime parsedEnd;
    private List<Long> parsedRecipients = new ArrayList<>();

    public static RestForm blank() {
        return new RestForm();
    }

    public static RestForm fromRequest(HttpServletRequest req) {
        RestForm f = new RestForm();
        f.restDate = Validator.trim(req.getParameter("restDate"));
        f.startTime = Validator.trim(req.getParameter("startTime"));
        f.endTime = Validator.trim(req.getParameter("endTime"));
        f.content = Validator.trim(req.getParameter("restContent"));
        String[] values = req.getParameterValues("recipients");
        if (values != null) {
            for (String v : values) {
                f.recipients.add(Validator.trim(v));
            }
        }
        return f;
    }

    /** 設計書 S14：希望日・開始・終了・内容(300文字以内)・宛先は必須。日時は未来、終了は開始より後。 */
    public Map<String, String> validate(long selfId, Set<Long> memberIds) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (restDate.isEmpty()) {
            errors.put("restDate", "希望日を入力してください");
        } else {
            try {
                parsedDate = LocalDate.parse(restDate);
            } catch (DateTimeParseException e) {
                errors.put("restDate", "希望日は日付の形式で入力してください");
            }
        }
        if (startTime.isEmpty()) {
            errors.put("startTime", "開始時刻を入力してください");
        } else {
            try {
                parsedStart = LocalTime.parse(startTime);
            } catch (DateTimeParseException e) {
                errors.put("startTime", "開始時刻は時刻の形式で入力してください");
            }
        }
        if (endTime.isEmpty()) {
            errors.put("endTime", "終了時刻を入力してください");
        } else {
            try {
                parsedEnd = LocalTime.parse(endTime);
            } catch (DateTimeParseException e) {
                errors.put("endTime", "終了時刻は時刻の形式で入力してください");
            }
        }
        if (parsedStart != null && parsedEnd != null && !parsedEnd.isAfter(parsedStart)) {
            errors.put("endTime", "終了時刻は開始時刻より後にしてください");
        }
        if (parsedDate != null && parsedStart != null && !errors.containsKey("restDate")
                && !LocalDateTime.of(parsedDate, parsedStart).isAfter(LocalDateTime.now())) {
            errors.put("restDate", "休息の日時は現在より後を指定してください");
        }
        if (content.isEmpty()) {
            errors.put("restContent", "希望内容を入力してください");
        } else if (content.length() > 300) {
            errors.put("restContent", "希望内容は300文字以内で入力してください");
        }
        parsedRecipients = TaskSupport.parseRecipients(recipients.toArray(new String[0]), selfId, memberIds, errors);
        return errors;
    }

    public String getRestDate() { return restDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRestContent() { return content; }
    public List<String> getRecipients() { return recipients; }

    public Set<Long> getRecipientIds() {
        Set<Long> ids = new LinkedHashSet<>();
        for (String r : recipients) {
            if (r.matches("\\d{1,18}")) ids.add(Long.valueOf(r));
        }
        return ids;
    }

    public LocalDate dateValue() { return parsedDate; }
    public LocalTime startValue() { return parsedStart; }
    public LocalTime endValue() { return parsedEnd; }
    public String contentValue() { return content; }
    public List<Long> recipientValues() { return parsedRecipients; }
}
