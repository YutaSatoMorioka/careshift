package careshift.main;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.PlanCatalog;

/**
 * S21 「有料へ切替」の金額・条件の確認画面。URL: /careshift/main/PlanConfirm.action（主介護者のみ）
 * すでに契約済みなら、案内して戻す。
 */
public class PlanConfirmAction extends Action {

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
        GroupPlan plan = new PlanDao().find(me.getGroupId());
        if (plan.isPaid()) { // 契約済みは案内
            flash(req, plan.isCancelRequested()
                    ? "有料プランの解約を予約済みです。" + plan.getNextDate() + " まで、有料機能を利用できます。"
                    : "すでに有料プランを契約しています。");
            resp.sendRedirect(back);
            return;
        }
        req.setAttribute("price", PlanCatalog.PAID_PRICE_YEN);
        req.setAttribute("features", PlanCatalog.FEATURES);
        req.getRequestDispatcher("/careshift/main/plan_confirm.jsp").forward(req, resp);
    }
}
