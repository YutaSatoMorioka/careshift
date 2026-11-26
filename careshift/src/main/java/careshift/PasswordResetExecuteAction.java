package careshift;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.EmailToken;
import bean.User;
import dao.TokenDao;
import dao.TokenDao.PasswordResetResult;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AppLog;
import tool.AuthMail;
import tool.LoginLimiter;
import tool.PasswordUtil;
import tool.TokenUtil;
import tool.Validator;

/**
 * S03 新しいパスワードの保存。
 *
 * URL:
 * /careshift/PasswordResetExecute.action
 *
 * POSTだけを受け付ける。
 *
 * 新しいパスワードは8～128文字で、
 * 確認入力と一致している必要がある。
 *
 * パスワード更新とトークン使用済み化は、
 * TokenDaoの同一トランザクション内で実行する。
 */
public class PasswordResetExecuteAction extends Action {

    private static final int MIN_PASSWORD_LENGTH =
            8;

    private static final int MAX_PASSWORD_LENGTH =
            128;

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

        String token =
                Validator.nz(
                        req.getParameter("token")
                );

        String password =
                Validator.nz(
                        req.getParameter("password")
                );

        String passwordConfirm =
                Validator.nz(
                        req.getParameter(
                                "passwordConfirm"
                        )
                );

        /*
         * 入力画面の再表示前にも、トークンの有効性を確認する。
         */
        EmailToken currentToken =
                AuthMail.findValidResetToken(
                        token
                );

        if (currentToken == null) {
            showInvalidLink(
                    req,
                    resp
            );

            return;
        }

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (password.isEmpty()) {
            errors.put(
                    "password",
                    "新しいパスワードを入力してください"
            );

        } else if (password.length()
                < MIN_PASSWORD_LENGTH) {

            errors.put(
                    "password",
                    "パスワードは8文字以上で入力してください"
            );

        } else if (password.length()
                > MAX_PASSWORD_LENGTH) {

            errors.put(
                    "password",
                    "パスワードは128文字以内で入力してください"
            );
        }

        if (passwordConfirm.isEmpty()) {
            errors.put(
                    "passwordConfirm",
                    "確認用パスワードを入力してください"
            );

        } else if (!password.equals(
                passwordConfirm
        )) {
            errors.put(
                    "passwordConfirm",
                    "パスワードが一致しません"
            );
        }

        if (!errors.isEmpty()) {
            req.setAttribute(
                    "errors",
                    errors
            );

            req.setAttribute(
                    "token",
                    token
            );

            req.getRequestDispatcher(
                    "/password_reset_form.jsp"
            ).forward(
                    req,
                    resp
            );

            return;
        }

        String passwordHash =
                PasswordUtil.hash(
                        password
                );

        /*
         * パスワードとトークンを1つのトランザクションで更新する。
         *
         * この中でもトークンを行ロックし、
         * 使用済み・期限切れを再確認する。
         */
        PasswordResetResult result =
                new TokenDao()
                        .resetPassword(
                                TokenUtil.hash(token),
                                EmailToken.RESET,
                                passwordHash
                        );

        if (!result.isSuccess()) {
            showInvalidLink(
                    req,
                    resp
            );

            return;
        }

        /*
         * パスワード変更に成功した場合、
         * ログイン失敗回数とロックを解除する。
         */
        try {
            User user =
                    new UserDao().findById(
                            result.getUserId()
                    );

            if (user != null) {
                LoginLimiter.shared()
                        .recordSuccess(
                                user.getEmail()
                        );
            }

        } catch (Exception e) {
            /*
             * ログイン制限解除に失敗しても、
             * パスワード変更自体は完了済み。
             */
            AppLog.write(
                    "ERROR",
                    "パスワード再設定後のログイン制限解除に失敗しました"
                    + "（"
                    + e.getClass()
                            .getSimpleName()
                    + "）"
            );
        }

        AppLog.write(
                "AUTH",
                "パスワードを再設定しました"
        );

        showResult(
                req,
                resp,
                "パスワードを再設定しました",
                "新しいパスワードでログインしてください。",
                "/login.jsp",
                "ログインへ"
        );
    }

    /**
     * 不正、使用済み、期限切れのトークンに対して、
     * 同じ安全な案内を表示する。
     */
    private void showInvalidLink(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        showResult(
                req,
                resp,
                "リンクが使用できません",
                "リンクが正しくないか、使用済み、"
                + "または有効期限（1時間）が切れています。"
                + "もう一度、再設定メールを請求してください。",
                "/password_reset.jsp",
                "再設定メールを請求する"
        );
    }
}