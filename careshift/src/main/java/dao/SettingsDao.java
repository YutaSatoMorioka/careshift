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
}
