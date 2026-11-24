package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 主介護者の変更。
 *
 * URL:
 * /careshift/main/MemberSetPrimary.action
 *
 * 現在の主介護者だけが実行できる。
 * POSTのみ受け付ける。
 */
public class MemberSetPrimaryAction extends Action {

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

        if (!me.isPrimary()) {
            flashError(
                    req,
                    "主介護者を設定できるのは、現在の主介護者のみです。"
            );

            resp.sendRedirect(back);

            return;
        }

        Member target =
                MemberSaveAction.findTarget(
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

        if (target.isPrimary()) {
            flashError(
                    req,
                    target.getDisplayName()
                    + " さんはすでに主介護者です。"
            );

            resp.sendRedirect(back);

            return;
        }

        boolean changed =
                dao.setPrimary(
                        me.getGroupId(),
                        target.getUserId()
                );

        if (!changed) {
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
                + " さんを主介護者に設定しました。"
        );

        resp.sendRedirect(back);
    }
}