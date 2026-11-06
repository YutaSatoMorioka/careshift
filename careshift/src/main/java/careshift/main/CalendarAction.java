package careshift.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.CalendarCell;
import bean.Member;
import bean.Task;
import bean.User;
import bean.RestPlan;
import dao.RestDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S10 カレンダー。URL: /careshift/main/Calendar.action?month=2026-11
 * 月単位でタスクの期限・担当者を表示する。表示月の切替（前月・翌月・月の選択）に対応。
 * 休息予定（調整中／確定／取消）も表示する。休息の取消は主介護者のみ（確認後に状態を更新し、担当者へ通知）。
 */
public class CalendarAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        YearMonth ym;
        try {
            ym = YearMonth.parse(Validator.trim(req.getParameter("month")));
        } catch (DateTimeParseException e) {
            ym = YearMonth.now(); // 指定がない・形式が不正なときは当月
        }

        List<List<CalendarCell>> weeks = buildWeeks(ym);
        LocalDate from = weeks.get(0).get(0).getDate();
        List<CalendarCell> lastWeek = weeks.get(weeks.size() - 1);
        LocalDate to = lastWeek.get(lastWeek.size() - 1).getDate();

        Map<LocalDate, CalendarCell> byDate = new HashMap<>();
        for (List<CalendarCell> week : weeks) {
            for (CalendarCell c : week) {
                byDate.put(c.getDate(), c);
            }
        }
        for (Task t : new TaskDao().findByDueRange(me.getGroupId(), from, to)) {
            byDate.get(t.getDueDate()).getTasks().add(t);
        }

        // 休息予定（調整中／確定／取消）も同じマスに載せる
        for (RestPlan rp : new RestDao().findByDateRange(me.getGroupId(), from, to)) {
            byDate.get(rp.getDate()).getRests().add(rp);
        }

        req.setAttribute("isPrimary", me.isPrimary());
        req.setAttribute("month", ym.toString());
        req.setAttribute("monthLabel", ym.getYear() + "年" + ym.getMonthValue() + "月");
        req.setAttribute("prevMonth", ym.minusMonths(1).toString());
        req.setAttribute("nextMonth", ym.plusMonths(1).toString());
        req.setAttribute("weeks", weeks);
        req.setAttribute("weekdays", List.of("日", "月", "火", "水", "木", "金", "土"));
        req.getRequestDispatcher("/careshift/main/calendar.jsp").forward(req, resp);
    }

    /** 日曜始まりで、表示月を含む週を並べる（前後の月の日も埋める）。 */
    static List<List<CalendarCell>> buildWeeks(YearMonth ym) {
        LocalDate today = LocalDate.now();
        LocalDate first = ym.atDay(1);
        LocalDate day = first.minusDays(first.getDayOfWeek().getValue() % 7); // 日曜(7)→0
        LocalDate last = ym.atEndOfMonth();

        List<List<CalendarCell>> weeks = new ArrayList<>();
        while (!day.isAfter(last) || day.getDayOfWeek() != DayOfWeek.SUNDAY) {
            List<CalendarCell> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(new CalendarCell(day, YearMonth.from(day).equals(ym), day.equals(today)));
                day = day.plusDays(1);
            }
            weeks.add(week);
        }
        return weeks;
    }
}
