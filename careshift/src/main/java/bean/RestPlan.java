package bean;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 休息予定。
 *
 * rest_plansテーブルと、登録者名・担当者名を
 * 結合して取得した1件分を表す。
 */
public class RestPlan {

    public static final String ADJUSTING = "ADJUSTING";
    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

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

    public RestPlan(
            long id,
            long groupId,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            String content,
            String status,
            long createdBy,
            String creatorName,
            Long coveredBy,
            String coveredName
    ) {

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

    public long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public Long getCoveredBy() {
        return coveredBy;
    }

    public String getCoveredName() {
        return coveredName;
    }

    public boolean isAdjusting() {
        return ADJUSTING.equals(status);
    }

    public boolean isConfirmed() {
        return CONFIRMED.equals(status);
    }

    public boolean isCancelled() {
        return CANCELLED.equals(status);
    }

    public String getStatusLabel() {

        if (isConfirmed()) {
            return "確定";
        }

        if (isCancelled()) {
            return "取消";
        }

        return "調整中";
    }

    public int getMinutes() {
        return (int) Duration.between(start, end).toMinutes();
    }

    public LocalDateTime getStartAt() {
        return LocalDateTime.of(date, start);
    }

    public LocalDateTime getEndAt() {
        return LocalDateTime.of(date, end);
    }

    /**
     * 開始時刻を迎えているか。
     *
     * 開始日時が現在より前、または現在と同じ場合にtrue。
     */
    public boolean isStarted() {
        return !getStartAt().isAfter(LocalDateTime.now());
    }

    /**
     * 休息終了時刻を過ぎているか。
     */
    public boolean isEnded() {
        return !getEndAt().isAfter(LocalDateTime.now());
    }

    /**
     * 例：2026/12/01
     */
    public String getDateLabel() {
        return date.format(DATE_FORMATTER);
    }

    /**
     * 例：10:00〜13:00
     */
    public String getTimeRange() {
        return start.format(TIME_FORMATTER)
                + "〜"
                + end.format(TIME_FORMATTER);
    }

    /**
     * 例：
     * 3時間
     * 1時間30分
     * 45分
     */
    public String getDurationLabel() {

        int minutes = getMinutes();

        if (minutes < 60) {
            return minutes + "分";
        }

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (remainingMinutes == 0) {
            return hours + "時間";
        }

        return hours
                + "時間"
                + remainingMinutes
                + "分";
    }
}