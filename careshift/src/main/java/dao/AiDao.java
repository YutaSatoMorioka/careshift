package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** AI001：利用回数（グループ・月ごと）と、保存した下書き（利用者ごと）。 */
public class AiDao extends Dao {

    /** 保存した下書き */
    public static final class Draft {
        public final String content;
        public final String requestedAt; // "yyyy-MM-ddTHH:mm"。なければ空
        public final Integer durationMin;

        public Draft(String content, String requestedAt, Integer durationMin) {
            this.content = content;
            this.requestedAt = requestedAt == null ? "" : requestedAt;
            this.durationMin = durationMin;
        }
    }

    public int used(long groupId, String ym) throws Exception {
        String sql = "SELECT used FROM ai_usage WHERE group_id = ? AND ym = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ps.setString(2, ym);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** 上限に達していなければ1回分を使う（先に確保する）。使えたら true。 */
    public boolean tryConsume(long groupId, String ym, int limit) throws Exception {
        try (Connection con = getConnection()) {
            if (increment(con, groupId, ym, limit) == 1) {
                return true;
            }
            if (limit <= 0) {
                return false;
            }
            // 行がまだない場合は作る（同時に作られたら、もう一度加算を試す）
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ai_usage (group_id, ym, used) VALUES (?, ?, 1)")) {
                ps.setLong(1, groupId);
                ps.setString(2, ym);
                return ps.executeUpdate() == 1;
            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    return increment(con, groupId, ym, limit) == 1;
                }
                throw e;
            }
        }
    }

    private int increment(Connection con, long groupId, String ym, int limit) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE ai_usage SET used = used + 1 WHERE group_id = ? AND ym = ? AND used < ?")) {
            ps.setLong(1, groupId);
            ps.setString(2, ym);
            ps.setInt(3, limit);
            return ps.executeUpdate();
        }
    }

    /** 生成に失敗したときに、確保した1回分を戻す。 */
    public void refund(long groupId, String ym) throws Exception {
        String sql = "UPDATE ai_usage SET used = used - 1 WHERE group_id = ? AND ym = ? AND used > 0";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ps.setString(2, ym);
            ps.executeUpdate();
        }
    }

    public Draft findDraft(long userId) throws Exception {
        String sql = "SELECT content, requested_at, duration_min FROM ai_drafts WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int d = rs.getInt(3);
                return new Draft(rs.getString(1), rs.getString(2), rs.wasNull() ? null : d);
            }
        }
    }

    /** 下書きを保存する（利用者ごとに最新の1件）。 */
    public void saveDraft(long userId, String content, String requestedAt, Integer durationMin) throws Exception {
        try (Connection con = getConnection()) {
            int updated;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE ai_drafts SET content = ?, requested_at = ?, duration_min = ?, updated_at = CURRENT_TIMESTAMP"
                    + " WHERE user_id = ?")) {
                ps.setString(1, content);
                ps.setString(2, requestedAt == null || requestedAt.isEmpty() ? null : requestedAt);
                if (durationMin == null) ps.setNull(3, java.sql.Types.INTEGER); else ps.setInt(3, durationMin);
                ps.setLong(4, userId);
                updated = ps.executeUpdate();
            }
            if (updated == 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ai_drafts (user_id, content, requested_at, duration_min) VALUES (?, ?, ?, ?)")) {
                    ps.setLong(1, userId);
                    ps.setString(2, content);
                    ps.setString(3, requestedAt == null || requestedAt.isEmpty() ? null : requestedAt);
                    if (durationMin == null) ps.setNull(4, java.sql.Types.INTEGER); else ps.setInt(4, durationMin);
                    ps.executeUpdate();
                }
            }
        }
    }
}
