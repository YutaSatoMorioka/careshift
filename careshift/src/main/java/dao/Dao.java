package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import tool.PasswordUtil;

/**
 * すべての DAO の親クラス。DB接続と、初回のテーブル作成を担当する。
 *
 * 接続先は既定で「H2 のファイルDB」（ユーザーフォルダ/careshift-db）。
 * PostgreSQL に切り替えるときは、コードを変えず Tomcat の VM 引数で指定する:
 *   -Dcareshift.db.driver=org.postgresql.Driver
 *   -Dcareshift.db.url=jdbc:postgresql://localhost:5432/careshift
 *   -Dcareshift.db.user=...  -Dcareshift.db.password=...
 */
public class Dao {

    private static boolean initialized = false;

    private static String driver() {
        return System.getProperty("careshift.db.driver", "org.h2.Driver");
    }

    private static String url() {
        return System.getProperty("careshift.db.url",
                "jdbc:h2:file:~/careshift-db/careshift;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
    }

    private static String user() {
        return System.getProperty("careshift.db.user", "sa");
    }

    private static String password() {
        return System.getProperty("careshift.db.password", "");
    }

    private static Connection open() throws Exception {
        // Tomcat では WEB-INF/lib のドライバを自動認識しないため、明示的に読み込む
        Class.forName(driver());
        return DriverManager.getConnection(url(), user(), password());
    }

    /** DB接続を返す。呼び出し側で try-with-resources を使って閉じること。 */
    protected Connection getConnection() throws Exception {
        init();
        return open();
    }

    /** 初回だけテーブルを作り、ユーザーが0人ならデモユーザーを入れる。 */
    private static synchronized void init() throws Exception {
        if (initialized) {
            return;
        }
        try (Connection con = open()) {
            try (Statement st = con.createStatement()) {
                for (String ddl : Schema.DDL) {
                    st.execute(ddl);
                }
            }
            seedDemoUser(con);
        }
        initialized = true;
    }

    /** 動作確認用: demo@example.com / password123（不要になったらこのメソッドごと削除してよい） */
    private static void seedDemoUser(Connection con) throws Exception {
        try (Statement st = con.createStatement();
                java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
            rs.next();
            if (rs.getLong(1) > 0) {
                return;
            }
        }
        String sql = "INSERT INTO users (display_name, email, password_hash, email_verified)"
                + " VALUES (?, ?, ?, TRUE)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "デモ太郎");
            ps.setString(2, "demo@example.com");
            ps.setString(3, PasswordUtil.hash("password123"));
            ps.executeUpdate();
        }
    }

    /** 使っているDBが H2（既定）か。バックアップ・復元（S90）は H2 のときだけ使える。 */
    public static boolean isH2() {
        return url().startsWith("jdbc:h2:");
    }

    /** 復元のあとに呼ぶ。テーブルの不足（古いバックアップ）を補うため、初期化をやり直す。 */
    public static synchronized void reinit() throws Exception {
        initialized = false;
        init();
    }

    /** アプリ停止時に呼ぶ。H2のファイルDBを閉じて、再読み込み時のロック競合を防ぐ。 */
    public static synchronized void shutdown() {
        if (!initialized || !url().startsWith("jdbc:h2:file:")) {
            return;
        }
        try (Connection con = open(); Statement st = con.createStatement()) {
            st.execute("SHUTDOWN");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            initialized = false;
        }
    }
}
