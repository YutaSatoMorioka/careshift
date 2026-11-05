package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;
import tool.Validator;

/**
 * S16 メンバー情報の保存（member_list.jsp の save ボタンの送信先）。URL: /careshift/main/MemberSave.action
 * 権限: 主介護者は全員分、それ以外は自分の「続柄・連絡先」だけ変更できる（権限外は拒否）。
 * 役割: 主介護者は、他のメンバーを「主介護者」にして付け替えられる。主介護者が0名になる変更は不可。
 */
public class MemberSaveAction extends Action {

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
        Member target = findTarget(dao, me, req.getParameter("userId"));
        if (target == null) {
            flashError(req, "対象のメンバーが見つかりません。");
            resp.sendRedirect(back);
            return;
        }
        if (!me.isPrimary() && target.getUserId() != me.getUserId()) {
            flashError(req, "他のメンバーの情報を変更できるのは主介護者のみです。");
            resp.sendRedirect(back);
            return;
        }

        String relationship = Validator.trim(req.getParameter("relationship"));
        String contact = Validator.trim(req.getParameter("contact"));
        String role = Validator.trim(req.getParameter("role"));
        if (!Options.isValidRelationship(relationship)) {
            flashError(req, "続柄の選択が正しくありません。");
            resp.sendRedirect(back);
            return;
        }
        if (contact.length() > 100) {
            flashError(req, "連絡先は100文字以内で入力してください。");
            resp.sendRedirect(back);
            return;
        }

        // 役割の変更は主介護者だけ（それ以外が送ってきても無視する）
        boolean transfer = false;
        if (me.isPrimary() && !role.isEmpty()) {
            if (Member.ROLE_PRIMARY.equals(role)) {
                transfer = !target.isPrimary();
            } else if (Member.ROLE_FAMILY.equals(role)) {
                if (target.isPrimary()) {
                    flashError(req, "主介護者は常に1名必要です。別のメンバーを主介護者に設定してください。");
                    resp.sendRedirect(back);
                    return;
                }
            } else {
                flashError(req, "役割の選択が正しくありません。");
                resp.sendRedirect(back);
                return;
            }
        }

        dao.updateProfile(target.getUserId(), relationship, contact);
        if (transfer) {
            dao.setPrimary(me.getGroupId(), target.getUserId());
            flash(req, target.getDisplayName() + " さんを主介護者に設定し、情報を更新しました。");
        } else {
            flash(req, target.getDisplayName() + " さんの情報を更新しました。");
        }
        resp.sendRedirect(back);
    }

    /** 同じグループに所属するメンバーだけを対象にする（他グループのIDを送られても拒否）。 */
    static Member findTarget(GroupDao dao, Member me, String userIdParam) throws Exception {
        try {
            Member target = dao.findMember(Long.parseLong(Validator.trim(userIdParam)));
            return (target != null && target.getGroupId() == me.getGroupId()) ? target : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
