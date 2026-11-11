package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.Notification;

/** notifications テーブルの操作（書き込みと、通知一覧・既読化）。 */
public class NotificationDao extends Dao {

    /** 通知種別のうち、設計書の4種別以外（家族・タスク）をまとめて指す絞り込みの値 */
    public static final String OTHER = "OTHER";

    /** 通知を1件追加する。type は REQUEST / ANSWER / REST / DEADLINE / TASK / GROUP。 */
    public void add(long userId, String type, String message, String linkPath) throws Exception {
        insert(userId, type, message, linkPath, null);
    }

    /**
     * 同じ refKey の通知が（その人に）すでにあれば何もしない。作ったら true。
     * 期限リマインドを、画面を開くたびに何度も作らないために使う。
     */
    public boolean addOnce(long userId, String type, String message, String linkPath, String refKey) throws Exception {
        try {
            insert(userId, type, message, linkPath, refKey);
            return true;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // 一意制約違反＝作成済み
                return false;
            }
            throw e;
        }
    }

    private void insert(long userId, String type, String message, String linkPath, String refKey) throws Exception {
        String sql = "INSERT INTO notifications (user_id, type, message, link_path, ref_key) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, type);
            ps.setString(3, message.length() > 200 ? message.substring(0, 200) : message);
            ps.setString(4, linkPath);
            ps.setString(5, refKey);
            ps.executeUpdate();
        }
        tool.EmailNotifier.notify(userId, message); // メール通知がオンの人には、メールでも知らせる
    }

    /** 新しい順。type が空なら全種別、OTHER なら家族・タスクの通知、それ以外はその種別だけ。 */
    public List<Notification> find(long userId, String type, int limit) throws Exception {
        StringBuilder sql = new StringBuilder(
                "SELECT id, type, message, link_path, is_read, created_at FROM notifications WHERE user_id = ?");
        if (OTHER.equals(type)) {
            sql.append(" AND type NOT IN ('REQUEST', 'ANSWER', 'REST', 'DEADLINE')");
        } else if (type != null && !type.isEmpty()) {
            sql.append(" AND type = ?");
        }
        sql.append(" ORDER BY created_at DESC, id DESC");
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setLong(1, userId);
            if (type != null && !type.isEmpty() && !OTHER.equals(type)) {
                ps.setString(2, type);
            }
            ps.setMaxRows(limit);
            try (ResultSet rs = ps.executeQuery()) {
                return readAll(rs);
            }
        }
    }

    /** 未読だけを新しい順に。 */
    public List<Notification> findUnread(long userId, int limit) throws Exception {
        String sql = "SELECT id, type, message, link_path, is_read, created_at FROM notifications"
                + " WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC, id DESC";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setMaxRows(limit);
            try (ResultSet rs = ps.executeQuery()) {
                return readAll(rs);
            }
        }
    }

    /** 自分の通知を1件。他の人の通知は取得できない（null）。 */
    public Notification findOwn(long id, long userId) throws Exception {
        String sql = "SELECT id, type, message, link_path, is_read, created_at FROM notifications"
                + " WHERE id = ? AND user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Notification> list = readAll(rs);
                return list.isEmpty() ? null : list.get(0);
            }
        }
    }

    public int countUnread(long userId) throws Exception {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** 自分の通知を既読にする。 */
    public boolean markRead(long id, long userId) throws Exception {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND user_id = ? AND is_read = FALSE";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /** 自分の未読をすべて既読にする。既読にした件数を返す。 */
    public int markAllRead(long userId) throws Exception {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate();
        }
    }

    private List<Notification> readAll(ResultSet rs) throws Exception {
        List<Notification> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Notification(rs.getLong("id"), rs.getString("type"), rs.getString("message"),
                    rs.getString("link_path"), rs.getBoolean("is_read"), rs.getTimestamp("created_at").toLocalDateTime()));
        }
        return list;
    }
}
