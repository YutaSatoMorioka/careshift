package careshift.main;

import java.time.LocalDateTime;

import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.InvitationDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Mailer;
import tool.Options;
import tool.TokenUtil;
import tool.Validator;

/**
 * 招待リンクの発行。
 *
 * URL:
 * /careshift/main/InviteIssue.action
 *
 * 続柄を指定した1名分、7日間有効の招待リンクを発行する。
 * 招待時の役割は家族等で固定する。
 */
public class InviteIssueAction extends Action {

    private static final int INVITATION_VALID_DAYS =
            7;

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
                + "/careshift/main/Group.action";

        GroupDao groupDao =
                new GroupDao();

        Member me =
                groupDao.findMember(
                        user.getId()
                );

        if (me == null) {
            flashError(
                    req,
                    "先に家族グループを作成してください。"
            );

            resp.sendRedirect(back);

            return;
        }

        String relationship =
                Validator.trim(
                        req.getParameter("relationship")
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

        int memberCount =
                groupDao.countMembers(
                        me.getGroupId()
                );

        if (memberCount >= GroupDao.MAX_MEMBERS) {
            flashError(
                    req,
                    "グループの人数が上限（"
                    + GroupDao.MAX_MEMBERS
                    + "名）に達しているため、招待できません。"
            );

            resp.sendRedirect(back);

            return;
        }

        String token =
                TokenUtil.generate();

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusDays(
                                INVITATION_VALID_DAYS
                        );

        new InvitationDao().issue(
                me.getGroupId(),
                TokenUtil.hash(token),
                relationship,
                Member.ROLE_FAMILY,
                user.getId(),
                expiresAt
        );

        String inviteLink =
                Mailer.baseUrl(req)
                + "/careshift/Invite.action?token="
                + token;

        req.getSession()
                .setAttribute(
                        "inviteLink",
                        inviteLink
                );

        flash(
                req,
                "招待リンクを発行しました。"
                + "下のリンクを、招待したい家族本人に送ってください。"
        );

        resp.sendRedirect(back);
    }
}