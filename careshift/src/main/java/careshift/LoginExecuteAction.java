package careshift;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.AuthMail;
import tool.LoginLimiter;
import tool.PasswordUtil;
import tool.Validator;

/**
 * S01 ログイン（login.jsp の送信先）。URL: /careshift/LoginExecute.action
 * 設計書の画面項目 email / password のバリデーションと、動作・例外処理を実装する。
 */
public class LoginExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String email = Validator.trim(req.getParameter("email"));
        String password = Validator.nz(req.getParameter("password"));

        // ① 入力チェック（エラーは項目名(name属性)をキーに保持する）
        Map<String, String> errors = new LinkedHashMap<>();
        if (email.isEmpty()) {
            errors.put("email", "メールアドレスを入力してください");
        } else if (!Validator.isEmail(email)) {
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
        String key = email.toLowerCase(java.util.Locale.ROOT);
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
        limiter.recordSuccess(key);

        // ④ メール未確認なら確認メールを再送してログインさせない（設計書の指定）
        if (!user.isEmailVerified()) {
            String msg;
            try {
                AuthMail.sendVerification(req, user);
                msg = "メールアドレスの確認が完了していません。確認メールを再送しました。メール内のリンクから本登録を完了してください。";
            } catch (Exception e) {
                e.printStackTrace();
                msg = "メールアドレスの確認が完了していません。確認メールの送信に失敗しました。時間をおいて、もう一度ログインをお試しください。";
            }
            req.setAttribute("verificationRequired", Boolean.TRUE);
            back(req, resp, email, errors, msg);
            return;
        }

        // ⑤ 成功：セッションを作り直して（セッション固定化攻撃の対策）ユーザーを保存し、メニューへ
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        req.getSession(true).setAttribute("user", user);

        // 招待リンクから来た場合は、招待画面（S04）へ戻す。トークンは英数字・-・_ だけを許可する
        String invite = Validator.trim(req.getParameter("invite"));
        if (invite.matches("[A-Za-z0-9_-]{20,100}")) {
            resp.sendRedirect(req.getContextPath() + "/careshift/Invite.action?token=" + invite);
            return;
        }
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
