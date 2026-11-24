package careshift;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * 旧管理者ログインURLとの互換用Action。
 *
 * 旧URL:
 * /careshift/AdminLogin.action
 *
 * 正式な管理者認証:
 *
 * 1. 通常のCare Shiftログインを行う
 * 2. AdminSupportがメールアドレスを確認する
 * 3. S90管理者画面を開く
 *
 * 管理者のメールアドレスは次で設定する。
 *
 * VM引数:
 * -Dcareshift.admin.emails=admin@example.com
 *
 * または環境変数:
 * CARESHIFT_ADMIN_EMAILS=admin@example.com
 */
public class AdminLoginAction extends Action {

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
                        "管理者機能は通常のCare Shiftアカウントでログインして使用してください。"
                );

        resp.sendRedirect(
                req.getContextPath()
                + "/login.jsp"
        );
    }
}