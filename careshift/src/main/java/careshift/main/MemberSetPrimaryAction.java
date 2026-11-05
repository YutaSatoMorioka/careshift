package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/** S16 「主介護者に設定」ボタンの送信先。URL: /careshift/main/MemberSetPrimary.action（主介護者のみ） */
public class MemberSetPrimaryAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String back = req.getContextPath() + "/careshift/main/MemberList.action";
        GroupDao dao = new GroupDao();

        Member me = dao.findMember(user.getId());
        if (me == null) {
            resp.sendRedirect(req.getContextPath() + "/careshift/main/Group.action");
            return;
        }
        if (!me.isPrimary()) {
            flashError(req, "主介護者を設定できるのは、現在の主介護者のみです。");
            resp.sendRedirect(back);
            return;
        }
        Member target = MemberSaveAction.findTarget(dao, me, req.getParameter("userId"));
        if (target == null) {
            flashError(req, "対象のメンバーが見つかりません。");
        } else if (target.isPrimary()) {
            flashError(req, target.getDisplayName() + " さんはすでに主介護者です。");
        } else {
            dao.setPrimary(me.getGroupId(), target.getUserId());
            flash(req, target.getDisplayName() + " さんを主介護者に設定しました。");
        }
        resp.sendRedirect(back);
    }
}
