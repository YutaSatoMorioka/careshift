package careshift.main;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.AiDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * AI001 「修正して保存」（利用者が確認・修正したあとに保存する）。URL: /careshift/main/AiDraftSave.action
 * 保存するのは、依頼の下書き（内容・日時・所要時間）だけ。相談文は保存しない。
 */
public class AiDraftSaveAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String content = Validator.trim(req.getParameter("content"));
        String requestedAt = Validator.trim(req.getParameter("requestedAt"));
        String duration = Validator.trim(req.getParameter("duration"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (content.isEmpty()) {
            errors.put("content", "依頼内容を入力してください");
        } else if (content.length() > 500) {
            errors.put("content", "依頼内容は500文字以内で入力してください");
        }
        if (!requestedAt.isEmpty()) {
            try {
                LocalDateTime.parse(requestedAt);
            } catch (DateTimeParseException e) {
                errors.put("requestedAt", "希望日時は日時の形式で入力してください");
            }
        }
        Integer minutes = null;
        if (!duration.isEmpty()) {
            if (!duration.matches("\\d{1,6}") || Integer.parseInt(duration) < 5 || Integer.parseInt(duration) > 1440) {
                errors.put("duration", "所要時間は5分以上24時間（1440分）以内の整数で入力してください");
            } else {
                minutes = Integer.parseInt(duration);
            }
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("hasDraft", Boolean.TRUE);
            req.setAttribute("draftContent", content);
            req.setAttribute("draftRequestedAt", requestedAt);
            req.setAttribute("draftDuration", duration.isEmpty() ? null : duration);
            AiDraftAction.show(req, resp, me);
            return;
        }

        new AiDao().saveDraft(user.getId(), content, requestedAt, minutes);
        flash(req, "下書きを保存しました。「依頼作成へ」で、送る相手を選んで依頼を作れます。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/AiDraft.action");
    }
}
