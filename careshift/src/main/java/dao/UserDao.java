package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bean.User;

/** users テーブルの操作。 */
public class UserDao extends Dao {

    private static final String COLUMNS = "id, display_name, email, password_hash, email_verified";

    /** メールアドレス（小文字）で検索。いなければ null。 */
    public User findByEmail(String email) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(long id) throws Exception {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** 新規登録。メール未確認の状態で作り、採番された id を返す。 */
    public long insert(String displayName, String email, String passwordHash) throws Exception {
        String sql = "INSERT INTO users (display_name, email, password_hash, email_verified)"
                + " VALUES (?, ?, ?, FALSE)";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" })) {
            ps.setString(1, displayName);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public void markVerified(long userId) throws Exception {
        String sql = "UPDATE users SET email_verified = TRUE WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public void updatePassword(long userId, String passwordHash) throws Exception {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws Exception {
        return new User(rs.getLong("id"), rs.getString("display_name"), rs.getString("email"),
                rs.getString("password_hash"), rs.getBoolean("email_verified"));
    }
}
