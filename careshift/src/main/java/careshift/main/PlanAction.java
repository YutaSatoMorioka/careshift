package careshift.main;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.PlanCatalog;

/**
 * S21 プラン。URL: /careshift/main/Plan.action
 * 現在のプランと機能・料金を表示する。切替の操作は主介護者にだけ表示する。決済の結果（成功／失敗）もここに表示する。
 */
public class PlanAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        GroupPlan plan = new PlanDao().find(me.getGroupId());
        req.setAttribute("member", me);
        req.setAttribute("plan", plan);
        req.setAttribute("features", PlanCatalog.FEATURES);
        req.setAttribute("price", PlanCatalog.PAID_PRICE_YEN);

        // 決済の結果は、表示したら消す
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("paymentResult") != null) {
            req.setAttribute("paymentResult", session.getAttribute("paymentResult"));
            req.setAttribute("paymentOk", session.getAttribute("paymentOk"));
            session.removeAttribute("paymentResult");
            session.removeAttribute("paymentOk");
        }
        req.getRequestDispatcher("/careshift/main/plan.jsp").forward(req, resp);
    }
}
