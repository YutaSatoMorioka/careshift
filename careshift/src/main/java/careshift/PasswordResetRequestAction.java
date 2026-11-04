package careshift;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.User;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AuthMail;
import tool.Validator;

/**
 * S03 パスワード再設定の依頼（password_reset.jsp の送信先）。URL: /careshift/PasswordResetRequest.action
 * 登録済みかどうかは画面で明かさず、常に同じメッセージを表示する（設計書の指定）。
 */
public class PasswordResetRequestAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String email = Validator.trim(req.getParameter("email"));

        Map<String, String> errors = new LinkedHashMap<>();
        if (email.isEmpty()) {
            errors.put("email", "メールアドレスを入力してください");
        } else if (!Validator.isEmail(email)) {
            errors.put("email", "メールアドレスの形式が正しくありません");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("email", email);
            req.getRequestDispatcher("/password_reset.jsp").forward(req, resp);
            return;
        }

        User user = new UserDao().findByEmail(email.toLowerCase());
        if (user != null) {
            try {
                AuthMail.sendReset(req, user);
            } catch (Exception e) {
                e.printStackTrace(); // 失敗しても画面の表示は変えない
            }
        }

        showResult(req, resp, "再設定メールを送信しました",
                "登録されているメールアドレスの場合、再設定の手順を記載したメールを送信しました。"
                        + "リンクは1時間有効で、一度だけ使えます。届かない場合は迷惑メールフォルダもご確認ください。",
                "/login.jsp", "ログインへ戻る");
    }
}
