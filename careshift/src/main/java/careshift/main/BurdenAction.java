package careshift.main;

import bean.Member;
import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧「負担状況」URLとの互換用。
 *
 * 正式な負担状況画面は LoadAction で表示する。
 *
 * 旧URL:
 * /careshift/main/Burden.action
 *
 * 正式URL:
 * /careshift/main/Load.action
 */
public class BurdenAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        User user = requireLogin(req, resp);
        if (user == null) {
            return;
        }

        Member me = requireMember(req, resp, user);
        if (me == null) {
            return;
        }

        String period = req.getParameter("period");

        String redirectUrl =
                req.getContextPath()
                + "/careshift/main/Load.action";

        if ("month".equals(period)) {
            redirectUrl += "?period=month";
        } else if ("week".equals(period)) {
            redirectUrl += "?period=week";
        }

        resp.sendRedirect(redirectUrl);
    }
}