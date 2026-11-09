package careshift.main;

/** 遠方からできるタスクの候補（固定）。 */
public class Suggestion {
    private final String condition;   // CALL / RESERVE / SHOP / INFO
    private final String title;
    private final String description;
    private final int durationMin;
    private final int daysAhead;      // 登録画面の初期の期限（今日から何日後か）

    Suggestion(String condition, String title, String description, int durationMin, int daysAhead) {
        this.condition = condition;
        this.title = title;
        this.description = description;
        this.durationMin = durationMin;
        this.daysAhead = daysAhead;
    }

    public String getCondition() { return condition; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDurationMin() { return durationMin; }
    public int getDaysAhead() { return daysAhead; }
    public String getDueDate() { return java.time.LocalDate.now().plusDays(daysAhead).toString(); }
}
