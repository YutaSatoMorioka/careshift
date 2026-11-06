package careshift.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.RequestCard;
import bean.RestPlan;
import bean.User;
import dao.GroupDao;
import dao.RequestDao;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S14 調整中の休息予定への再依頼。URL: /careshift/main/RestReRequest.action（POST）
 * 全員が難しい場合などに、同じ予定のまま、宛先を選び直して依頼し直せる。
 */
public class RestReRequestAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;
        if (!RestRequestAction.requirePrimary(req, resp, me)) return;

        String back = req.getContextPath() + "/careshift/main/RestRequest.action";
        RestPlan plan = new RestDao().find(TaskSupport.parseId(req.getParameter("restId")), me.getGroupId());
        if (plan == null) {
            flashError(req, "休息予定が見つかりません。");
            resp.sendRedirect(back);
            return;
        }
        RequestDao requestDao = new RequestDao();
        List<RequestCard> cards = requestDao.findByRest(plan.getId(), me.getGroupId());
        RestRow row = new RestRow(plan, cards.isEmpty() ? null : cards.get(0));
        if (!row.isCanRerequest()) {
            flashError(req, plan.isCancelled() ? "取り消された休息予定には再依頼できません。"
                    : plan.isConfirmed() ? "すでに確定している休息予定です。"
                    : plan.isStarted() ? "開始時刻を過ぎているため、再依頼できません。"
                    : "まだ回答待ちの依頼があります。");
            resp.sendRedirect(back);
            return;
        }

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        Map<String, String> errors = new java.util.LinkedHashMap<>();
        List<Long> recipients = TaskSupport.parseRecipients(req.getParameterValues("recipients"), user.getId(),
                TaskSupport.memberIds(members), errors);
        if (!errors.isEmpty()) {
            flashError(req, errors.get("recipients"));
            resp.sendRedirect(back);
            return;
        }

        long id = requestDao.create(me.getGroupId(), "REST", plan.getContent(), plan.getStartAt(), plan.getMinutes(),
                null, plan.getId(), user.getId(), recipients);
        requestDao.withdrawOpenForRest(plan.getId(), id);
        for (long recipient : recipients) {
            Notices.send(recipient, "REST",
                    user.getDisplayName() + " さんが休息の協力をあらためて依頼しています（" + plan.getDateLabel() + " "
                            + plan.getTimeRange() + "）", Notices.requestLink(id));
        }
        flash(req, "あらためて依頼を送信しました（調整中のままです）。");
        resp.sendRedirect(back);
    }
}
