package careshift.main;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.Task;
import jakarta.servlet.http.HttpServletRequest;
import tool.Options;
import tool.Validator;

/**
 * タスク登録（S08）・編集（S08-2）で共通の入力フォーム。
 * 設計書: 「入力検証は登録画面と共通」。画面に値を戻すため、入力はすべて文字列のまま保持する。
 */
public class TaskForm {

    /** 病名・薬名らしい語（見つかったら医療情報を入力しないよう注意を出す） */
    private static final String[] MEDICAL_WORDS = {
        "薬", "錠", "カプセル", "mg", "インスリン", "処方", "診断", "病名", "糖尿", "高血圧", "認知症",
        "がん", "癌", "心不全", "パーキンソン", "脳梗塞", "脳卒中", "うつ病", "肺炎", "骨粗鬆"
    };

    private String title = "";
    private String dueDate = "";
    private String duration = "";
    private String frequency = "ONCE";
    private String assignee = "";
    private String status = "TODO";
    private String version = "";

    // 検証に成功したあとの値
    private LocalDate parsedDueDate;
    private int parsedDuration;
    private Long parsedAssignee;

    public static TaskForm fromRequest(HttpServletRequest req) {
        TaskForm f = new TaskForm();
        f.title = Validator.trim(req.getParameter("taskTitle"));
        f.dueDate = Validator.trim(req.getParameter("dueDate"));
        f.duration = Validator.trim(req.getParameter("duration"));
        String freq = Validator.trim(req.getParameter("frequency"));
        f.frequency = freq.isEmpty() ? "ONCE" : freq;
        f.assignee = Validator.trim(req.getParameter("assignee"));
        f.status = Validator.trim(req.getParameter("status"));
        f.version = Validator.trim(req.getParameter("version"));
        return f;
    }

    public static TaskForm fromTask(Task t) {
        TaskForm f = new TaskForm();
        f.title = t.getTitle();
        f.dueDate = t.getDueDate().toString();
        f.duration = String.valueOf(t.getDurationMin());
        f.frequency = t.getFrequency();
        f.assignee = t.getAssigneeId() == null ? "" : String.valueOf(t.getAssigneeId());
        f.status = t.getStatus();
        f.version = String.valueOf(t.getVersion());
        return f;
    }

    /** 新規登録画面の初期値（期限は今日） */
    public static TaskForm blank() {
        TaskForm f = new TaskForm();
        f.dueDate = LocalDate.now().toString();
        return f;
    }

    /**
     * 入力チェック。エラーは項目名(name属性)をキーに返す。
     * @param withStatus 状態（必須）も検証するか（編集画面）
     * @param memberIds  担当者に選べるユーザーID（グループのメンバー）
     */
    public Map<String, String> validate(boolean withStatus, Set<Long> memberIds) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (title.isEmpty()) {
            errors.put("taskTitle", "タスク内容を入力してください");
        } else if (title.length() > 100) {
            errors.put("taskTitle", "タスク内容は100文字以内で入力してください");
        }

        if (dueDate.isEmpty()) {
            errors.put("dueDate", "期限を入力してください");
        } else {
            try {
                parsedDueDate = LocalDate.parse(dueDate);
            } catch (DateTimeParseException e) {
                errors.put("dueDate", "期限は日付の形式（例: 2026-11-30）で入力してください");
            }
        }

        if (duration.isEmpty()) {
            errors.put("duration", "所要時間（分）を入力してください");
        } else if (!duration.matches("\\d{1,6}")) {
            errors.put("duration", "所要時間は正の整数（分）で入力してください");
        } else {
            parsedDuration = Integer.parseInt(duration);
            if (parsedDuration < 5 || parsedDuration > 24 * 60) {
                errors.put("duration", "所要時間は5分以上24時間（1440分）以内で入力してください");
            }
        }

        if (!Options.FREQUENCIES.containsKey(frequency)) {
            errors.put("frequency", "頻度の選択が正しくありません");
        }

        if (!assignee.isEmpty()) {
            if (assignee.matches("\\d{1,18}") && memberIds.contains(Long.parseLong(assignee))) {
                parsedAssignee = Long.parseLong(assignee);
            } else {
                errors.put("assignee", "担当者はグループのメンバーから選んでください");
            }
        }

        if (withStatus && !Options.STATUSES.containsKey(status)) {
            errors.put("status", "状態を選択してください");
        }
        return errors;
    }

    /**
     * 登録前の確認事項（設計書の「確認後に登録可」「注意を表示」）。
     * before があれば編集：期限・内容を変えていない場合は、同じ注意を繰り返さない。
     */
    public List<String> warnings(Task before) {
        List<String> list = new ArrayList<>();
        if (parsedDueDate != null && parsedDueDate.isBefore(LocalDate.now())
                && (before == null || !parsedDueDate.equals(before.getDueDate()))) {
            list.add("期限が過去の日付になっています。");
        }
        if (looksMedical(title) && (before == null || !title.equals(before.getTitle()))) {
            list.add("病名・薬名などの医療情報は入力しないでください。タスク内容には「何をするか」だけを書きましょう。");
        }
        return list;
    }

    static boolean looksMedical(String text) {
        String lower = text.toLowerCase();
        for (String w : MEDICAL_WORDS) {
            if (lower.contains(w)) {
                return true;
            }
        }
        return false;
    }

    // ---- 画面（JSP）から参照する値 ----
    public String getTaskTitle() { return title; }
    public String getDueDate() { return dueDate; }
    public String getDuration() { return duration; }
    public String getFrequency() { return frequency; }
    public String getAssignee() { return assignee; }
    public String getStatus() { return status; }
    public String getVersion() { return version; }

    /** select の選択状態の判定用（数字でなければ null） */
    public Long getAssigneeId() {
        return assignee.matches("\\d{1,18}") ? Long.valueOf(assignee) : null;
    }

    // ---- 検証後の値（Action から使う） ----
    public String title() { return title; }
    public LocalDate dueDateValue() { return parsedDueDate; }
    public int durationValue() { return parsedDuration; }
    public String frequencyValue() { return frequency; }
    public Long assigneeValue() { return parsedAssignee; }
    public String statusValue() { return status; }
    public int versionValue() {
        return version.matches("\\d{1,9}") ? Integer.parseInt(version) : -1; // -1 は必ず競合になる
    }
}
