package careshift.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧復元URLとの互換用Action。
 *
 * 旧URL:
 * /careshift/admin/AdminRestore.action
 *
 * 正式URL:
 * /careshift/main/AdminRestore.action
 *
 * 旧BackupServiceの復元処理は実行しない。
 *
 * 正式な復元処理では次を実施する。
 *
 * ・通常ログインユーザーの管理者権限確認
 * ・現在のログインパスワードによる再認証
 * ・バックアップ圧縮ファイルの破損確認
 * ・復元直前の自動退避
 * ・復元失敗時のロールバック
 */
public class AdminRestoreAction extends Action {

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
                        "旧管理画面からは復元を実行できません。"
                        + "通常ログイン後のS90管理者画面を使用してください。"
                );

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Admin.action"
        );
    }
}