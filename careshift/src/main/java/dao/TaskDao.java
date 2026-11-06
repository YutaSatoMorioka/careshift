package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bean.Task;

/** tasks テーブルの操作。すべて group_id で絞る（他グループのタスクには触れない）。 */
public class TaskDao extends Dao {

    private static final String SELECT =
            "SELECT t.id, t.group_id, t.title, t.due_date, t.duration_min, t.frequency, t.assignee_id,"
            + " a.display_name AS assignee_name, t.status, t.created_by, c.display_name AS creator_name,"
            + " t.lock_version"
            + " FROM tasks t"
            + " LEFT JOIN users a ON a.id = t.assignee_id"
            + " JOIN users c ON c.id = t.created_by";

    /**
     * 絞り込み検索（S07）。条件はすべて任意。
     * assignee は "none"（担当なし）かユーザーID、dueTo は「この日まで」、status は TODO/DOING/DONE。
     */
    public List<Task> search(long groupId, String keyword, String assignee, LocalDate dueTo, String status)
            throws Exception {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE t.group_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(groupId);

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND LOWER(t.title) LIKE ? ESCAPE '!'");
            params.add("%" + escapeLike(keyword.toLowerCase()) + "%");
        }
        if ("none".equals(assignee)) {
            sql.append(" AND t.assignee_id IS NULL");
        } else if (assignee != null && assignee.matches("\\d{1,18}")) {
            sql.append(" AND t.assignee_id = ?");
            params.add(Long.parseLong(assignee));
        }
        if (dueTo != null) {
            sql.append(" AND t.due_date <= ?");
            params.add(Date.valueOf(dueTo));
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND t.status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY t.due_date, t.id");
        return query(sql.toString(), params);
    }

    /** カレンダー用：期限が from〜to（両端を含む）のタスク。 */
    public List<Task> findByDueRange(long groupId, LocalDate from, LocalDate to) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(groupId);
        params.add(Date.valueOf(from));
        params.add(Date.valueOf(to));
        return query(SELECT + " WHERE t.group_id = ? AND t.due_date >= ? AND t.due_date <= ?"
                + " ORDER BY t.due_date, t.id", params);
    }

    /** 期限が to 以前で、まだ完了していない、特定の担当者のタスク（期限リマインド用）。 */
    public List<Task> findOpenAssignedDueBy(long groupId, long assigneeId, LocalDate to) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(groupId);
        params.add(assigneeId);
        params.add(Date.valueOf(to));
        return query(SELECT + " WHERE t.group_id = ? AND t.assignee_id = ? AND t.due_date <= ? AND t.status <> 'DONE'"
                + " ORDER BY t.due_date, t.id", params);
    }

    /** グループ内のタスクを1件取得。なければ（削除済み・他グループ含む）null。 */
    public Task find(long id, long groupId) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(groupId);
        List<Task> list = query(SELECT + " WHERE t.id = ? AND t.group_id = ?", params);
        return list.isEmpty() ? null : list.get(0);
    }

    public long insert(long groupId, String title, LocalDate dueDate, int durationMin, String frequency,
            Long assigneeId, long createdBy) throws Exception {
        String sql = "INSERT INTO tasks (group_id, title, due_date, duration_min, frequency, assignee_id, created_by)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" })) {
            ps.setLong(1, groupId);
            ps.setString(2, title);
            ps.setDate(3, Date.valueOf(dueDate));
            ps.setInt(4, durationMin);
            ps.setString(5, frequency);
            setNullableLong(ps, 6, assigneeId);
            ps.setLong(7, createdBy);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    /**
     * 編集画面からの更新。expectedVersion が今のDBの版と違えば（先に誰かが更新していれば）
     * 何も更新せず false を返す。
     */
    public boolean update(long id, long groupId, String title, LocalDate dueDate, int durationMin, String frequency,
            Long assigneeId, String status, int expectedVersion) throws Exception {
        String sql = "UPDATE tasks SET title = ?, due_date = ?, duration_min = ?, frequency = ?, assignee_id = ?,"
                + " status = ?, lock_version = lock_version + 1, updated_at = CURRENT_TIMESTAMP"
                + " WHERE id = ? AND group_id = ? AND lock_version = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setDate(2, Date.valueOf(dueDate));
            ps.setInt(3, durationMin);
            ps.setString(4, frequency);
            setNullableLong(ps, 5, assigneeId);
            ps.setString(6, status);
            ps.setLong(7, id);
            ps.setLong(8, groupId);
            ps.setInt(9, expectedVersion);
            return ps.executeUpdate() == 1;
        }
    }

    /** 詳細画面からの担当者・進捗の更新。競合時は false。 */
    public boolean updateAssigneeStatus(long id, long groupId, Long assigneeId, String status, int expectedVersion)
            throws Exception {
        String sql = "UPDATE tasks SET assignee_id = ?, status = ?, lock_version = lock_version + 1,"
                + " updated_at = CURRENT_TIMESTAMP WHERE id = ? AND group_id = ? AND lock_version = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableLong(ps, 1, assigneeId);
            ps.setString(2, status);
            ps.setLong(3, id);
            ps.setLong(4, groupId);
            ps.setInt(5, expectedVersion);
            return ps.executeUpdate() == 1;
        }
    }

    /** 削除。実際に削除できたら true（すでに削除済みなら false）。 */
    public boolean delete(long id, long groupId) throws Exception {
        String sql = "DELETE FROM tasks WHERE id = ? AND group_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, groupId);
            return ps.executeUpdate() == 1;
        }
    }

    private List<Task> query(String sql, List<Object> params) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Task> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        }
    }

    private Task map(ResultSet rs) throws Exception {
        long assignee = rs.getLong("assignee_id");
        Long assigneeId = rs.wasNull() ? null : assignee;
        return new Task(rs.getLong("id"), rs.getLong("group_id"), rs.getString("title"),
                rs.getDate("due_date").toLocalDate(), rs.getInt("duration_min"), rs.getString("frequency"),
                assigneeId, rs.getString("assignee_name"), rs.getString("status"), rs.getLong("created_by"),
                rs.getString("creator_name"), rs.getInt("lock_version"));
    }

    private static void setNullableLong(PreparedStatement ps, int index, Long value) throws Exception {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }

    /** LIKE 検索で % _ ! をそのままの文字として扱うためのエスケープ。 */
    private static String escapeLike(String s) {
        return s.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
