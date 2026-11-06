package careshift.main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.RequestDao;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S14 休息リクエストの送信（rest_request.jsp の送信ボタンの送信先）。URL: /careshift/main/RestRequestSend.action
 * 日時は未来・終了は開始より後・同じ時間帯の重複は不可。送ると「調整中」の休息予定になり、家族へ通知する。
 */
public class RestRequestSendAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;
        if (!RestRequestAction.requirePrimary(req, resp, me)) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        RestForm form = RestForm.fromRequest(req);
        Map<String, String> errors = form.validate(user.getId(), TaskSupport.memberIds(members));

        if (errors.isEmpty() && new RestDao().hasOverlap(me.getGroupId(), form.dateValue(), form.startValue(), form.endValue())) {
            errors.put("restDate", "同じ時間帯に、別の休息予定がすでにあります");
        }
        if (!errors.isEmpty()) {
            RestRequestAction.show(req, resp, user, me, form, errors);
            return;
        }

        long requestId;
        try {
            requestId = new RequestDao().createRest(me.getGroupId(), form.dateValue(), form.startValue(), form.endValue(),
                    form.contentValue(), user.getId(), form.recipientValues());
        } catch (Exception e) {
            e.printStackTrace();
            errors = new LinkedHashMap<>();
            errors.put("send", "送信に失敗しました。入力内容はそのままです。もう一度お試しください。");
            RestRequestAction.show(req, resp, user, me, form, errors);
            return;
        }

        String when = form.dateValue() + " " + form.startValue() + "〜" + form.endValue();
        for (long recipient : form.recipientValues()) {
            Notices.send(recipient, "REST",
                    user.getDisplayName() + " さんが休息の協力を依頼しています（" + when + "）",
                    Notices.requestLink(requestId));
        }
        flash(req, "休息リクエストを送信しました（調整中）。家族の回答を待ちましょう。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/RestRequest.action");
    }
}
