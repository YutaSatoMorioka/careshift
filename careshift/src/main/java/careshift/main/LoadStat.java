package careshift.main;

/** 負担状況の、家族1人分（または「担当なし」）の集計。 */
public class LoadStat {
    private final Long userId;      // 担当なしは null
    private final String name;
    private int count;
    private int minutes;
    private int percent;            // 担当ありの合計時間に対する割合（担当なしは 0）
    private int countBar;           // グラフの棒の長さ（最大を100とする）
    private int minutesBar;

    LoadStat(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    void add(int minutes) {
        this.count++;
        this.minutes += minutes;
    }

    void setPercent(int percent) { this.percent = percent; }
    void setBars(int countBar, int minutesBar) { this.countBar = countBar; this.minutesBar = minutesBar; }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public int getCount() { return count; }
    public int getMinutes() { return minutes; }
    public int getPercent() { return percent; }
    public int getCountBar() { return countBar; }
    public int getMinutesBar() { return minutesBar; }
    public boolean isUnassigned() { return userId == null; }

    /** 例: 4時間30分 / 45分 / 0分 */
    public String getMinutesLabel() { return MonthStat.format(minutes); }
}
