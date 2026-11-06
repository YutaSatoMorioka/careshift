package careshift.main;

import bean.Member;
import bean.RequestCard;
import bean.RequestRecipient;
import bean.User;
import dao.RequestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S11 / S13 依頼の取り下げ。URL: /careshift/main/RequestWithdraw.action（POST）
 * 送信者のみ。回答待ちで期限前のときだけ。できない場合は理由を表示し、成功したら宛先へ通知する。
 */
public class RequestWithdrawAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String listUrl = req.getContextPath() + "/careshift/main/RequestList.action?tab=sent";
        long id = TaskSupport.parseId(req.getParameter("id"));
        RequestDao dao = new RequestDao();
        RequestCard card = dao.find(id, me.getGroupId());
        if (card == null) {
            flashError(req, "依頼が見つかりません。");
            resp.sendRedirect(listUrl);
            return;
        }
        if (card.getSenderId() != user.getId()) {
            flashError(req, "依頼を取り下げられるのは、送信した本人のみです。");
            resp.sendRedirect(listUrl);
            return;
        }
        if (!card.isWithdrawable() || !dao.withdraw(id, user.getId())) {
            RequestCard latest = dao.find(id, me.getGroupId());
            String reason = latest == null ? "" : latest.getWithdrawBlockedReason();
            flashError(req, reason.isEmpty() ? "取り下げできませんでした。もう一度お試しください。" : reason);
            resp.sendRedirect(listUrl);
            return;
        }
        for (RequestRecipient r : card.getRecipients()) {
            Notices.send(r.getUserId(), "REQUEST",
                    user.getDisplayName() + " さんが依頼を取り下げました：" + Notices.brief(card.getContent(), 30),
                    Notices.requestLink(id));
        }
        flash(req, "依頼を取り下げました。");
        resp.sendRedirect(listUrl);
    }
}
