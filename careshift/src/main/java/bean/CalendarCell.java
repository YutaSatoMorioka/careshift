package bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** カレンダーの1日分のマス。 */
public class CalendarCell {
    private final LocalDate date;
    private final boolean inMonth;
    private final boolean today;
    private final List<Task> tasks = new ArrayList<>();
    private final List<RestPlan> rests = new ArrayList<>();

    public CalendarCell(LocalDate date, boolean inMonth, boolean today) {
        this.date = date;
        this.inMonth = inMonth;
        this.today = today;
    }

    public LocalDate getDate() { return date; }
    public int getDay() { return date.getDayOfMonth(); }
    public boolean isInMonth() { return inMonth; }
    public boolean isToday() { return today; }
    public List<Task> getTasks() { return tasks; }
    public List<RestPlan> getRests() { return rests; }
}
