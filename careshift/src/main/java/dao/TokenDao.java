package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import bean.EmailToken;

/** email_tokens テーブルの操作。 */
public class TokenDao extends Dao {

    /** 同じ目的の未使用トークンを無効にしてから、新しいトークンを保存する。 */
    public void issue(long userId, String purpose, String tokenHash, LocalDateTime expiresAt) throws Exception {
        try (Connection con = getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE email_tokens SET used = TRUE WHERE user_id = ? AND purpose = ? AND used = FALSE")) {
                ps.setLong(1, userId);
                ps.setString(2, purpose);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO email_tokens (user_id, purpose, token_hash, expires_at) VALUES (?, ?, ?, ?)")) {
                ps.setLong(1, userId);
                ps.setString(2, purpose);
                ps.setString(3, tokenHash);
                ps.setTimestamp(4, Timestamp.valueOf(expiresAt));
                ps.executeUpdate();
            }
        }
    }

    /** ハッシュからトークンを探す（期限切れ・使用済みも返す。判定は呼び出し側）。いなければ null。 */
    public EmailToken find(String tokenHash, String purpose) throws Exception {
        String sql = "SELECT id, user_id, purpose, expires_at, used FROM email_tokens"
                + " WHERE token_hash = ? AND purpose = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setString(2, purpose);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new EmailToken(rs.getLong("id"), rs.getLong("user_id"), rs.getString("purpose"),
                        rs.getTimestamp("expires_at").toLocalDateTime(), rs.getBoolean("used"));
            }
        }
    }

    /** 使用済みにする。まだ未使用だった場合だけ true（同時に2回開かれても一度しか成功しない）。 */
    public boolean consume(long tokenId) throws Exception {
        String sql = "UPDATE email_tokens SET used = TRUE WHERE id = ? AND used = FALSE";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, tokenId);
            return ps.executeUpdate() == 1;
        }
    }
}
