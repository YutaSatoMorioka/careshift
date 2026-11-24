package careshift.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;

/**
 * 旧管理者ダッシュボードURLとの互換用Action。
 *
 * 旧URL:
 * /careshift/admin/AdminDashboard.action
 *
 * 正式URL:
 * /careshift/main/Admin.action
 *
 * 旧BackupService方式の管理画面は表示せず、
 * ログインユーザーとAdminSupportを使用する
 * 正式なS90管理画面へ移動する。
 */
public class AdminDashboardAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        /*
         * 旧管理者ログインで作られたセッション属性は削除する。
         * 正式な管理機能では通常ログイン中のUserと、
         * AdminSupportの管理者メール設定を使用する。
         */
        HttpSession session =
                req.getSession(false);

        if (session != null) {
            session.removeAttribute(
                    "careshiftAdmin"
            );
        }

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Admin.action"
        );
    }
}