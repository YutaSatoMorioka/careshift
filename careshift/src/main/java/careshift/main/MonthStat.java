package careshift.main;

/** 休息レポートの月ごとの集計（確定した休息の回数と合計時間）。 */
public class MonthStat {
    private final String label;
    private final int count;
    private final int minutes;
    private final int percent; // グラフの棒の長さ（最大の月を100とする）

    public MonthStat(String label, int count, int minutes, int percent) {
        this.label = label;
        this.count = count;
        this.minutes = minutes;
        this.percent = percent;
    }

    public String getLabel() { return label; }
    public int getCount() { return count; }
    public int getMinutes() { return minutes; }
    public int getPercent() { return percent; }

    /** 例: 4時間30分 / 45分 / 0分 */
    public String getMinutesLabel() { return format(minutes); }

    static String format(int m) {
        if (m < 60) return m + "分";
        return m % 60 == 0 ? (m / 60) + "時間" : (m / 60) + "時間" + (m % 60) + "分";
    }
}
