package careshift;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.AppLog;
import tool.AuthMail;
import tool.LoginLimiter;
import tool.PasswordUtil;
import tool.Validator;

/**
 * S01 ログイン。
 *
 * URL:
 * /careshift/LoginExecute.action
 *
 * POSTだけを受け付ける。
 *
 * ログインに5回連続で失敗した場合は、
 * 15分間ログインを制限する。
 *
 * メールアドレスまたはパスワードのどちらが
 * 間違っているかは画面に表示しない。
 */
public class LoginExecuteAction extends Action {

    /**
     * 招待トークンに使用できる文字と長さ。
     */
    private static final String INVITE_TOKEN_PATTERN =
            "[A-Za-z0-9_-]{20,100}";

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        /*
         * ログイン処理はPOSTだけを受け付ける。
         */
        if (!"POST".equalsIgnoreCase(
                req.getMethod()
        )) {
            resp.sendError(
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED
            );

            return;
        }

        String email =
                Validator.trim(
                        req.getParameter("email")
                );

        String password =
                Validator.nz(
                        req.getParameter("password")
                );

        String invite =
                normalizeInviteToken(
                        req.getParameter("invite")
                );

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (email.isEmpty()) {
            errors.put(
                    "email",
                    "メールアドレスを入力してください"
            );

        } else if (!Validator.isEmail(email)) {
            errors.put(
                    "email",
                    "メールアドレスの形式が正しくありません"
            );
        }

        if (password.isEmpty()) {
            errors.put(
                    "password",
                    "パスワードを入力してください"
            );
        }

        if (!errors.isEmpty()) {
            back(
                    req,
                    resp,
                    email,
                    invite,
                    errors,
                    null
            );

            return;
        }

        String key =
                email.toLowerCase(
                        java.util.Locale.ROOT
                );

        LoginLimiter limiter =
                LoginLimiter.shared();

        /*
         * 5回連続失敗後、15分間は認証処理を行わない。
         */
        if (limiter.isLocked(key)) {
            AppLog.write(
                    "AUTH",
                    "ログイン制限中のため認証を中断しました"
            );

            back(
                    req,
                    resp,
                    email,
                    invite,
                    errors,
                    "ログインを一時的に制限しています。"
                    + "15分後にお試しいただくか、"
                    + "パスワードを再設定してください。"
            );

            return;
        }

        User user =
                new UserDao().findByEmail(key);

        /*
         * ユーザーが存在しない場合と、
         * パスワードが違う場合は同じメッセージにする。
         *
         * アカウントの存在を推測されないようにする。
         */
        if (user == null
                || !PasswordUtil.verify(
                        password,
                        user.getPasswordHash()
                )) {

            limiter.recordFailure(key);

            AppLog.write(
                    "AUTH",
                    "ログイン認証に失敗しました"
            );

            back(
                    req,
                    resp,
                    email,
                    invite,
                    errors,
                    "メールアドレスまたはパスワードが正しくありません"
            );

            return;
        }

        limiter.recordSuccess(key);

        /*
         * メール未確認の利用者はログインさせない。
         *
         * 確認メールの送信結果にかかわらず、
         * ログインセッションは作成しない。
         */
        if (!user.isEmailVerified()) {
            String message;

            try {
                AuthMail.sendVerification(
                        req,
                        user
                );

                AppLog.write(
                        "AUTH",
                        "メール未確認のため確認メールを再送しました"
                );

                message =
                        "メールアドレスの確認が完了していません。"
                        + "確認メールを再送しました。"
                        + "メール内のリンクから本登録を完了してください。";

            } catch (Exception e) {
                /*
                 * メールアドレスや例外メッセージはログへ出さない。
                 */
                AppLog.write(
                        "ERROR",
                        "メール確認用メールの再送に失敗しました"
                        + "（"
                        + e.getClass().getSimpleName()
                        + "）"
                );

                message =
                        "メールアドレスの確認が完了していません。"
                        + "確認メールの送信に失敗しました。"
                        + "時間をおいて、もう一度ログインをお試しください。";
            }

            back(
                    req,
                    resp,
                    email,
                    invite,
                    errors,
                    message
            );

            return;
        }

        /*
         * セッション固定化攻撃への対策。
         *
         * ログイン前のセッションを破棄し、
         * 新しいセッションへUserを保存する。
         */
        HttpSession oldSession =
                req.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession =
                req.getSession(true);

        newSession.setAttribute(
                "user",
                user
        );

        /*
         * ログイン済み画面をブラウザーキャッシュへ
         * 残しにくくするための基準時刻を保持する。
         */
        newSession.setAttribute(
                "loginAt",
                java.time.Instant.now()
        );

        AppLog.write(
                "AUTH",
                "ログインに成功しました"
        );

        /*
         * 招待リンク経由の場合は、
         * ログイン後に招待確認画面へ戻る。
         */
        if (!invite.isEmpty()) {
            resp.sendRedirect(
                    req.getContextPath()
                    + "/careshift/Invite.action?token="
                    + invite
            );

            return;
        }

        resp.sendRedirect(
                req.getContextPath()
                + "/careshift/main/Menu.action"
        );
    }

    /**
     * 招待トークンを検証する。
     *
     * 想定外の文字や長さの場合は破棄する。
     */
    private String normalizeInviteToken(
            String value
    ) {

        String invite =
                Validator.trim(value);

        if (!invite.matches(
                INVITE_TOKEN_PATTERN
        )) {
            return "";
        }

        return invite;
    }

    /**
     * 入力エラーを保持してログイン画面へ戻す。
     *
     * パスワードは保持・再表示しない。
     */
    private void back(
            HttpServletRequest req,
            HttpServletResponse resp,
            String email,
            String invite,
            Map<String, String> errors,
            String formError
    ) throws Exception {

        req.setAttribute(
                "email",
                email
        );

        req.setAttribute(
                "invite",
                invite
        );

        req.setAttribute(
                "errors",
                errors
        );

        req.setAttribute(
                "formError",
                formError
        );

        req.getRequestDispatcher(
                "/login.jsp"
        ).forward(
                req,
                resp
        );
    }
}