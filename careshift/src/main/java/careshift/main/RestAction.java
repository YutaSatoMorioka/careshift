package careshift.main;

import bean.Member;
import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧休息画面URLとの互換用Action。
 *
 * 旧URL:
 * /careshift/main/Rest.action
 *
 * 正式URL:
 * /careshift/main/RestRequest.action
 *
 * 旧rest_records方式では登録せず、
 * rest_plans・requests方式の正式画面へ移動する。
 */
public class RestAction extends Action {

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

        if (!me.isPrimary()) {
            req.getSession().setAttribute(
                    "flashError",
                    "休息リクエストは主介護者専用の機能です。"
            );

            resp.sendRedirect(
                    req.getContextPath()
                    + "/careshift/main/Menu.action"
            );

            return;
        }

        /*
         * GET・POSTのどちらで旧URLが呼ばれても、
         * 旧rest_recordsへ登録せず、正式画面に移動する。
         */
        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/RestRequest.action"
        );
    }
}