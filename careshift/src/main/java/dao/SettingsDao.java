package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * user_settingsテーブルの操作。
 *
 * 設定行が存在しない場合の初期値:
 *
 * メール通知:
 * オフ
 *
 * 期限リマインド:
 * オン
 */
public class SettingsDao extends Dao {

    /**
     * 期限リマインドが有効か取得する。
     *
     * 設定行が存在しない場合は初期値のtrueを返す。
     */
    public boolean isReminderOn(
            long userId
    ) throws Exception {

        String sql =
                "SELECT reminder"
                + " FROM user_settings"
                + " WHERE user_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return !rs.next()
                        || rs.getBoolean("reminder");
            }
        }
    }

    /**
     * メール通知が有効か取得する。
     *
     * 設定行が存在しない場合は初期値のfalseを返す。
     */
    public boolean isEmailNoticeOn(
            long userId
    ) throws Exception {

        String sql =
                "SELECT email_notice"
                + " FROM user_settings"
                + " WHERE user_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next()
                        && rs.getBoolean("email_notice");
            }
        }
    }

    /**
     * 通知設定を保存する。
     *
     * 設定行が存在する場合:
     * UPDATE
     *
     * 設定行が存在しない場合:
     * INSERT
     */
    public void save(
            long userId,
            boolean emailNotice,
            boolean reminder
    ) throws Exception {

        try (
            Connection con =
                    getConnection()
        ) {
            String updateSql =
                    "UPDATE user_settings"
                    + " SET email_notice = ?,"
                    + " reminder = ?"
                    + " WHERE user_id = ?";

            int updated;

            try (
                PreparedStatement ps =
                        con.prepareStatement(
                                updateSql
                        )
            ) {
                ps.setBoolean(
                        1,
                        emailNotice
                );

                ps.setBoolean(
                        2,
                        reminder
                );

                ps.setLong(
                        3,
                        userId
                );

                updated =
                        ps.executeUpdate();
            }

            if (updated > 0) {
                return;
            }

            String insertSql =
                    "INSERT INTO user_settings"
                    + " (user_id, email_notice, reminder)"
                    + " VALUES (?, ?, ?)";

            try (
                PreparedStatement ps =
                        con.prepareStatement(
                                insertSql
                        )
            ) {
                /*
                 * SQLの列順:
                 *
                 * 1. user_id
                 * 2. email_notice
                 * 3. reminder
                 */
                ps.setLong(
                        1,
                        userId
                );

                ps.setBoolean(
                        2,
                        emailNotice
                );

                ps.setBoolean(
                        3,
                        reminder
                );

                ps.executeUpdate();
            }
        }
    }
}