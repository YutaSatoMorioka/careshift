package careshift.main;

import bean.Member;
import bean.User;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.TokenUtil;
import tool.Validator;

/**
 * 【試験用】決済サービスからの結果の受け取り。URL: /careshift/main/PaymentSandboxComplete.action（POST）
 * 成功ならプランを有料にし、失敗・キャンセルなら無料のままにする。同じ決済は一度しか反映されない。
 * 本番では、この役目は決済サービスからの通知（署名つき）を検証して行う。
 */
public class PaymentSandboxCompleteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String back = req.getContextPath() + "/careshift/main/Plan.action";
        String token = Validator.trim(req.getParameter("token"));
        String result = Validator.trim(req.getParameter("result"));
        PlanDao dao = new PlanDao();
        PlanDao.Payment p = token.isEmpty() ? null : dao.findPayment(TokenUtil.hash(token));
        if (p == null || p.userId != user.getId() || p.groupId != me.getGroupId()) {
            flashError(req, "この決済は無効です。");
            resp.sendRedirect(back);
            return;
        }

        boolean success = "success".equals(result);
        boolean applied;
        try {
            applied = dao.complete(TokenUtil.hash(token), success);
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("paymentResult", "決済の結果を反映できませんでした。プランは変更されていません。");
            req.getSession().setAttribute("paymentOk", Boolean.FALSE);
            resp.sendRedirect(back);
            return;
        }
        if (!applied) {
            flashError(req, "この決済はすでに処理されています。");
        } else if (success) {
            req.getSession().setAttribute("paymentResult", "決済が成功しました。有料プランを開始しました。");
            req.getSession().setAttribute("paymentOk", Boolean.TRUE);
        } else {
            req.getSession().setAttribute("paymentResult",
                    "cancel".equals(result) ? "決済をキャンセルしました。プランは無料のままです。"
                            : "決済に失敗しました。プランは無料のままです。");
            req.getSession().setAttribute("paymentOk", Boolean.FALSE);
        }
        resp.sendRedirect(back);
    }
}
