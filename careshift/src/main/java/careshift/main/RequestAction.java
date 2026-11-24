package careshift.main;

import bean.Member;
import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧依頼画面URLとの互換用Action。
 *
 * 旧URL:
 * /careshift/main/Request.action
 *
 * 正式URL:
 * /careshift/main/RequestList.action
 *
 * 旧care_requests・care_request_responses方式では処理せず、
 * requests・request_recipients方式の正式な依頼一覧へ移動する。
 */
public class RequestAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        User user =
                requireLogin(
                        req,
                        resp
                );

        if (user == null) {
            return;
        }

        Member me =
                requireMember(
                        req,
                        resp,
                        user
                );

        if (me == null) {
            return;
        }

        /*
         * 旧Request.actionはGET・POSTのどちらで呼ばれても、
         * 旧care_requestsテーブルへの登録や一覧取得を行わない。
         *
         * GET:
         * 正式な依頼一覧へ移動する。
         *
         * POST:
         * 古いrequest.jspを開いたまま送信された可能性があるため、
         * 旧方式では登録せず、案内を表示して正式な依頼作成画面へ移動する。
         */
        if ("POST".equalsIgnoreCase(req.getMethod())) {

            req.getSession().setAttribute(
                    "flashError",
                    "旧形式の依頼画面からは送信できません。"
                    + "新しい依頼作成画面から入力してください。"
            );

            resp.sendRedirect(
                    req.getContextPath()
                    + "/careshift/main/RequestCreate.action"
            );

            return;
        }

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/RequestList.action"
        );
    }
}