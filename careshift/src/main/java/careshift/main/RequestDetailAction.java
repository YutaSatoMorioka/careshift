package careshift.main;

import java.util.Map;

import bean.Member;
import bean.RequestCard;
import bean.RequestRecipient;
import bean.User;
import dao.RequestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/**
 * S13 依頼詳細・回答。URL: /careshift/main/RequestDetail.action?id=...
 * 送信者には各宛先の回答状況と取り下げを、宛先には回答フォームを表示する。
 * 取り下げ済み・期限切れ・他の人が引受済みの依頼には回答できない（理由を表示する）。
 */
public class RequestDetailAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        RequestCard card = new RequestDao().find(TaskSupport.parseId(req.getParameter("id")), me.getGroupId());
        if (card == null || !isParticipant(card, user.getId())) {
            // 存在しない場合と、無関係な家族の場合を区別せず同じ案内にする
            flashError(req, "依頼が見つかりません。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/RequestList.action");
            return;
        }
        RequestRecipient mine = card.recipient(user.getId());
        show(req, resp, user, card, null, mine == null ? null : mine.getResponse(),
                mine == null ? "" : mine.getAlternative(), false);
    }

    static boolean isParticipant(RequestCard card, long userId) {
        return card.getSenderId() == userId || card.recipient(userId) != null;
    }

    /** 画面を表示する（回答の検証エラー・引受の確認でも使う）。 */
    static void show(HttpServletRequest req, HttpServletResponse resp, User user, RequestCard card,
            Map<String, String> errors, String selected, String alternative, boolean confirmAccept) throws Exception {
        boolean isSender = card.getSenderId() == user.getId();
        String blocked = isSender ? "" : card.getAnswerBlockedReason(user.getId());
        req.setAttribute("card", card);
        req.setAttribute("isSender", isSender);
        req.setAttribute("blockedReason", blocked);
        req.setAttribute("canAnswer", !isSender && blocked.isEmpty());
        req.setAttribute("responses", Options.RESPONSES);
        req.setAttribute("selected", selected == null ? "" : selected);
        req.setAttribute("alternative", alternative == null ? "" : alternative);
        req.setAttribute("confirmAccept", confirmAccept);
        req.setAttribute("errors", errors == null ? new java.util.LinkedHashMap<String, String>() : errors);
        req.getRequestDispatcher("/careshift/main/request_detail.jsp").forward(req, resp);
    }
}
