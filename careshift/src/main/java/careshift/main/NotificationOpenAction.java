package careshift.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.Member;
import bean.Notification;
import bean.RequestCard;
import bean.User;
import dao.NotificationDao;
import dao.RequestDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 通知を選んで、対象の画面を開く（同時に既読にする）。URL: /careshift/main/NotificationOpen.action?id=...
 * 対象のタスク・依頼がすでにない場合は、その旨を案内して通知一覧へ戻す。
 */
public class NotificationOpenAction extends Action {

    private static final Pattern TASK_LINK = Pattern.compile("^/careshift/main/TaskDetail\\.action\\?id=(\\d{1,18})$");
    private static final Pattern REQUEST_LINK = Pattern.compile("^/careshift/main/RequestDetail\\.action\\?id=(\\d{1,18})$");

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String listUrl = req.getContextPath() + "/careshift/main/NotificationList.action";
        NotificationDao dao = new NotificationDao();
        Notification n = dao.findOwn(TaskSupport.parseId(req.getParameter("id")), user.getId());
        if (n == null) {
            flashError(req, "通知が見つかりません。");
            resp.sendRedirect(listUrl);
            return;
        }
        dao.markRead(n.getId(), user.getId()); // 開いたら既読にする

        String link = n.getLinkPath();
        // アプリ内の道筋だけを開く（外部URLなどへ飛ばさない）
        if (!link.startsWith("/careshift/") || link.contains("//") || link.contains("\\") || link.contains(":")) {
            resp.sendRedirect(listUrl);
            return;
        }
        if (isTargetMissing(link, me, user)) {
            flashError(req, "この通知の対象（タスクまたは依頼）は削除されたか、見つかりません。");
            resp.sendRedirect(listUrl);
            return;
        }
        resp.sendRedirect(req.getContextPath() + link);
    }

    /** 通知の開く先が、すでに存在しない（削除済み）か。 */
    private boolean isTargetMissing(String link, Member me, User user) throws Exception {
        Matcher task = TASK_LINK.matcher(link);
        if (task.matches()) {
            return new TaskDao().find(Long.parseLong(task.group(1)), me.getGroupId()) == null;
        }
        Matcher request = REQUEST_LINK.matcher(link);
        if (request.matches()) {
            RequestCard card = new RequestDao().find(Long.parseLong(request.group(1)), me.getGroupId());
            return card == null || !RequestDetailAction.isParticipant(card, user.getId());
        }
        return false;
    }
}
