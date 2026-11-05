package careshift;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AuthMail;

/** S03 メール内の再設定リンク。有効なら新パスワード入力画面を表示。URL: /careshift/PasswordResetForm.action?token=... */
public class PasswordResetFormAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String token = req.getParameter("token");
        if (AuthMail.findValidResetToken(token) == null) {
            showResult(req, resp, "リンクが使用できません",
                    "リンクが正しくないか、使用済み、または有効期限（1時間）が切れています。もう一度、再設定メールを請求してください。",
                    "/password_reset.jsp", "再設定メールを請求する");
            return;
        }
        req.setAttribute("token", token);
        req.getRequestDispatcher("/password_reset_form.jsp").forward(req, resp);
    }
}
