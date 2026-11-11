package careshift.main;

import java.util.Map;

import bean.FamilyGroup;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.PlanDao;
import dao.SettingsDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S20 設定。URL: /careshift/main/Settings.action
 * グループ名（主介護者のみ）・通知・データ出力（主介護者のみ）・プラン・ログアウトをまとめた画面。
 * アプリ内の依頼通知は常にオンで、変更できない。
 */
public class SettingsAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;
        show(req, resp, user, me, null, null);
    }

    /** 画面を表示する。errors / groupNameInput は、入力エラーで再表示するときに使う。 */
    static void show(HttpServletRequest req, HttpServletResponse resp, User user, Member me,
            Map<String, String> errors, String groupNameInput) throws Exception {
        FamilyGroup group = new GroupDao().findGroup(me.getGroupId());
        SettingsDao settings = new SettingsDao();
        req.setAttribute("member", me);
        req.setAttribute("group", group);
        req.setAttribute("groupName", groupNameInput != null ? groupNameInput : group.getName());
        req.setAttribute("emailNotice", settings.isEmailNoticeOn(user.getId())); // 保存済みの値（失敗時は元の設定に戻る）
        req.setAttribute("reminder", settings.isReminderOn(user.getId()));
        req.setAttribute("plan", new PlanDao().find(me.getGroupId()));
        req.setAttribute("errors", errors == null ? new java.util.LinkedHashMap<String, String>() : errors);
        req.getRequestDispatcher("/careshift/main/settings.jsp").forward(req, resp);
    }
}
