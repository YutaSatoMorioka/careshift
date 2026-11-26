package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * すべてのDAOの親クラス。
 *
 * データベース接続と、初回のテーブル作成を担当する。
 *
 * 既定の接続先:
 *
 * H2ファイルデータベース
 * ユーザーフォルダー/careshift-db
 *
 * PostgreSQLへ切り替える場合は、
 * TomcatのVM引数または起動時のシステムプロパティで
 * 接続情報を指定する。
 *
 * 例:
 *
 * -Dcareshift.db.driver=org.postgresql.Driver
 * -Dcareshift.db.url=jdbc:postgresql://localhost:5432/careshift
 * -Dcareshift.db.user=careshift
 * -Dcareshift.db.password=設定したパスワード
 */
public class Dao {

    /**
     * Schema.DDLによる初期化が完了しているか。
     */
    private static volatile boolean initialized =
            false;

    /**
     * データベースドライバー名を取得する。
     */
    private static String driver() {

        return System.getProperty(
                "careshift.db.driver",
                "org.h2.Driver"
        );
    }

    /**
     * データベースURLを取得する。
     */
    private static String url() {

        return System.getProperty(
                "careshift.db.url",
                "jdbc:h2:file:~/careshift-db/careshift"
                + ";MODE=PostgreSQL"
                + ";DATABASE_TO_LOWER=TRUE"
        );
    }

    /**
     * データベース接続ユーザー名を取得する。
     */
    private static String user() {

        return System.getProperty(
                "careshift.db.user",
                "sa"
        );
    }

    /**
     * データベース接続パスワードを取得する。
     */
    private static String password() {

        return System.getProperty(
                "careshift.db.password",
                ""
        );
    }

    /**
     * データベース接続を新規作成する。
     *
     * TomcatではWEB-INF/lib内のJDBCドライバーが
     * 自動認識されない場合があるため、
     * Class.forName()で明示的に読み込む。
     */
    private static Connection open()
            throws Exception {

        Class.forName(
                driver()
        );

        return DriverManager.getConnection(
                url(),
                user(),
                password()
        );
    }

    /**
     * 初期化済みのデータベース接続を返す。
     *
     * 呼び出し側はtry-with-resourcesを使用して
     * Connectionを閉じること。
     */
    protected Connection getConnection()
            throws Exception {

        init();

        return open();
    }

    /**
     * アプリケーション起動後、最初の接続時に
     * Schema.DDLを実行する。
     *
     * CREATE TABLE IF NOT EXISTSなど、
     * Schema.DDL側の定義に従って不足テーブルを作成する。
     *
     * デモユーザーや固定パスワードのアカウントは
     * 自動作成しない。
     */
    private static synchronized void init()
            throws Exception {

        if (initialized) {
            return;
        }

        try (
            Connection con =
                    open();

            Statement statement =
                    con.createStatement()
        ) {
            for (String ddl : Schema.DDL) {

                if (ddl == null
                        || ddl.trim().isEmpty()) {

                    continue;
                }

                statement.execute(
                        ddl
                );
            }
        }

        initialized =
                true;
    }

    /**
     * 現在使用しているデータベースがH2か判定する。
     *
     * バックアップ・復元機能はH2の場合だけ
     * アプリケーション内から利用できる。
     */
    public static boolean isH2() {

        return url().startsWith(
                "jdbc:h2:"
        );
    }

    /**
     * バックアップからの復元後に、
     * データベース初期化処理を再実行する。
     *
     * 古いバックアップに新しいテーブルが含まれていない場合、
     * Schema.DDLによって不足テーブルを補う。
     */
    public static synchronized void reinit()
            throws Exception {

        initialized =
                false;

        init();
    }

    /**
     * アプリケーション停止時にH2ファイルDBを閉じる。
     *
     * PostgreSQLなど、H2以外のデータベースでは
     * SHUTDOWNコマンドを実行しない。
     */
    public static synchronized void shutdown() {

        if (!initialized
                || !url().startsWith(
                        "jdbc:h2:file:"
                )) {

            return;
        }

        try (
            Connection con =
                    open();

            Statement statement =
                    con.createStatement()
        ) {
            statement.execute(
                    "SHUTDOWN"
            );

        } catch (Exception e) {
            /*
             * アプリ停止時の処理なので、
             * ここでは例外を再送出しない。
             *
             * 接続情報やDBパスワードは出力しない。
             */
            System.err.println(
                    "Care ShiftのH2データベースを"
                    + "正常に終了できませんでした。"
            );

        } finally {
            initialized =
                    false;
        }
    }
}