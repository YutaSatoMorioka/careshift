package careshift;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;

/** Actionクラス共通のログイン確認。 */
public abstract class ActionSupport extends Action {
    protected boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return false;
        }
        return true;
    }
    protected boolean isPrimaryCaregiver(HttpServletRequest req) {
        return Boolean.TRUE.equals(req.getSession(false).getAttribute("primaryCaregiver"));
    }
}

