package careshift.main;

import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.BackupManager;

/** S90 「今すぐバックアップ」。URL: /careshift/main/AdminBackup.action（POST・管理者のみ）。結果は履歴とログに残る。 */
public class AdminBackupAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        if (!AdminAction.requireAdmin(req, resp, user)) return;

        try {
            BackupManager.Record r = BackupManager.backup("MANUAL");
            flash(req, "バックアップを取りました（" + r.getAtLabel() + "・" + r.getSizeLabel() + "）。");
        } catch (Exception e) {
            flashError(req, "バックアップに失敗しました。もう一度お試しください（失敗は履歴とログに記録しました）。");
        }
        resp.sendRedirect(req.getContextPath() + "/careshift/main/Admin.action");
    }
}
