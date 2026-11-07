package careshift.main;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bean.Member;
import bean.RestPlan;
import bean.User;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S15 休息レポート。URL: /careshift/main/RestReport.action?targetMonth=2026-11
 * 対象月に「確定」した休息の取得回数と合計時間を集計する。調整中・取消は実績に含めず、別に表示する。
 * PDF出力は、印刷画面から「PDFとして保存」する方式（印刷用の表示に切り替わる）。
 */
public class RestReportAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        YearMonth ym;
        try {
            ym = YearMonth.parse(Validator.trim(req.getParameter("targetMonth")));
        } catch (DateTimeParseException e) {
            ym = YearMonth.now(); // 初期値は当月
        }

        RestDao dao = new RestDao();
        List<RestPlan> inMonth = dao.findByDateRange(me.getGroupId(), ym.atDay(1), ym.atEndOfMonth());
        List<RestPlan> confirmed = new ArrayList<>();
        int adjusting = 0;
        int cancelled = 0;
        int upcoming = 0;
        int totalMinutes = 0;
        for (RestPlan p : inMonth) {
            if (p.isConfirmed()) {
                confirmed.add(p);
                totalMinutes += p.getMinutes();
                if (!p.isStarted()) upcoming++;
            } else if (p.isCancelled()) {
                cancelled++;
            } else {
                adjusting++;
            }
        }

        // 直近6か月の推移（確定のみ）
        YearMonth first = ym.minusMonths(5);
        List<RestPlan> span = dao.findByDateRange(me.getGroupId(), first.atDay(1), ym.atEndOfMonth());
        int[] counts = new int[6];
        int[] minutes = new int[6];
        int maxMinutes = 0;
        for (RestPlan p : span) {
            if (!p.isConfirmed()) continue;
            int idx = (int) java.time.temporal.ChronoUnit.MONTHS.between(first, YearMonth.from(p.getDate()));
            counts[idx]++;
            minutes[idx] += p.getMinutes();
        }
        for (int m : minutes) maxMinutes = Math.max(maxMinutes, m);
        List<MonthStat> stats = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth m = first.plusMonths(i);
            stats.add(new MonthStat(m.getYear() + "年" + m.getMonthValue() + "月", counts[i], minutes[i],
                    maxMinutes == 0 ? 0 : (int) Math.round(minutes[i] * 100.0 / maxMinutes)));
        }

        req.setAttribute("targetMonth", ym.toString());
        req.setAttribute("monthLabel", ym.getYear() + "年" + ym.getMonthValue() + "月");
        req.setAttribute("prevMonth", ym.minusMonths(1).toString());
        req.setAttribute("nextMonth", ym.plusMonths(1).toString());
        req.setAttribute("confirmed", confirmed);
        req.setAttribute("confirmedCount", confirmed.size());
        req.setAttribute("totalMinutesLabel", MonthStat.format(totalMinutes));
        req.setAttribute("upcomingCount", upcoming);
        req.setAttribute("adjustingCount", adjusting);
        req.setAttribute("cancelledCount", cancelled);
        req.setAttribute("stats", stats);
        req.setAttribute("today", LocalDate.now().toString());
        req.getRequestDispatcher("/careshift/main/rest_report.jsp").forward(req, resp);
    }
}
