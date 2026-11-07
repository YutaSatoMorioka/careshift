package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import bean.RestPlan;

/** rest_plans テーブルの操作。すべて group_id で絞る。 */
public class RestDao extends Dao {

    private static final String SELECT =
            "SELECT p.id, p.group_id, p.rest_date, p.start_time, p.end_time, p.content, p.status, p.created_by,"
            + " c.display_name AS creator_name, p.covered_by, v.display_name AS covered_name"
            + " FROM rest_plans p"
            + " JOIN users c ON c.id = p.created_by"
            + " LEFT JOIN users v ON v.id = p.covered_by";

    public RestPlan find(long id, long groupId) throws Exception {
        List<RestPlan> list = query(SELECT + " WHERE p.id = ? AND p.group_id = ?", id, groupId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 日付が from〜to（両端を含む）の休息予定（取消も含む）。カレンダー・集計用。 */
    public List<RestPlan> findByDateRange(long groupId, LocalDate from, LocalDate to) throws Exception {
        return query(SELECT + " WHERE p.group_id = ? AND p.rest_date >= ? AND p.rest_date <= ?"
                + " ORDER BY p.rest_date, p.start_time, p.id", groupId, Date.valueOf(from), Date.valueOf(to));
    }

    /** 休息リクエスト画面の一覧用：新しい日付順（取消も含む）。 */
    public List<RestPlan> findRecent(long groupId, int limit) throws Exception {
        List<RestPlan> all = query(SELECT + " WHERE p.group_id = ? ORDER BY p.rest_date DESC, p.start_time DESC, p.id DESC",
                groupId);
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    /** 同じ日に、時間が重なる（取消でない）休息予定があるか。 */
    public boolean hasOverlap(long groupId, LocalDate date, LocalTime start, LocalTime end) throws Exception {
        String sql = "SELECT COUNT(*) FROM rest_plans WHERE group_id = ? AND rest_date = ?"
                + " AND status <> 'CANCELLED' AND start_time < ? AND end_time > ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(end));
            ps.setTime(4, Time.valueOf(start));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * 休息予定を取消にする。まだ取消でなければ true。
     * 同時に、その予定への依頼（回答待ち・引受済み）は取り下げ済みにする。
     */
    public boolean cancel(long id, long groupId) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                int updated;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE rest_plans SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP"
                        + " WHERE id = ? AND group_id = ? AND status <> 'CANCELLED'")) {
                    ps.setLong(1, id);
                    ps.setLong(2, groupId);
                    updated = ps.executeUpdate();
                }
                if (updated == 1) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE requests SET status = 'WITHDRAWN', updated_at = CURRENT_TIMESTAMP"
                            + " WHERE rest_id = ? AND status IN ('OPEN', 'ACCEPTED')")) {
                        ps.setLong(1, id);
                        ps.executeUpdate();
                    }
                }
                con.commit();
                return updated == 1;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    private List<RestPlan> query(String sql, Object... params) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<RestPlan> list = new ArrayList<>();
                while (rs.next()) {
                    long covered = rs.getLong("covered_by");
                    Long coveredBy = rs.wasNull() ? null : covered;
                    list.add(new RestPlan(rs.getLong("id"), rs.getLong("group_id"),
                            rs.getDate("rest_date").toLocalDate(), rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime(), rs.getString("content"), rs.getString("status"),
                            rs.getLong("created_by"), rs.getString("creator_name"), coveredBy,
                            rs.getString("covered_name")));
                }
                return list;
            }
        }
    }
}
