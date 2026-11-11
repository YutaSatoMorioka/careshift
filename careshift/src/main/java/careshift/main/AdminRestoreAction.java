package careshift.main;

import bean.User;
import dao.NotificationDao;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AdminSupport;
import tool.AppLog;
import tool.BackupManager;
import tool.LoginLimiter;
import tool.PasswordUtil;
import tool.Validator;

/**
 * S90 「復元する」。URL: /careshift/main/AdminRestore.action（POST・管理者のみ）
 * 手順: 上書きの確認 → パスワードの再認証 → 破損チェック → 復元前の退避 → 復元。
 *  - 再認証に失敗したら、何も変えずに中断する（失敗が続くとしばらくロック）
 *  - 復元に失敗したら、退避から元に戻して、管理者全員に通知する
 *  - 復元に成功したら、データが入れ替わったため、もう一度ログインしてもらう
 */
public class AdminRestoreAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        if (!AdminAction.requireAdmin(req, resp, user)) return;

        String back = req.getContextPath() + "/careshift/main/Admin.action";
        String generation = Validator.trim(req.getParameter("restoreGeneration"));
        String password = Validator.nz(req.getParameter("password"));

        if (generation.isEmpty()) {
            flashError(req, "復元する世代を選んでください。");
            resp.sendRedirect(back);
            return;
        }
        if (!"1".equals(req.getParameter("confirm"))) {
            flashError(req, "「現在のデータを上書きする」ことを確認して、チェックを入れてください。");
            resp.sendRedirect(back);
            return;
        }

        // 再認証（パスワードの再入力）。失敗が続いたら一定時間ロックする
        String limiterKey = "admin:" + user.getEmail().toLowerCase();
        LoginLimiter limiter = LoginLimiter.shared();
        if (limiter.isLocked(limiterKey)) {
            AppLog.write("AUTH", "管理者の再認証がロック中のため、復元を中断しました");
            flashError(req, "再認証の失敗が続いたため、しばらく操作できません。時間をおいてお試しください。");
            resp.sendRedirect(back);
            return;
        }
        User fresh = new UserDao().findById(user.getId());
        if (fresh == null || password.isEmpty() || !PasswordUtil.verify(password, fresh.getPasswordHash())) {
            limiter.recordFailure(limiterKey);
            AppLog.write("AUTH", "管理者の再認証に失敗したため、復元を中断しました");
            flashError(req, "再認証に失敗しました。パスワードを確認してください（復元は行っていません）。");
            resp.sendRedirect(back);
            return;
        }
        limiter.recordSuccess(limiterKey);

        try {
            BackupManager.restore(generation);
        } catch (BackupManager.RestoreException e) {
            notifyAdmins("復元に失敗しました：" + e.getMessage());
            flashError(req, e.getMessage() + (e.isRolledBack() ? "（データは復元前のままです）" : ""));
            resp.sendRedirect(back);
            return;
        }

        // データが入れ替わったので、いったんログアウトして、ログインし直してもらう
        if (req.getSession(false) != null) {
            req.getSession(false).invalidate();
        }
        req.getSession(true).setAttribute("flash", "復元が完了しました。データが入れ替わったため、もう一度ログインしてください。");
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    /** 管理者全員へ、アプリ内の通知を送る。 */
    private void notifyAdmins(String message) {
        try {
            UserDao users = new UserDao();
            for (String email : AdminSupport.adminEmails()) {
                User admin = users.findByEmail(email);
                if (admin != null) {
                    new NotificationDao().add(admin.getId(), "ADMIN", message, "/careshift/main/Admin.action");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 通知できなくても、画面には結果を出す
        }
    }
}
