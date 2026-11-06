package bean;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** 休息予定（rest_plans と、登録者名・担当者名を結合した1行）。 */
public class RestPlan {
    public static final String ADJUSTING = "ADJUSTING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    private final long id;
    private final long groupId;
    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private final String content;
    private final String status;
    private final long createdBy;
    private final String creatorName;
    private final Long coveredBy;
    private final String coveredName;

    public RestPlan(long id, long groupId, LocalDate date, LocalTime start, LocalTime end, String content,
            String status, long createdBy, String creatorName, Long coveredBy, String coveredName) {
        this.id = id;
        this.groupId = groupId;
        this.date = date;
        this.start = start;
        this.end = end;
        this.content = content;
        this.status = status;
        this.createdBy = createdBy;
        this.creatorName = creatorName;
        this.coveredBy = coveredBy;
        this.coveredName = coveredName;
    }

    public long getId() { return id; }
    public long getGroupId() { return groupId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public long getCreatedBy() { return createdBy; }
    public String getCreatorName() { return creatorName; }
    public Long getCoveredBy() { return coveredBy; }
    public String getCoveredName() { return coveredName; }

    public boolean isAdjusting() { return ADJUSTING.equals(status); }
    public boolean isConfirmed() { return CONFIRMED.equals(status); }
    public boolean isCancelled() { return CANCELLED.equals(status); }

    public String getStatusLabel() {
        if (isConfirmed()) return "確定";
        if (isCancelled()) return "取消";
        return "調整中";
    }

    public int getMinutes() { return (int) Duration.between(start, end).toMinutes(); }

    public LocalDateTime getStartAt() { return LocalDateTime.of(date, start); }
    public LocalDateTime getEndAt() { return LocalDateTime.of(date, end); }

    /** 開始時刻を過ぎている */
    public boolean isStarted() { return getStartAt().isBefore(LocalDateTime.now()); }

    /** 例: 2026/12/01 */
    public String getDateLabel() { return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); }

    /** 例: 10:00〜13:00 */
    public String getTimeRange() { return start + "〜" + end; }

    /** 例: 3時間 / 1時間30分 / 45分 */
    public String getDurationLabel() {
        int m = getMinutes();
        if (m < 60) return m + "分";
        return m % 60 == 0 ? (m / 60) + "時間" : (m / 60) + "時間" + (m % 60) + "分";
    }
}
