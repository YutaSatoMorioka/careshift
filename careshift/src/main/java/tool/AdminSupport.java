package tool;

import java.util.HashSet;
import java.util.Set;

import bean.User;

/**
 * 管理者の判定（S90）。管理者にするメールアドレスは、画面からは変えられない設定で決める。
 *   Tomcat の VM 引数:  -Dcareshift.admin.emails=admin@example.com,other@example.com
 *   または環境変数:     CARESHIFT_ADMIN_EMAILS=admin@example.com
 * 設定がなければ、誰も管理者にならない。
 */
public final class AdminSupport {

    private AdminSupport() {}

    public static Set<String> adminEmails() {
        String v = System.getProperty("careshift.admin.emails");
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv("CARESHIFT_ADMIN_EMAILS");
        }
        Set<String> set = new HashSet<>();
        if (v != null) {
            for (String e : v.split(",")) {
                if (!e.trim().isEmpty()) set.add(e.trim().toLowerCase());
            }
        }
        return set;
    }

    public static boolean isAdmin(User user) {
        return user != null && user.isEmailVerified() && adminEmails().contains(user.getEmail().toLowerCase());
    }
}
