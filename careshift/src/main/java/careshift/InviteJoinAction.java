package careshift;

import bean.Invitation;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.InvitationDao;
import dao.InvitationDao.JoinResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.TokenUtil;

/**
 * S04 「参加する」ボタンの送信先。URL: /careshift/InviteJoin.action
 * 参加できたらホームと初回ガイドを表示する。招待者と主介護者には参加通知が届く。
 */
public class InviteJoinAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        String token = req.getParameter("token");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp" + (token == null ? "" : "?invite=" + token));
            return;
        }

        Invitation inv = (token == null || token.isEmpty()) ? null
                : new InvitationDao().find(TokenUtil.hash(token));
        if (inv == null) {
            showResult(req, resp, "招待リンクが正しくありません", "リンクをそのまま開いているか、招待した家族に確認してください。",
                    "/careshift/main/Menu.action", "ホームへ");
            return;
        }

        JoinResult result = new InvitationDao().join(inv.getId(), user.getId(), user.getDisplayName());
        switch (result) {
            case JOINED:
                flash(req, "「" + inv.getGroupName() + "」に参加しました。");
                req.getSession().setAttribute("guide", Boolean.TRUE); // ホームで初回ガイドを表示
                resp.sendRedirect(req.getContextPath() + "/careshift/main/Menu.action");
                return;
            case USED:
                fail(req, resp, "この招待リンクは使用済みです。");
                return;
            case EXPIRED:
                fail(req, resp, "招待リンクの有効期限が切れています。");
                return;
            case FULL:
                fail(req, resp, "このグループは人数の上限（" + GroupDao.MAX_MEMBERS + "名）に達しています。");
                return;
            case ALREADY_IN_GROUP:
                Member me = new GroupDao().findMember(user.getId());
                fail(req, resp, (me != null && me.getGroupId() == inv.getGroupId())
                        ? "すでにこのグループに参加しています。"
                        : "すでに別の家族グループに所属しているため、参加できません。");
                return;
            default:
                fail(req, resp, "招待リンクが正しくありません。");
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String reason) throws Exception {
        showResult(req, resp, "参加できません", reason, "/careshift/main/Menu.action", "ホームへ戻る");
    }
}
