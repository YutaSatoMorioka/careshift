package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.GroupDao.RemoveMemberResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * 主介護者による家族メンバー削除。
 *
 * URL:
 * /careshift/main/MemberRemove.action
 *
 * POSTのみ受け付ける。
 * 本人と主介護者は削除できない。
 */
public class MemberRemoveAction extends Action {

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

        GroupDao dao =
                new GroupDao();

        Member me =
                dao.findMember(
                        user.getId()
                );

        String back =
                req.getContextPath()
                + "/careshift/main/MemberList.action";

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
                    "メンバーを削除できるのは主介護者のみです。"
            );

            resp.sendRedirect(back);

            return;
        }

        long targetUserId =
                parseUserId(
                        req.getParameter("userId")
                );

        if (targetUserId < 1) {
            flashError(
                    req,
                    "対象のメンバーが正しくありません。"
            );

            resp.sendRedirect(back);

            return;
        }

        RemoveMemberResult result =
                dao.removeMember(
                        me.getGroupId(),
                        user.getId(),
                        targetUserId
                );

        switch (result) {

        case REMOVED:

            flash(
                    req,
                    "メンバーをグループから削除しました。"
                    + "担当していたタスクは未割当になり、"
                    + "引き受けていた依頼と休息予定は調整中に戻りました。"
            );

            break;

        case SELF:

            flashError(
                    req,
                    "自分自身をグループから削除することはできません。"
            );

            break;

        case PRIMARY:

            flashError(
                    req,
                    "主介護者はグループから削除できません。"
                    + "先に別のメンバーへ主介護者を引き継いでください。"
            );

            break;

        case NOT_FOUND:
        default:

            flashError(
                    req,
                    "対象のメンバーが見つからないか、削除できません。"
            );

            break;
        }

        resp.sendRedirect(back);
    }

    private long parseUserId(
            String value
    ) {

        String userId =
                Validator.trim(value);

        if (!userId.matches("\\d{1,18}")) {
            return -1;
        }

        try {
            return Long.parseLong(userId);

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}