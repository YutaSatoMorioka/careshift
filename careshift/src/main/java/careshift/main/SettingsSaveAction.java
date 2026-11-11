package careshift.main;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.SettingsDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S20 設定の保存（settings.jsp の save ボタンの送信先）。URL: /careshift/main/SettingsSave.action
 *  - メール通知・期限リマインド：オン／オフを保存。チェックが外れていれば「オフ」
 *  - アプリ内通知：常にオン。変更の要求が来ても受け付けない
 *  - グループ名：主介護者のみ（1〜30文字）。権限のない人が送ってきたら、何も変更せず拒否する
 * 保存に失敗したら、元の設定のまま画面に戻す。
 */
public class SettingsSaveAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String back = req.getContextPath() + "/careshift/main/Settings.action";
        boolean emailNotice = req.getParameter("emailNotice") != null;
        boolean reminder = req.getParameter("reminder") != null;
        // appNotice（アプリ内通知）は、送られてきても無視する（常にオン）

        // グループ名の変更（送られてきたときだけ）
        String groupName = req.getParameter("groupName") == null ? null : Validator.trim(req.getParameter("groupName"));
        GroupDao groupDao = new GroupDao();
        boolean rename = false;
        if (groupName != null) {
            if (!me.isPrimary()) {
                flashError(req, "グループ名を変更できるのは主介護者のみです。設定は変更していません。");
                resp.sendRedirect(back);
                return;
            }
            Map<String, String> errors = new LinkedHashMap<>();
            if (groupName.isEmpty()) {
                errors.put("groupName", "グループ名を入力してください");
            } else if (groupName.length() > 30) {
                errors.put("groupName", "グループ名は30文字以内で入力してください");
            }
            if (!errors.isEmpty()) {
                SettingsAction.show(req, resp, user, me, errors, groupName); // 入力を保持して再表示
                return;
            }
            rename = !groupName.equals(groupDao.findGroup(me.getGroupId()).getName());
        }

        SettingsDao settings = new SettingsDao();
        boolean oldEmail = false;
        boolean oldReminder = true;
        try {
            oldEmail = settings.isEmailNoticeOn(user.getId());
            oldReminder = settings.isReminderOn(user.getId());
            settings.save(user.getId(), emailNotice, reminder);
            if (rename) {
                groupDao.rename(me.getGroupId(), groupName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { // 途中まで保存されていたら、元に戻す
                settings.save(user.getId(), oldEmail, oldReminder);
            } catch (Exception ignore) {
                // 戻せなかった場合も、画面は保存済みの値を表示する
            }
            flashError(req, "設定を保存できませんでした。通信状態を確認して、もう一度お試しください（画面は保存前の設定に戻しました）。");
            resp.sendRedirect(back);
            return;
        }
        flash(req, "設定を保存しました。");
        resp.sendRedirect(back);
    }
}
