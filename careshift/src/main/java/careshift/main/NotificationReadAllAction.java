package careshift.main;

import bean.User;
import dao.NotificationDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/** NOTI001 「すべて既読にする」。URL: /careshift/main/NotificationReadAll.action（POST） */
public class NotificationReadAllAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;

        int n = new NotificationDao().markAllRead(user.getId());
        flash(req, n == 0 ? "未読の通知はありません。" : n + "件の通知を既読にしました。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/NotificationList.action");
    }
}
