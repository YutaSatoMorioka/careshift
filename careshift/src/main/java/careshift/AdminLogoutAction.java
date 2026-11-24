package careshift;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;

/**
 * 旧管理者ログアウトURLとの互換用Action。
 *
 * 旧careshiftAdmin属性だけを削除し、
 * 通常のCare Shiftホームへ移動する。
 *
 * Care Shift自体からログアウトする場合は、
 * /careshift/Logout.actionを使用する。
 */
public class AdminLogoutAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        HttpSession session =
                req.getSession(false);

        if (session != null) {
            session.removeAttribute(
                    "careshiftAdmin"
            );
        }

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Menu.action"
        );
    }
}