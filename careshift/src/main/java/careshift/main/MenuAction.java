package careshift.main;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/** S06 ホーム（メニュー）。URL: /careshift/main/Menu.action */
public class MenuAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.getRequestDispatcher("/careshift/main/menu.jsp").forward(req, resp);
    }
}
