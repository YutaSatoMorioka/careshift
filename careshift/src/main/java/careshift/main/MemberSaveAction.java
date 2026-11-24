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
 * メンバー情報の保存。
 *
 * URL:
 * /careshift/main/MemberSave.action
 *
 * 主介護者は全メンバーを編集できる。
 * 家族等は自分の続柄と連絡先だけを変更できる。
 */
public class MemberSaveAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        if (!"POST".equalsIgnoreCase(
                req.getMethod()
        )) {
            resp.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED
            );

            return;
        }

        User user =
                requireLogin(
                        req,
                        resp
                );

        if (user == null) {
            return;
        }

        String back =
                req.getContextPath()
                + "/careshift/main/MemberList.action";

        GroupDao dao =
                new GroupDao();

        Member me =
                dao.findMember(
                        user.getId()
                );

        if (me == null) {
            flashError(
                    req,
                    "家族グループに所属していません。"
            );

            resp.sendRedirect(
                    req.getContextPath()
                    + "/careshift/main/Group.action"
            );

            return;
        }

        Member target =
                findTarget(
                        dao,
                        me,
                        req.getParameter("userId")
                );

        if (target == null) {
            flashError(
                    req,
                    "対象のメンバーが見つかりません。"
            );

            resp.sendRedirect(back);

            return;
        }

        if (!me.isPrimary()
                && target.getUserId()
                != me.getUserId()) {

            flashError(
                    req,
                    "他のメンバーの情報を変更できるのは主介護者のみです。"
            );

            resp.sendRedirect(back);

            return;
        }

        String relationship =
                Validator.trim(
                        req.getParameter("relationship")
                );

        String contact =
                Validator.trim(
                        req.getParameter("contact")
                );

        String role =
                Validator.trim(
                        req.getParameter("role")
                );

        if (!Options.isValidRelationship(
                relationship
        )) {
            flashError(
                    req,
                    "続柄の選択が正しくありません。"
            );

            resp.sendRedirect(back);

            return;
        }

        if (contact.length() > 100) {
            flashError(
                    req,
                    "連絡先は100文字以内で入力してください。"
            );

            resp.sendRedirect(back);

            return;
        }

        boolean transferPrimary =
                false;

        if (me.isPrimary()
                && !role.isEmpty()) {

            if (Member.ROLE_PRIMARY.equals(role)) {

                transferPrimary =
                        !target.isPrimary();

            } else if (Member.ROLE_FAMILY.equals(role)) {

                if (target.isPrimary()) {
                    flashError(
                            req,
                            "主介護者は常に1名必要です。"
                            + "別のメンバーを主介護者に設定してください。"
                    );

                    resp.sendRedirect(back);

                    return;
                }

            } else {
                flashError(
                        req,
                        "役割の選択が正しくありません。"
                );

                resp.sendRedirect(back);

                return;
            }
        }

        boolean updated =
                dao.updateProfile(
                        me.getGroupId(),
                        target.getUserId(),
                        relationship,
                        contact
                );

        if (!updated) {
            flashError(
                    req,
                    "メンバー情報を更新できませんでした。"
            );

            resp.sendRedirect(back);

            return;
        }

        if (transferPrimary) {
            boolean transferred =
                    dao.setPrimary(
                            me.getGroupId(),
                            target.getUserId()
                    );

            if (!transferred) {
                flashError(
                        req,
                        "主介護者を変更できませんでした。"
                );

                resp.sendRedirect(back);

                return;
            }

            flash(
                    req,
                    target.getDisplayName()
                    + " さんを主介護者に設定し、"
                    + "メンバー情報を更新しました。"
            );

        } else {
            flash(
                    req,
                    target.getDisplayName()
                    + " さんの情報を更新しました。"
            );
        }

        resp.sendRedirect(back);
    }

    /**
     * 同じグループのメンバーだけを取得する。
     */
    static Member findTarget(
            GroupDao dao,
            Member me,
            String userIdParam
    ) throws Exception {

        String value =
                Validator.trim(
                        userIdParam
                );

        if (!value.matches("\\d{1,18}")) {
            return null;
        }

        try {
            long userId =
                    Long.parseLong(value);

            return dao.findMember(
                    me.getGroupId(),
                    userId
            );

        } catch (NumberFormatException e) {
            return null;
        }
    }
}