package careshift;

import bean.EmailToken;
import dao.TokenDao;
import dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.TokenUtil;

/** S02 本登録リンク（確認メール内のリンク）。URL: /careshift/VerifyEmail.action?token=... */
public class VerifyEmailAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String token = req.getParameter("token");
        TokenDao tokenDao = new TokenDao();
        EmailToken t = (token == null || token.isEmpty()) ? null
                : tokenDao.find(TokenUtil.hash(token), EmailToken.VERIFY);

        if (t == null) {
            showResult(req, resp, "リンクが正しくありません",
                    "確認用のリンクが正しくありません。メールに記載されたリンクをそのまま開いてください。",
                    "/login.jsp", "ログインへ");
            return;
        }
        if (t.isExpired() && !t.isUsed()) {
            bean.User user = new UserDao().findById(t.getUserId());
            req.setAttribute("email", user == null ? "" : user.getEmail());
            req.getRequestDispatcher("/verify_expired.jsp").forward(req, resp);
            return;
        }
        if (t.isUsed() || !tokenDao.consume(t.getId())) {
            showResult(req, resp, "このリンクは使用できません",
                    "すでに本登録が完了しているか、新しい確認メールが送られて無効になっています。ログインをお試しください。",
                    "/login.jsp", "ログインへ");
            return;
        }

        new UserDao().markVerified(t.getUserId());
        showResult(req, resp, "本登録が完了しました", "ご登録ありがとうございます。ログインしてご利用ください。",
                "/login.jsp", "ログインへ");
    }
}
