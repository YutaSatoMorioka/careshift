package tool;

import java.time.LocalDateTime;

import bean.EmailToken;
import bean.User;
import dao.TokenDao;
import jakarta.servlet.http.HttpServletRequest;

/** 認証まわりのメール（本登録確認・パスワード再設定）の作成と送信。 */
public final class AuthMail {

    private AuthMail() {}

    /** 本登録確認メール（リンクは24時間有効）。以前の確認リンクは無効になる。 */
    public static void sendVerification(HttpServletRequest req, User user) throws Exception {
        String token = TokenUtil.generate();
        new TokenDao().issue(user.getId(), EmailToken.VERIFY, TokenUtil.hash(token),
                LocalDateTime.now().plusHours(24));
        String link = Mailer.baseUrl(req) + "/careshift/VerifyEmail.action?token=" + token;
        Mailer.send(user.getEmail(), "【Care Shift】メールアドレスの確認",
                user.getDisplayName() + " さん\n\n"
                        + "Care Shift にご登録ありがとうございます。\n"
                        + "次のリンクを開くと、本登録が完了します（24時間有効）。\n\n"
                        + link + "\n\n"
                        + "心当たりがない場合は、このメールを破棄してください。");
    }

    /** パスワード再設定メール（リンクは1時間有効・一度限り）。以前の再設定リンクは無効になる。 */
    public static void sendReset(HttpServletRequest req, User user) throws Exception {
        String token = TokenUtil.generate();
        new TokenDao().issue(user.getId(), EmailToken.RESET, TokenUtil.hash(token),
                LocalDateTime.now().plusHours(1));
        String link = Mailer.baseUrl(req) + "/careshift/PasswordResetForm.action?token=" + token;
        Mailer.send(user.getEmail(), "【Care Shift】パスワード再設定のご案内",
                user.getDisplayName() + " さん\n\n"
                        + "パスワード再設定のご依頼を受け付けました。\n"
                        + "次のリンクから新しいパスワードを設定してください（1時間有効・一度のみ使用可）。\n\n"
                        + link + "\n\n"
                        + "心当たりがない場合は、このメールを破棄してください。パスワードは変更されません。");
    }

    /** 再設定トークンが有効（存在・未使用・期限内）ならそのトークンを、そうでなければ null を返す。 */
    public static EmailToken findValidResetToken(String token) throws Exception {
        if (token == null || token.isEmpty()) {
            return null;
        }
        EmailToken t = new TokenDao().find(TokenUtil.hash(token), EmailToken.RESET);
        return (t == null || t.isUsed() || t.isExpired()) ? null : t;
    }
}
