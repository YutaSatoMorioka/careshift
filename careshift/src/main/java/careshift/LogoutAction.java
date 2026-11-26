package careshift;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.AppLog;

/**
 * ログアウト。
 *
 * URL:
 * /careshift/Logout.action
 *
 * POSTだけを受け付ける。
 *
 * ログアウト時はセッション全体を破棄し、
 * 新しいセッションへ完了メッセージだけを設定して
 * ログイン画面へリダイレクトする。
 */
public class LogoutAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        /*
         * GETアクセスではログアウトさせない。
         *
         * 外部ページや画像などからログアウトURLを
         * 読み込まれた場合に、意図せずセッションが破棄されることを防ぐ。
         */
        if (!"POST".equalsIgnoreCase(
                req.getMethod()
        )) {
            resp.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED
            );

            return;
        }

        HttpSession session =
                req.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        /*
         * ログアウト完了メッセージ専用の新しいセッションを作成する。
         *
         * ログイン済みユーザー情報は引き継がない。
         */
        HttpSession messageSession =
                req.getSession(true);

        messageSession.setAttribute(
                "flash",
                "ログアウトしました。"
        );

        /*
         * 利用者名、メールアドレス、セッションIDはログへ記録しない。
         */
        AppLog.write(
                "AUTH",
                "ログアウトしました"
        );

        /*
         * POST後に直接JSPへforwardせず、
         * リダイレクトして再送信を防止する。
         */
        resp.sendRedirect(
                req.getContextPath()
                + "/login.jsp"
        );
    }
}