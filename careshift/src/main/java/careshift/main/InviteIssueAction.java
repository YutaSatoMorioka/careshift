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
 * S05 招待リンクの発行（group.jsp の invite ボタンの送信先）。URL: /careshift/main/InviteIssue.action
 * 続柄を指定した「1名分・7日間有効」のリンクを作る。グループは6名まで。
 * 招待で付与する役割は「家族等」固定（主介護者の付け替えは S16 で行う）。
 */
public class InviteIssueAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String back = req.getContextPath() + "/careshift/main/Group.action";

        GroupDao groupDao = new GroupDao();
        Member me = groupDao.findMember(user.getId());
        if (me == null) {
            flashError(req, "先に家族グループを作成してください。");
            resp.sendRedirect(back);
            return;
        }
        String relationship = Validator.trim(req.getParameter("relationship"));
        if (!Options.isValidRelationship(relationship)) {
            flashError(req, "続柄の選択が正しくありません。");
            resp.sendRedirect(back);
            return;
        }
        if (groupDao.countMembers(me.getGroupId()) >= GroupDao.MAX_MEMBERS) {
            flashError(req, "グループの人数が上限（" + GroupDao.MAX_MEMBERS + "名）に達しているため、招待できません。");
            resp.sendRedirect(back);
            return;
        }

        String token = TokenUtil.generate();
        new InvitationDao().issue(me.getGroupId(), TokenUtil.hash(token), relationship, Member.ROLE_FAMILY,
                user.getId(), LocalDateTime.now().plusDays(7));

        req.getSession().setAttribute("inviteLink", Mailer.baseUrl(req) + "/careshift/Invite.action?token=" + token);
        flash(req, "招待リンクを発行しました。下のリンクを、招待したい家族に送ってください。");
        resp.sendRedirect(back);
    }
}
