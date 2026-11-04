package careshift;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.LoginLimiter;
import tool.PasswordUtil;

/**
 * S01 ログイン（login.jsp の送信先）。URL: /careshift/LoginExecute.action
 * 設計書の画面項目 email / password のバリデーションと、動作・例外処理を実装する。
 */
public class LoginExecuteAction extends Action {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        // ① 入力チェック（エラーは項目名(name属性)をキーに保持する）
        Map<String, String> errors = new LinkedHashMap<>();
        if (email.isEmpty()) {
            errors.put("email", "メールアドレスを入力してください");
        } else if (!EMAIL.matcher(email).matches()) {
            errors.put("email", "メールアドレスの形式が正しくありません");
        }
        if (password.isEmpty()) {
            errors.put("password", "パスワードを入力してください");
        }
        if (!errors.isEmpty()) {
            back(req, resp, email, errors, null);
            return;
        }

        // ② ログイン制限（5回連続失敗で15分）
        String key = email.toLowerCase();
        LoginLimiter limiter = LoginLimiter.shared();
        if (limiter.isLocked(key)) {
            back(req, resp, email, errors,
                    "ログインを一時的に制限しています。15分後にお試しいただくか、パスワードを再設定してください。");
            return;
        }

        // ③ 認証（どちらが違うかは示さない：設計書の指定）
        User user = new UserDao().findByEmail(key);
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            limiter.recordFailure(key);
            back(req, resp, email, errors, "メールアドレスまたはパスワードが正しくありません");
            return;
        }

        // ④ 成功：セッションを作り直して（セッション固定化攻撃の対策）ユーザーを保存し、メニューへ
        limiter.recordSuccess(key);
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        req.getSession(true).setAttribute("user", user);
        resp.sendRedirect(req.getContextPath() + "/careshift/main/Menu.action");
    }

    /** エラー内容を持たせて login.jsp に戻す。 */
    private void back(HttpServletRequest req, HttpServletResponse resp, String email,
            Map<String, String> errors, String formError) throws Exception {
        req.setAttribute("email", email); // パスワードは再表示しない
        req.setAttribute("errors", errors);
        req.setAttribute("formError", formError);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
