package careshift.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bean.Member;
import bean.Task;
import dao.TaskDao;
import tool.Validator;

/**
 * S19 タスク分担表の中身を作る（画面のプレビューとPDF出力で共通）。
 * 期間（今週／今月／全て）・担当者・並び順（担当者別／期限順）を指定する。
 * 担当なしは「担当なし」と明記し、医療情報らしい語を含む内容は出力しない。0件は出力不可、200件超は絞り込みを案内。
 */
public final class TaskSheet {

    public static final int MAX_ROWS = 200;
    public static final String MASKED_TITLE = "（内容は非表示）";

    /** 表の1行 */
    public static final class Row {
        private final String title;
        private final String assignee;
        private final String due;
        private final String duration;
        private final String status;

        Row(String title, String assignee, String due, String duration, String status) {
            this.title = title;
            this.assignee = assignee;
            this.due = due;
            this.duration = duration;
            this.status = status;
        }

        public String getTitle() { return title; }
        public String getAssignee() { return assignee; }
        public String getDue() { return due; }
        public String getDuration() { return duration; }
        public String getStatus() { return status; }
    }

    private final List<Row> rows = new ArrayList<>();
    private String period = "week";
    private String order = "assignee";
    private String assignee = "";
    private int maskedCount;
    private String blockedReason = "";

    public static TaskSheet build(TaskDao dao, long groupId, String periodParam, String assigneeParam,
            String orderParam, List<Member> members) throws Exception {
        TaskSheet sheet = new TaskSheet();
        sheet.period = "month".equals(periodParam) || "all".equals(periodParam) ? periodParam : "week";
        sheet.order = "due".equals(orderParam) ? "due" : "assignee";
        String a = Validator.trim(assigneeParam);
        boolean assigneeValid = "none".equals(a) || (a.matches("\\d{1,18}")
                && members.stream().anyMatch(m -> m.getUserId() == Long.parseLong(a)));
        sheet.assignee = assigneeValid ? a : "";

        LocalDate today = LocalDate.now();
        List<Task> tasks;
        if ("all".equals(sheet.period)) {
            tasks = dao.search(groupId, "", null, null, "");
        } else if ("month".equals(sheet.period)) {
            YearMonth ym = YearMonth.from(today);
            tasks = dao.findByDueRange(groupId, ym.atDay(1), ym.atEndOfMonth());
        } else {
            LocalDate from = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            tasks = dao.findByDueRange(groupId, from, from.plusDays(6));
        }

        List<Task> picked = new ArrayList<>();
        for (Task t : tasks) {
            if ("none".equals(sheet.assignee) && t.getAssigneeId() != null) continue;
            if (sheet.assignee.matches("\\d+") && (t.getAssigneeId() == null
                    || t.getAssigneeId() != Long.parseLong(sheet.assignee))) continue;
            picked.add(t);
        }

        Comparator<Task> byDue = Comparator.comparing(Task::getDueDate).thenComparing(Task::getId);
        if ("assignee".equals(sheet.order)) {
            // 担当者名順（担当なしは最後）→ 期限順
            picked.sort(Comparator
                    .comparing((Task t) -> t.getAssigneeName() == null ? "\uFFFF" : t.getAssigneeName())
                    .thenComparing(byDue));
        } else {
            picked.sort(byDue);
        }

        for (Task t : picked) {
            String title = t.getTitle();
            if (TaskForm.looksMedical(title)) { // 医療情報は出力しない
                title = MASKED_TITLE;
                sheet.maskedCount++;
            }
            sheet.rows.add(new Row(title, t.getAssigneeName() == null ? "担当なし" : t.getAssigneeName(),
                    t.getDueDate().toString(), t.getDurationLabel(), t.getStatusLabel()));
        }

        if (sheet.rows.isEmpty()) {
            sheet.blockedReason = "出力できるタスクがありません。期間や担当者を変えてください。";
        } else if (sheet.rows.size() > MAX_ROWS) {
            sheet.blockedReason = "タスクが" + MAX_ROWS + "件を超えています（" + sheet.rows.size()
                    + "件）。期間や担当者を絞り込んでください。";
        }
        return sheet;
    }

    public List<Row> getRows() { return rows; }
    public String getPeriod() { return period; }
    public String getOrder() { return order; }
    public String getAssignee() { return assignee; }
    public int getMaskedCount() { return maskedCount; }
    public String getBlockedReason() { return blockedReason; }
    public boolean isBlocked() { return !blockedReason.isEmpty(); }

    public String getPeriodLabel() {
        return "month".equals(period) ? "今月" : "all".equals(period) ? "全て" : "今週";
    }

    public String getOrderLabel() { return "due".equals(order) ? "期限順" : "担当者別"; }
}
