package dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bean.User;
import tool.PasswordUtil;

/**
 * ユーザーのDAO。
 * いまは動作確認用に、メモリ上のデモユーザー（demo@example.com / password123）だけを返す。
 * DB設計が固まったら、findByEmail の中身を JDBC（SELECT）に書き換える。
 * 呼び出す側（LoginExecuteAction）は変更しなくてよい。
 */
public class UserDao {

    private static final Map<String, User> USERS = new ConcurrentHashMap<>();

    static {
        USERS.put("admin@careshift.com",
                new User(1L, "admin", "admin@careshift.com", PasswordUtil.hash("admin")));
    }

    /** メールアドレス（小文字）でユーザーを検索する。いなければ null。 */
    public User findByEmail(String email) {
        return USERS.get(email);
    }
}
