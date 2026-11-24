package careshift.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧バックアップURLとの互換用Action。
 *
 * 旧URL:
 * /careshift/admin/AdminBackup.action
 *
 * 正式URL:
 * /careshift/main/AdminBackup.action
 *
 * 旧BackupServiceではバックアップを作成しない。
 * 正式なS90管理画面へ案内する。
 */
public class AdminBackupAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        if (!"POST".equalsIgnoreCase(
                req.getMethod()
        )) {
            resp.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED
            );

            return;
        }

        req.getSession()
                .setAttribute(
                        "flashError",
                        "旧管理画面からはバックアップを実行できません。"
                        + "通常ログイン後のS90管理者画面を使用してください。"
                );

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Admin.action"
        );
    }
}