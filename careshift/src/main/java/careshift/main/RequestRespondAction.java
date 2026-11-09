package careshift.main;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.Member;
import bean.RequestCard;
import bean.RequestRecipient;
import bean.User;
import dao.RequestDao;
import dao.RequestDao.RespondResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;
import tool.Validator;

/**
 * S13 回答の送信（request_detail.jsp の回答送信ボタンの送信先）。URL: /careshift/main/RequestRespond.action
 * 3択（担当する／今回は難しい／別タスクなら可能）。「別タスクなら可能」は代替案(300文字以内)が必須。
 * 「担当する」は確認後に担当を設定して依頼者へ通知。回答を変更したときも依頼者へ再通知する。
 */
public class RequestRespondAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        long id = TaskSupport.parseId(req.getParameter("id"));
        String listUrl = req.getContextPath() + "/careshift/main/RequestList.action";
        String detailUrl = req.getContextPath() + "/careshift/main/RequestDetail.action?id=" + id;
        RequestDao dao = new RequestDao();
        RequestCard card = dao.find(id, me.getGroupId());
        RequestRecipient mine = card == null ? null : card.recipient(user.getId());
        if (card == null || mine == null) {
            flashError(req, "依頼が見つかりません。");
            resp.sendRedirect(listUrl);
            return;
        }

        // 取り下げ済み・期限切れ・他の人が引受済みは、入力内容にかかわらず回答できない（理由を表示）
        String blocked = card.getAnswerBlockedReason(user.getId());
        if (!blocked.isEmpty()) {
            flashError(req, blocked);
            resp.sendRedirect(detailUrl);
            return;
        }

        String response = Validator.trim(req.getParameter("response"));
        String alternative = Validator.trim(req.getParameter("alternative"));
        Map<String, String> errors = new LinkedHashMap<>();
        if (!Options.RESPONSES.containsKey(response)) {
            errors.put("response", "回答を選んでください");
        } else if ("ALT".equals(response)) {
            if (alternative.isEmpty()) {
                errors.put("alternative", "「別タスクなら可能」の場合は代替案を入力してください");
            } else if (alternative.length() > 300) {
                errors.put("alternative", "代替案は300文字以内で入力してください");
            }
        }
        if (!errors.isEmpty()) {
            RequestDetailAction.show(req, resp, user, card, errors, response, alternative, false);
            return;
        }
        if (!"ALT".equals(response)) {
            alternative = "";
        }

        // 引受は確認してから（担当設定と通知が行われるため）
        if ("ACCEPT".equals(response) && !"1".equals(req.getParameter("confirm"))) {
            RequestDetailAction.show(req, resp, user, card, null, response, alternative, true);
            return;
        }

        // 回答が変わっていなければ何もしない（再通知もしない）
        if (response.equals(mine.getResponse()) && alternative.equals(mine.getAlternative())) {
            flash(req, "回答は変更されていません。");
            resp.sendRedirect(detailUrl);
            return;
        }

        RespondResult result = dao.respond(id, me.getGroupId(), user.getId(), response, alternative);
        switch (result) {
            case SAVED:
                notifySender(card, mine, user, response, alternative);
                flash(req, "ACCEPT".equals(response)
                        ? "担当として引き受けました。依頼者に通知しました。" : "回答を送信しました。依頼者に通知しました。");
                break;
            case WITHDRAWN:
                flashError(req, "この依頼は取り下げられたため、回答できません。");
                break;
            case EXPIRED:
                flashError(req, "希望日時・期限を過ぎているため、回答できません。");
                break;
            case TAKEN:
                flashError(req, "ほかの家族がすでに引き受けているため、回答できません。");
                break;
            default:
                flashError(req, "回答できませんでした。");
        }
        resp.sendRedirect(detailUrl);
    }

    private void notifySender(RequestCard card, RequestRecipient previous, User user, String response,
            String alternative) {
        String label = Options.RESPONSES.get(response);
        String message;
        if (previous.getResponse() == null) {
            message = user.getDisplayName() + " さんが「" + label + "」と回答しました";
        } else {
            message = user.getDisplayName() + " さんが回答を「" + previous.getResponseLabel() + "」から「" + label + "」に変更しました";
        }
        if ("ACCEPT".equals(response) && "REST".equals(card.getType())) {
            message += "（休息予定が確定しました）";
        }
        if ("ALT".equals(response)) {
            message += "（代替案: " + Notices.brief(alternative, 40) + "）";
        }
        message += "：" + Notices.brief(card.getContent(), 20);
        Notices.send(card.getSenderId(), "ANSWER", message, Notices.requestLink(card.getId()));
    }
}
