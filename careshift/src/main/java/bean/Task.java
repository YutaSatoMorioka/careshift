package bean;

import java.time.LocalDate;

import tool.Options;

/** タスク（tasks と、担当者名・登録者名を結合した1行）。 */
public class Task {
    public static final String TODO = "TODO";
    public static final String DOING = "DOING";
    public static final String DONE = "DONE";

    private final long id;
    private final long groupId;
    private final String title;
    private final LocalDate dueDate;
    private final int durationMin;
    private final String frequency;
    private final Long assigneeId;      // 担当なしは null
    private final String assigneeName;  // 担当なしは null
    private final String status;
    private final long createdBy;
    private final String creatorName;
    private final int version;

    public Task(long id, long groupId, String title, LocalDate dueDate, int durationMin, String frequency,
            Long assigneeId, String assigneeName, String status, long createdBy, String creatorName, int version) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.dueDate = dueDate;
        this.durationMin = durationMin;
        this.frequency = frequency;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.status = status;
        this.createdBy = createdBy;
        this.creatorName = creatorName;
        this.version = version;
    }

    public long getId() { return id; }
    public long getGroupId() { return groupId; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public int getDurationMin() { return durationMin; }
    public String getFrequency() { return frequency; }
    public Long getAssigneeId() { return assigneeId; }
    public String getAssigneeName() { return assigneeName; }
    public String getStatus() { return status; }
    public long getCreatedBy() { return createdBy; }
    public String getCreatorName() { return creatorName; }
    public int getVersion() { return version; }

    public boolean isDone() { return DONE.equals(status); }
    public boolean isUnassigned() { return assigneeId == null; }

    /** 期限を過ぎていて、まだ完了していない */
    public boolean isOverdue() { return !isDone() && dueDate.isBefore(LocalDate.now()); }

    public String getStatusLabel() { return Options.STATUSES.getOrDefault(status, status); }
    public String getFrequencyLabel() { return Options.FREQUENCIES.getOrDefault(frequency, frequency); }

    /** 例: 30分 / 1時間 / 1時間30分 */
    public String getDurationLabel() {
        if (durationMin < 60) {
            return durationMin + "分";
        }
        int h = durationMin / 60;
        int m = durationMin % 60;
        return m == 0 ? h + "時間" : h + "時間" + m + "分";
    }

    /** 編集・削除できるのは、登録者・担当者・主介護者（設計書 S08-2 / S09） */
    public boolean canEdit(long userId, boolean primary) {
        return primary || createdBy == userId || (assigneeId != null && assigneeId == userId);
    }
}
