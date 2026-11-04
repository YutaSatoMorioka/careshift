package careshift;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AuthMail;
import tool.PasswordUtil;
import tool.Validator;

/**
 * S02 新規登録（register.jsp の送信先）。URL: /careshift/RegisterExecute.action
 * 検証 → 登録（メール未確認）→ 確認メール送信 → 送信案内。
 */
public class RegisterExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String displayName = Validator.trim(req.getParameter("displayName"));
        String email = Validator.trim(req.getParameter("email"));
        String password = Validator.nz(req.getParameter("password"));
        String passwordConfirm = Validator.nz(req.getParameter("passwordConfirm"));

        // ① 入力チェック（設計書 S02 の画面項目・代替フローどおり）
        Map<String, String> errors = new LinkedHashMap<>();
        if (displayName.isEmpty()) {
            errors.put("displayName", "表示名を入力してください");
        } else if (displayName.length() > 30) {
            errors.put("displayName", "表示名は30文字以内で入力してください");
        }
        if (email.isEmpty()) {
            errors.put("email", "メールアドレスを入力してください");
        } else if (!Validator.isEmail(email)) {
            errors.put("email", "メールアドレスの形式が正しくありません");
        }
        if (password.isEmpty()) {
            errors.put("password", "パスワードを入力してください");
        } else if (password.length() < 8) {
            errors.put("password", "パスワードは8文字以上で入力してください");
        }
        if (passwordConfirm.isEmpty()) {
            errors.put("passwordConfirm", "確認用パスワードを入力してください");
        } else if (!password.equals(passwordConfirm)) {
            errors.put("passwordConfirm", "パスワードが一致しません");
        }

        // ② 登録済みメールか
        String key = email.toLowerCase();
        UserDao userDao = new UserDao();
        boolean alreadyRegistered = false;
        if (!errors.containsKey("email") && userDao.findByEmail(key) != null) {
            alreadyRegistered = true;
        }

        // ③ 登録（同時登録で重複した場合は DB の一意制約で検知する）
        long userId = -1;
        if (errors.isEmpty() && !alreadyRegistered) {
            try {
                userId = userDao.insert(displayName, key, PasswordUtil.hash(password));
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) { // 一意制約違反
                    alreadyRegistered = true;
                } else {
                    throw e;
                }
            }
        }

        if (!errors.isEmpty() || alreadyRegistered) {
            if (alreadyRegistered) {
                errors.put("email", "このメールアドレスは登録済みです");
            }
            req.setAttribute("errors", errors);
            req.setAttribute("alreadyRegistered", alreadyRegistered);
            req.setAttribute("displayName", displayName);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/register.jsp").forward(req, resp); // パスワードは再表示しない
            return;
        }

        // ④ 確認メール送信。失敗しても登録は残す（ログイン時に再送できる）
        boolean mailFailed = false;
        try {
            AuthMail.sendVerification(req, new User(userId, displayName, key, "", false));
        } catch (Exception e) {
            e.printStackTrace();
            mailFailed = true;
        }
        req.setAttribute("mailFailed", mailFailed);
        req.setAttribute("email", key);
        req.getRequestDispatcher("/register_done.jsp").forward(req, resp);
    }
}
