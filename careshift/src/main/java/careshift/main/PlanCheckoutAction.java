package careshift.main;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.PlanCatalog;
import tool.TokenUtil;

/**
 * S21 「決済サービスで申込」。URL: /careshift/main/PlanCheckout.action（POST・主介護者のみ）
 * 決済セッションを作って、決済サービスの画面へ移動する。カード情報はこのアプリでは一切扱わない。
 * いまの「決済サービス」は試験用の画面（PaymentSandbox）で、本番ではここを実際の決済サービスに差し替える。
 */
public class PlanCheckoutAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String back = req.getContextPath() + "/careshift/main/Plan.action";
        if (!me.isPrimary()) {
            flashError(req, "プランを切り替えられるのは主介護者のみです。");
            resp.sendRedirect(back);
            return;
        }
        PlanDao dao = new PlanDao();
        GroupPlan plan = dao.find(me.getGroupId());
        if (plan.isPaid()) {
            flash(req, "すでに有料プランを契約しています。");
            resp.sendRedirect(back);
            return;
        }

        String token = TokenUtil.generate();
        try {
            dao.createPayment(me.getGroupId(), user.getId(), TokenUtil.hash(token), PlanCatalog.PAID_PRICE_YEN);
        } catch (Exception e) {
            e.printStackTrace();
            flashError(req, "決済の準備に失敗しました。もう一度お試しください（プランは無料のままです）。");
            resp.sendRedirect(back);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/careshift/main/PaymentSandbox.action?token=" + token);
    }
}
