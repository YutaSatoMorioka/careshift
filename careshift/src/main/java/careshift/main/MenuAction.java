package careshift.main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import bean.Member;
import bean.Notification;
import bean.Task;
import bean.User;
import dao.GroupDao;
import dao.NotificationDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S06 ホーム。URL: /careshift/main/Menu.action
 * 今週（月〜日）のタスク状況と、未読通知を表示する。各画面への主要導線とおすすめコラムへの導線もここに置く。
 */
public class MenuAction extends Action {

    private static final DateTimeFormatter MD = DateTimeFormatter.ofPattern("M/d");

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        Member me = new GroupDao().findMember(user.getId());
        req.setAttribute("member", me); // グループ未所属ならホームで案内する

        if (me != null) {
            ReminderSupport.generate(user, me); // 期限リマインドを（なければ）作る
            try {
                loadWeek(req, user, me);
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("weekError", Boolean.TRUE);
            }
        }

        // 未読通知（読み込みに失敗してもホーム自体は表示する）
        try {
            NotificationDao dao = new NotificationDao();
            List<Notification> unread = dao.findUnread(user.getId(), 5);
            req.setAttribute("unreadList", unread);
            req.setAttribute("unreadCount", dao.countUnread(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("notificationError", Boolean.TRUE);
        }
        req.getRequestDispatcher("/careshift/main/menu.jsp").forward(req, resp);
    }

    /** 今週のタスク状況（件数）と、自分の担当で未完了のタスク。 */
    private void loadWeek(HttpServletRequest req, User user, Member me) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate from = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate to = from.plusDays(6);
        TaskDao dao = new TaskDao();

        int todo = 0, doing = 0, done = 0;
        List<Task> mine = new ArrayList<>();
        for (Task t : dao.findByDueRange(me.getGroupId(), from, to)) {
            switch (t.getStatus()) {
                case Task.DONE: done++; break;
                case Task.DOING: doing++; break;
                default: todo++;
            }
            if (!t.isDone() && t.getAssigneeId() != null && t.getAssigneeId() == user.getId()) {
                mine.add(t);
            }
        }
        int overdue = 0;
        for (Task t : dao.search(me.getGroupId(), "", null, today.minusDays(1), "")) {
            if (!t.isDone()) overdue++;
        }

        req.setAttribute("weekLabel", from.format(MD) + "〜" + to.format(MD));
        req.setAttribute("weekTotal", todo + doing + done);
        req.setAttribute("todoCount", todo);
        req.setAttribute("doingCount", doing);
        req.setAttribute("doneCount", done);
        req.setAttribute("overdueCount", overdue);
        req.setAttribute("myTasks", mine);
    }
}
