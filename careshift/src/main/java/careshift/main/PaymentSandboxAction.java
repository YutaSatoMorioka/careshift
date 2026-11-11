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
 * 【試験用】決済サービスの代わりの画面。URL: /careshift/main/PaymentSandbox.action?token=...
 * 実際の決済サービスでは、カード情報の入力と決済はサービス側の画面で行われ、このアプリには渡らない。
 * ここでは「成功」「失敗」「キャンセル」を選んで、結果の受け取りを試せる。
 */
public class PaymentSandboxAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String token = Validator.trim(req.getParameter("token"));
        PlanDao.Payment p = token.isEmpty() ? null : new PlanDao().findPayment(TokenUtil.hash(token));
        if (p == null || p.userId != user.getId() || p.groupId != me.getGroupId() || !"PENDING".equals(p.status)) {
            flashError(req, "この決済は無効か、すでに処理されています。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/Plan.action");
            return;
        }
        req.setAttribute("token", token);
        req.setAttribute("amount", p.amount);
        req.getRequestDispatcher("/careshift/main/payment_sandbox.jsp").forward(req, resp);
    }
}
