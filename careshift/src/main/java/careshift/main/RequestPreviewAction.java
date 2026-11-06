package careshift.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S12 送信前のプレビュー（request_create.jsp の確認ボタンの送信先）。URL: /careshift/main/RequestPreview.action
 * 検証に通ったら、宛先・内容を表示して「送信」「修正」を選んでもらう。
 */
public class RequestPreviewAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        RequestForm form = RequestForm.fromRequest(req);
        Map<String, String> errors = form.validate(user.getId(), TaskSupport.memberIds(members));

        // 休息の依頼は、日付・開始終了時刻・重複確認が必要なため、主介護者専用の休息リクエスト画面で行う
        if (errors.isEmpty() && "REST".equals(form.type())) {
            if (!me.isPrimary()) {
                errors.put("requestType", "休息の依頼を作成できるのは主介護者のみです");
            } else {
                flash(req, "休息の依頼は、日付と開始・終了時刻を指定する「休息リクエスト」画面から送ります。");
                resp.sendRedirect(req.getContextPath() + "/careshift/main/RestRequest.action");
                return;
            }
        }
        if (!errors.isEmpty()) {
            RequestCreateAction.show(req, resp, user, me, form, errors);
            return;
        }
        PreviewSupport.forward(req, resp, form, members, null);
    }

    /** プレビュー画面の表示（送信失敗時の再表示にも使う）。 */
    static final class PreviewSupport {
        static void forward(HttpServletRequest req, HttpServletResponse resp, RequestForm form, List<Member> members,
                String sendError) throws Exception {
            List<String> names = new ArrayList<>();
            for (Member m : members) {
                if (form.recipientValues().contains(m.getUserId())) names.add(m.getDisplayName());
            }
            req.setAttribute("form", form);
            req.setAttribute("recipientNames", names);
            req.setAttribute("sendError", sendError);
            req.getRequestDispatcher("/careshift/main/request_preview.jsp").forward(req, resp);
        }
    }
}
