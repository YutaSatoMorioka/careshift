package careshift.main;

import bean.User;
import dao.NotificationDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/** NOTI001 「既読にする」（1件）。URL: /careshift/main/NotificationRead.action（POST） */
public class NotificationReadAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;

        new NotificationDao().markRead(TaskSupport.parseId(req.getParameter("id")), user.getId());
        String type = Validator.trim(req.getParameter("type"));
        resp.sendRedirect(req.getContextPath() + "/careshift/main/NotificationList.action"
                + (NotificationListAction.TYPES.containsKey(type) && !type.isEmpty() ? "?type=" + type : ""));
    }
}
