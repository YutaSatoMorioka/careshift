package careshift.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.RequestCard;
import bean.RestPlan;
import bean.User;
import dao.GroupDao;
import dao.RequestDao;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S14 休息リクエスト（入力画面と予定の一覧）。URL: /careshift/main/RestRequest.action
 * 主介護者専用。休息を保証するものではなく、家族との調整を始める予定として扱う。
 */
public class RestRequestAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;
        if (!requirePrimary(req, resp, me)) return;

        show(req, resp, user, me, RestForm.blank(), null);
    }

    /** 主介護者でなければ案内してホームへ戻す。 */
    static boolean requirePrimary(HttpServletRequest req, HttpServletResponse resp, Member me) throws Exception {
        if (me.isPrimary()) return true;
        req.getSession().setAttribute("flashError", "休息リクエストは主介護者専用の機能です。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/Menu.action");
        return false;
    }

    static void show(HttpServletRequest req, HttpServletResponse resp, User user, Member me, RestForm form,
            Map<String, String> errors) throws Exception {
        List<Member> others = new ArrayList<>();
        for (Member m : new GroupDao().findMembers(me.getGroupId())) {
            if (m.getUserId() != user.getId()) others.add(m);
        }
        RequestDao requestDao = new RequestDao();
        List<RestRow> rows = new ArrayList<>();
        for (RestPlan plan : new RestDao().findRecent(me.getGroupId(), 30)) {
            List<RequestCard> cards = requestDao.findByRest(plan.getId(), me.getGroupId());
            rows.add(new RestRow(plan, cards.isEmpty() ? null : cards.get(0)));
        }
        req.setAttribute("form", form);
        req.setAttribute("errors", errors == null ? new java.util.LinkedHashMap<String, String>() : errors);
        req.setAttribute("others", others);
        req.setAttribute("rows", rows);
        req.getRequestDispatcher("/careshift/main/rest_request.jsp").forward(req, resp);
    }
}
