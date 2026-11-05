package careshift;

import bean.Invitation;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.InvitationDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.TokenUtil;

/**
 * S04 招待から参加（確認画面）。URL: /careshift/Invite.action?token=...
 * 無効・使用済み・期限切れ・上限6名・別グループ所属は、理由を表示して中止する。
 * 未ログインならログイン画面へ移動し、ログイン後にこの画面へ戻す。
 */
public class InviteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String token = req.getParameter("token");
        Invitation inv = (token == null || token.isEmpty()) ? null
                : new InvitationDao().find(TokenUtil.hash(token));

        if (inv == null) {
            stop(req, resp, "招待リンクが正しくありません", "リンクをそのまま開いているか、招待した家族に確認してください。");
            return;
        }
        if (inv.isUsed()) {
            stop(req, resp, "この招待リンクは使用済みです", "招待リンクは1名分のみ有効です。新しいリンクを招待した家族に依頼してください。");
            return;
        }
        if (inv.isExpired()) {
            stop(req, resp, "招待リンクの有効期限が切れています", "招待リンクの有効期間は7日間です。新しいリンクを招待した家族に依頼してください。");
            return;
        }

        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?invite=" + token); // ログイン後に戻す
            return;
        }

        GroupDao groupDao = new GroupDao();
        Member me = groupDao.findMember(user.getId());
        if (me != null) {
            String reason = me.getGroupId() == inv.getGroupId()
                    ? "すでにこのグループに参加しています。"
                    : "すでに別の家族グループに所属しているため、参加できません。";
            showResult(req, resp, "参加できません", reason, "/careshift/main/Menu.action", "ホームへ戻る");
            return;
        }
        if (groupDao.countMembers(inv.getGroupId()) >= GroupDao.MAX_MEMBERS) {
            stop(req, resp, "参加できません", "このグループは人数の上限（" + GroupDao.MAX_MEMBERS + "名）に達しています。");
            return;
        }

        req.setAttribute("invitation", inv);
        req.setAttribute("token", token);
        req.getRequestDispatcher("/careshift/main/invite.jsp").forward(req, resp);
    }

    private void stop(HttpServletRequest req, HttpServletResponse resp, String title, String message)
            throws Exception {
        showResult(req, resp, title, message, "/login.jsp", "ログインへ");
    }
}
