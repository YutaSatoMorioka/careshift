package careshift.main;

import java.time.YearMonth;

import bean.Member;
import bean.RestPlan;
import bean.User;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S10 / S14 休息予定の取消。URL: /careshift/main/RestCancel.action（POST・主介護者のみ）
 * 画面側で確認してから状態を「取消」に更新し、担当者（引き受けた人）がいれば通知する。
 */
public class RestCancelAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;
        if (!RestRequestAction.requirePrimary(req, resp, me)) return;

        RestDao dao = new RestDao();
        RestPlan plan = dao.find(TaskSupport.parseId(req.getParameter("id")), me.getGroupId());
        String back = req.getContextPath() + "/careshift/main/RestRequest.action";
        if (plan != null && "calendar".equals(Validator.trim(req.getParameter("back")))) {
            back = req.getContextPath() + "/careshift/main/Calendar.action?month=" + YearMonth.from(plan.getDate());
        }
        if (plan == null) {
            flashError(req, "休息予定が見つかりません。");
        } else if (plan.isCancelled()) {
            flashError(req, "この休息予定はすでに取り消されています。");
        } else if (dao.cancel(plan.getId(), me.getGroupId())) {
            if (plan.getCoveredBy() != null && plan.getCoveredBy() != user.getId()) {
                Notices.send(plan.getCoveredBy(), "REST",
                        user.getDisplayName() + " さんが休息予定を取り消しました（" + plan.getDateLabel() + " "
                                + plan.getTimeRange() + "）", "/careshift/main/Calendar.action?month="
                                + YearMonth.from(plan.getDate()));
            }
            flash(req, "休息予定を取り消しました。");
        } else {
            flashError(req, "取り消しできませんでした。もう一度お試しください。");
        }
        resp.sendRedirect(back);
    }
}
