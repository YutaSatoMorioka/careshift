package careshift;

import java.sql.SQLException;
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
import tool.PasswordUtil;
import tool.Validator;

/**
 * S02 新規登録。
 *
 * URL:
 * /careshift/RegisterExecute.action
 *
 * 入力を検証し、メール未確認状態でユーザーを登録する。
 * 登録後は、24時間有効なメール確認リンクを送信する。
 */
public class RegisterExecuteAction extends Action {

    private static final int MAX_DISPLAY_NAME_LENGTH =
            30;

    private static final int MAX_EMAIL_LENGTH =
            254;

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

        String displayName =
                Validator.trim(
                        req.getParameter("displayName")
                );

        String email =
                Validator.trim(
                        req.getParameter("email")
                );

        String password =
                Validator.nz(
                        req.getParameter("password")
                );

        String passwordConfirm =
                Validator.nz(
                        req.getParameter("passwordConfirm")
                );

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (displayName.isEmpty()) {
            errors.put(
                    "displayName",
                    "表示名を入力してください"
            );

        } else if (displayName.length()
                > MAX_DISPLAY_NAME_LENGTH) {

            errors.put(
                    "displayName",
                    "表示名は30文字以内で入力してください"
            );
        }

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

        if (password.isEmpty()) {
            errors.put(
                    "password",
                    "パスワードを入力してください"
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

        String normalizedEmail =
                email.toLowerCase(
                        Locale.ROOT
                );

        UserDao userDao =
                new UserDao();

        boolean alreadyRegistered =
                false;

        if (!errors.containsKey("email")
                && userDao.findByEmail(
                        normalizedEmail
                ) != null) {

            alreadyRegistered =
                    true;
        }

        long userId =
                -1L;

        if (errors.isEmpty()
                && !alreadyRegistered) {

            try {
                userId =
                        userDao.insert(
                                displayName,
                                normalizedEmail,
                                PasswordUtil.hash(
                                        password
                                )
                        );

            } catch (SQLException e) {

                if ("23505".equals(
                        e.getSQLState()
                )) {
                    alreadyRegistered =
                            true;

                } else {
                    throw e;
                }
            }
        }

        if (!errors.isEmpty()
                || alreadyRegistered) {

            if (alreadyRegistered) {
                errors.put(
                        "email",
                        "このメールアドレスは登録済みです"
                );
            }

            req.setAttribute(
                    "errors",
                    errors
            );

            req.setAttribute(
                    "alreadyRegistered",
                    alreadyRegistered
            );

            req.setAttribute(
                    "displayName",
                    displayName
            );

            req.setAttribute(
                    "email",
                    email
            );

            /*
             * パスワードはrequest属性へ設定しない。
             */
            req.getRequestDispatcher(
                    "/register.jsp"
            ).forward(
                    req,
                    resp
            );

            return;
        }

        boolean mailFailed =
                false;

        try {
            User registeredUser =
                    new User(
                            userId,
                            displayName,
                            normalizedEmail,
                            "",
                            false
                    );

            AuthMail.sendVerification(
                    req,
                    registeredUser
            );

            AppLog.write(
                    "AUTH",
                    "新規登録の確認メールを送信しました"
            );

        } catch (Exception e) {
            mailFailed =
                    true;

            /*
             * メールアドレスや例外メッセージはログへ出さない。
             */
            AppLog.write(
                    "ERROR",
                    "新規登録の確認メール送信に失敗しました"
                    + "（"
                    + e.getClass().getSimpleName()
                    + "）"
            );
        }

        req.setAttribute(
                "mailFailed",
                mailFailed
        );

        req.setAttribute(
                "email",
                normalizedEmail
        );

        req.getRequestDispatcher(
                "/register_done.jsp"
        ).forward(
                req,
                resp
        );
    }
}