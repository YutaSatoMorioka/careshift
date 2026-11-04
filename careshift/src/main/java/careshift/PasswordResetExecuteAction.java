package careshift;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.EmailToken;
import bean.User;
import dao.TokenDao;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AuthMail;
import tool.LoginLimiter;
import tool.PasswordUtil;
import tool.Validator;

/**
 * S03 新しいパスワードの保存（password_reset_form.jsp の送信先）。URL: /careshift/PasswordResetExecute.action
 * 新パスワードは8文字以上で、確認入力と一致させる（設計書の指定）。リンクは一度限り。
 */
public class PasswordResetExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String token = Validator.nz(req.getParameter("token"));
        String password = Validator.nz(req.getParameter("password"));
        String passwordConfirm = Validator.nz(req.getParameter("passwordConfirm"));

        EmailToken t = AuthMail.findValidResetToken(token);
        if (t == null) {
            showInvalidLink(req, resp);
            return;
        }

        Map<String, String> errors = new LinkedHashMap<>();
        if (password.isEmpty()) {
            errors.put("password", "新しいパスワードを入力してください");
        } else if (password.length() < 8) {
            errors.put("password", "パスワードは8文字以上で入力してください");
        }
        if (passwordConfirm.isEmpty()) {
            errors.put("passwordConfirm", "確認用パスワードを入力してください");
        } else if (!password.equals(passwordConfirm)) {
            errors.put("passwordConfirm", "パスワードが一致しません");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("token", token);
            req.getRequestDispatcher("/password_reset_form.jsp").forward(req, resp);
            return;
        }

        // 一度限り：先に使用済みにできた人だけが変更できる
        if (!new TokenDao().consume(t.getId())) {
            showInvalidLink(req, resp);
            return;
        }

        UserDao userDao = new UserDao();
        userDao.updatePassword(t.getUserId(), PasswordUtil.hash(password));
        userDao.markVerified(t.getUserId()); // メールを受け取れた＝本人確認ができたため

        User user = userDao.findById(t.getUserId());
        if (user != null) {
            LoginLimiter.shared().recordSuccess(user.getEmail()); // ログイン制限も解除
        }

        showResult(req, resp, "パスワードを再設定しました", "新しいパスワードでログインしてください。",
                "/login.jsp", "ログインへ");
    }

    private void showInvalidLink(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        showResult(req, resp, "リンクが使用できません",
                "リンクが正しくないか、使用済み、または有効期限（1時間）が切れています。もう一度、再設定メールを請求してください。",
                "/password_reset.jsp", "再設定メールを請求する");
    }
}
