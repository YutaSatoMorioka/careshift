package careshift.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.Task;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S17 負担状況。URL: /careshift/main/Load.action?period=week|month
 * 家族ごとのタスク件数・所要時間を集計して棒グラフにする（初期は今週、今週／今月で切り替え）。
 * 担当なしは別に表示する。所要時間の70%以上かつ3件以上が同じ人に集中していたら、依頼の導線を出す。
 * 集計の対象は、期限が期間内のタスク（完了したものも含む）。割合は「担当ありのタスクの所要時間」に対する比率。
 */
public class LoadAction extends Action {

    private static final DateTimeFormatter MD = DateTimeFormatter.ofPattern("M/d");

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        boolean month = "month".equals(Validator.trim(req.getParameter("period")));
        LocalDate today = LocalDate.now();
        LocalDate from;
        LocalDate to;
        if (month) {
            YearMonth ym = YearMonth.from(today);
            from = ym.atDay(1);
            to = ym.atEndOfMonth();
        } else {
            from = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            to = from.plusDays(6);
        }

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        Map<Long, LoadStat> byUser = new LinkedHashMap<>();
        for (Member m : members) {
            byUser.put(m.getUserId(), new LoadStat(m.getUserId(), m.getDisplayName()));
        }
        LoadStat unassigned = new LoadStat(null, "担当なし");
        int totalTasks = 0;
        int assignedMinutes = 0;
        for (Task t : new TaskDao().findByDueRange(me.getGroupId(), from, to)) {
            totalTasks++;
            LoadStat stat = t.getAssigneeId() == null ? null : byUser.get(t.getAssigneeId());
            if (stat == null) {
                unassigned.add(t.getDurationMin());
            } else {
                stat.add(t.getDurationMin());
                assignedMinutes += t.getDurationMin();
            }
        }

        // 棒の長さ（家族と担当なしで同じ物差し）と、担当あり内での割合
        int maxCount = unassigned.getCount();
        int maxMinutes = unassigned.getMinutes();
        for (LoadStat s : byUser.values()) {
            maxCount = Math.max(maxCount, s.getCount());
            maxMinutes = Math.max(maxMinutes, s.getMinutes());
        }
        List<LoadStat> stats = new ArrayList<>(byUser.values());
        for (LoadStat s : stats) {
            s.setPercent(assignedMinutes == 0 ? 0 : (int) Math.round(s.getMinutes() * 100.0 / assignedMinutes));
            s.setBars(bar(s.getCount(), maxCount), bar(s.getMinutes(), maxMinutes));
        }
        unassigned.setBars(bar(unassigned.getCount(), maxCount), bar(unassigned.getMinutes(), maxMinutes));

        // 集中の判定：所要時間70%以上かつ3件以上が同じ人（家族が2人以上いるとき）
        LoadStat concentrated = null;
        if (members.size() >= 2 && assignedMinutes > 0) {
            for (LoadStat s : stats) {
                if (s.getMinutes() * 10 >= assignedMinutes * 7 && s.getCount() >= 3) {
                    concentrated = s;
                }
            }
        }

        req.setAttribute("period", month ? "month" : "week");
        req.setAttribute("periodLabel", month ? "今月" : "今週");
        req.setAttribute("rangeLabel", from.format(MD) + "〜" + to.format(MD));
        req.setAttribute("stats", stats);
        req.setAttribute("unassigned", unassigned);
        req.setAttribute("totalTasks", totalTasks);
        req.setAttribute("concentrated", concentrated);
        req.getRequestDispatcher("/careshift/main/load.jsp").forward(req, resp);
    }

    private static int bar(int value, int max) {
        return max == 0 ? 0 : (int) Math.round(value * 100.0 / max);
    }
}
