package careshift;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AppLog;
import tool.AuthMail;
import tool.Validator;

/**
 * S03 パスワード再設定メールの請求。
 *
 * URL:
 * /careshift/PasswordResetRequest.action
 *
 * POSTだけを受け付ける。
 *
 * メールアドレスが登録済みかどうかは画面で明かさず、
 * 常に同じ結果メッセージを表示する。
 */
public class PasswordResetRequestAction extends Action {

    private static final int MAX_EMAIL_LENGTH =
            254;

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

        String email =
                Validator.trim(
                        req.getParameter("email")
                );

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (email.isEmpty()) {
            errors.put(
                    "email",
                    "メールアドレスを入力してください"
            );

        } else if (email.length()
                > MAX_EMAIL_LENGTH) {

            errors.put(
                    "email",
                    "メールアドレスは254文字以内で入力してください"
            );

        } else if (!Validator.isEmail(email)) {
            errors.put(
                    "email",
                    "メールアドレスの形式が正しくありません"
            );
        }

        if (!errors.isEmpty()) {
            req.setAttribute(
                    "errors",
                    errors
            );

            req.setAttribute(
                    "email",
                    email
            );

            req.getRequestDispatcher(
                    "/password_reset.jsp"
            ).forward(
                    req,
                    resp
            );

            return;
        }

        String normalizedEmail =
                email.toLowerCase(
                        Locale.ROOT
                );

        User user =
                new UserDao().findByEmail(
                        normalizedEmail
                );

        if (user != null) {
            try {
                AuthMail.sendReset(
                        req,
                        user
                );

                AppLog.write(
                        "AUTH",
                        "パスワード再設定メールを送信しました"
                );

            } catch (Exception e) {
                /*
                 * アカウントの有無やメール送信失敗を
                 * 利用者向け画面では区別しない。
                 *
                 * ログにもメールアドレスや例外メッセージを残さない。
                 */
                AppLog.write(
                        "ERROR",
                        "パスワード再設定メールの送信に失敗しました"
                        + "（"
                        + e.getClass()
                                .getSimpleName()
                        + "）"
                );
            }
        }

        showResult(
                req,
                resp,
                "再設定メールを送信しました",
                "登録されているメールアドレスの場合、"
                + "再設定の手順を記載したメールを送信しました。"
                + "リンクは1時間有効で、一度だけ使えます。"
                + "届かない場合は迷惑メールフォルダーもご確認ください。",
                "/login.jsp",
                "ログインへ戻る"
        );
    }
}