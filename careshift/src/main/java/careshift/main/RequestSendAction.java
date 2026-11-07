package careshift.main;

import java.util.List;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.RequestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S12 依頼の送信（プレビューの送信ボタンの送信先）。URL: /careshift/main/RequestSend.action
 * 送信に失敗したら、入力（下書き）をそのままプレビューに戻して再試行できる。
 */
public class RequestSendAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        RequestForm form = RequestForm.fromRequest(req);
        Map<String, String> errors = form.validate(user.getId(), TaskSupport.memberIds(members));
        if (errors.isEmpty() && "REST".equals(form.type())) {
            errors.put("requestType", "休息の依頼は「休息リクエスト」画面から送ってください");
        }
        if (!errors.isEmpty()) {
            RequestCreateAction.show(req, resp, user, me, form, errors); // 画面を開いている間に条件が変わった場合
            return;
        }

        long id;
        try {
            // 関連づけるタスクは、同じグループのものだけ
            Long taskId = form.taskIdValue();
            if (taskId != null && new dao.TaskDao().find(taskId, me.getGroupId()) == null) {
                taskId = null;
            }
            id = new RequestDao().create(me.getGroupId(), form.type(), form.getContent(), form.requestedAtValue(),
                    form.durationValue(), taskId, null, user.getId(), form.recipientValues());
        } catch (Exception e) {
            e.printStackTrace();
            RequestPreviewAction.PreviewSupport.forward(req, resp, form, members,
                    "送信に失敗しました。入力内容はそのままです。もう一度お試しください。");
            return;
        }

        for (long recipient : form.recipientValues()) {
            Notices.send(recipient, "REQUEST",
                    user.getDisplayName() + " さんから依頼が届きました：" + Notices.brief(form.getContent(), 30),
                    Notices.requestLink(id));
        }
        flash(req, "依頼を送信しました。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/RequestList.action?tab=sent");
    }
}
