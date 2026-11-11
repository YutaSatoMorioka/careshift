package tool;

import bean.User;
import dao.SettingsDao;
import dao.UserDao;

/**
 * S20「メール通知」がオンの人には、アプリ内の通知と同じ内容をメールでも送る。
 * いまの Mailer は開発用（コンソールに出力するだけ）なので、実際のメールは送られない。
 * 送信に失敗しても、アプリ内の通知は成功したままにする。
 */
public final class EmailNotifier {

    private EmailNotifier() {}

    public static void notify(long userId, String message) {
        try {
            if (!new SettingsDao().isEmailNoticeOn(userId)) {
                return;
            }
            User user = new UserDao().findById(userId);
            if (user == null) {
                return;
            }
            Mailer.send(user.getEmail(), "【Care Shift】新しい通知があります",
                    user.getDisplayName() + " さん\n\n" + message + "\n\nくわしくは、Care Shift を開いて確認してください。\n"
                            + "（メール通知は、設定画面でオフにできます）");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
