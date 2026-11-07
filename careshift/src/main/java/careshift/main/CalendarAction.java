package careshift.main;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bean.Member;
import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CalendarAction extends FeatureActionSupport {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Member me = requireMember(req, resp);
        if (me == null) return;

        LocalDate month;
        try {
            String raw = value(req, "month");
            String yearMonth = raw.isEmpty()
                    ? LocalDate.now().toString().substring(0, 7)
                    : raw;
            month = LocalDate.parse(yearMonth + "-01");
        } catch (Exception e) {
            month = LocalDate.now().withDayOfMonth(1);
        }

        CareFeatureDao dao = new CareFeatureDao();
        var tasks = dao.tasks(
                me.getGroupId(),
                month.atStartOfDay(),
                month.plusMonths(1).atStartOfDay());

        Map<LocalDate, List<Map<String, Object>>> byDay = new LinkedHashMap<>();
        for (var task : tasks) {
            Object due = task.get("due_at");
            LocalDate day = due instanceof Timestamp ts
                    ? ts.toLocalDateTime().toLocalDate()
                    : LocalDate.parse(String.valueOf(due).substring(0, 10));
            byDay.computeIfAbsent(day, k -> new ArrayList<>()).add(task);
        }

        LocalDate first = month.minusDays(month.getDayOfWeek().getValue() % 7);
        List<List<Map<String, Object>>> weeks = new ArrayList<>();

        for (int w = 0; w < 6; w++) {
            List<Map<String, Object>> week = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                LocalDate date = first.plusDays(w * 7L + d);
                Map<String, Object> cell = new LinkedHashMap<>();
                cell.put("date", date);
                cell.put("day", date.getDayOfMonth());
                cell.put("inMonth", date.getMonth() == month.getMonth());
                cell.put("tasks", byDay.getOrDefault(date, List.of()));
                week.add(cell);
            }
            weeks.add(week);
        }

        req.setAttribute("month", month.toString().substring(0, 7));
        req.setAttribute("weeks", weeks);
        req.getRequestDispatcher("/careshift/main/calendar.jsp").forward(req, resp);
    }
}