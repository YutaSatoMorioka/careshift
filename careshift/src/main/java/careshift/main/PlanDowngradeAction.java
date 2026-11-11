package careshift.main;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S21 「無料へ戻す」。URL: /careshift/main/PlanDowngrade.action（POST・主介護者のみ・画面で確認してから）
 * すぐには無料にならず、次回更新日（利用期限）までは有料機能を使える。
 */
public class PlanDowngradeAction extends Action {

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
        if (!plan.isPaid()) {
            flashError(req, "現在は有料プランを契約していません。");
        } else if (plan.isCancelRequested()) {
            flash(req, "すでに無料へ戻す手続き済みです。" + plan.getNextDate() + " まで有料機能を利用できます。");
        } else if (dao.requestCancel(me.getGroupId())) {
            flash(req, "無料へ戻す手続きをしました。" + dao.find(me.getGroupId()).getNextDate() + " まで、有料機能を利用できます。");
        } else {
            flashError(req, "手続きできませんでした。もう一度お試しください。");
        }
        resp.sendRedirect(back);
    }
}
