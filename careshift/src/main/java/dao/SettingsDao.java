package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** user_settings テーブルの操作（通知設定）。行がなければ初期値（メール通知オフ・期限リマインドオン）。 */
public class SettingsDao extends Dao {

    public boolean isReminderOn(long userId) throws Exception {
        String sql = "SELECT reminder FROM user_settings WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next() || rs.getBoolean(1);
            }
        }
    }

    public boolean isEmailNoticeOn(long userId) throws Exception {
        String sql = "SELECT email_notice FROM user_settings WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    /** 通知設定を保存する（なければ作る）。 */
    public void save(long userId, boolean emailNotice, boolean reminder) throws Exception {
        try (Connection con = getConnection()) {
            int updated;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE user_settings SET email_notice = ?, reminder = ? WHERE user_id = ?")) {
                ps.setBoolean(1, emailNotice);
                ps.setBoolean(2, reminder);
                ps.setLong(3, userId);
                updated = ps.executeUpdate();
            }
            if (updated == 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO user_settings (user_id, email_notice, reminder) VALUES (?, ?, ?)")) {
                    ps.setLong(1, userId);
                    ps.setBoolean(2, emailNotice);
                    ps.setBoolean(3, reminder);
                    ps.executeUpdate();
                }
            }
        }
    }
}
