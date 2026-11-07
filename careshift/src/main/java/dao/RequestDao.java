package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.RequestCard;
import bean.RequestRecipient;

/** requests / request_recipients の操作（依頼の作成・一覧・取り下げ・回答）。 */
public class RequestDao extends Dao {

    /** 回答の保存結果 */
    public enum RespondResult { SAVED, NOT_FOUND, NOT_RECIPIENT, WITHDRAWN, EXPIRED, TAKEN }

    private static final String SELECT =
            "SELECT r.id, r.group_id, r.type, r.content, r.requested_at, r.duration_min, r.task_id, r.rest_id,"
            + " r.sender_id, s.display_name AS sender_name, r.status, r.accepted_by, a.display_name AS accepted_name"
            + " FROM requests r"
            + " JOIN users s ON s.id = r.sender_id"
            + " LEFT JOIN users a ON a.id = r.accepted_by";

    // ---------------------------------------------------------------- 作成

    /** 依頼カードを作って宛先に送る。作った依頼の id を返す。 */
    public long create(long groupId, String type, String content, LocalDateTime requestedAt, Integer durationMin,
            Long taskId, Long restId, long senderId, List<Long> recipients) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                long id = insertRequest(con, groupId, type, content, requestedAt, durationMin, taskId, restId,
                        senderId, recipients);
                con.commit();
                return id;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** 休息予定（調整中）を作り、その協力依頼を宛先に送る。作った依頼カードの id を返す。 */
    public long createRest(long groupId, LocalDate date, LocalTime start, LocalTime end, String content,
            long senderId, List<Long> recipients) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                long restId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO rest_plans (group_id, rest_date, start_time, end_time, content, created_by)"
                        + " VALUES (?, ?, ?, ?, ?, ?)", new String[] { "id" })) {
                    ps.setLong(1, groupId);
                    ps.setDate(2, Date.valueOf(date));
                    ps.setTime(3, Time.valueOf(start));
                    ps.setTime(4, Time.valueOf(end));
                    ps.setString(5, content);
                    ps.setLong(6, senderId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        restId = keys.getLong(1);
                    }
                }
                int minutes = (int) java.time.Duration.between(start, end).toMinutes();
                long requestId = insertRequest(con, groupId, "REST", content, LocalDateTime.of(date, start), minutes,
                        null, restId, senderId, recipients);
                con.commit();
                return requestId;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    private long insertRequest(Connection con, long groupId, String type, String content, LocalDateTime requestedAt,
            Integer durationMin, Long taskId, Long restId, long senderId, List<Long> recipients) throws Exception {
        long id;
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO requests (group_id, type, content, requested_at, duration_min, task_id, rest_id, sender_id)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new String[] { "id" })) {
            ps.setLong(1, groupId);
            ps.setString(2, type);
            ps.setString(3, content);
            ps.setTimestamp(4, Timestamp.valueOf(requestedAt));
            setNullableInt(ps, 5, durationMin);
            setNullableLong(ps, 6, taskId);
            setNullableLong(ps, 7, restId);
            ps.setLong(8, senderId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                id = keys.getLong(1);
            }
        }
        for (long recipient : recipients) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO request_recipients (request_id, user_id) VALUES (?, ?)")) {
                ps.setLong(1, id);
                ps.setLong(2, recipient);
                ps.executeUpdate();
            }
        }
        return id;
    }

    // ---------------------------------------------------------------- 取得

    public List<RequestCard> findSent(long userId, long groupId) throws Exception {
        return queryWithRecipients(SELECT + " WHERE r.group_id = ? AND r.sender_id = ? ORDER BY r.id DESC",
                groupId, userId);
    }

    public List<RequestCard> findReceived(long userId, long groupId) throws Exception {
        return queryWithRecipients(SELECT + " WHERE r.group_id = ? AND r.id IN"
                + " (SELECT request_id FROM request_recipients WHERE user_id = ?) ORDER BY r.id DESC", groupId, userId);
    }

    public RequestCard find(long id, long groupId) throws Exception {
        List<RequestCard> list = queryWithRecipients(SELECT + " WHERE r.id = ? AND r.group_id = ?", id, groupId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 休息予定に紐づく依頼（新しい順）。再依頼の可否や「全員が難しい」の判定に使う。 */
    public List<RequestCard> findByRest(long restId, long groupId) throws Exception {
        return queryWithRecipients(SELECT + " WHERE r.rest_id = ? AND r.group_id = ? ORDER BY r.id DESC",
                restId, groupId);
    }

    // ---------------------------------------------------------------- 取り下げ

    /** 送信者が取り下げる。回答待ちで期限前のときだけ成功（true）。 */
    public boolean withdraw(long id, long senderId) throws Exception {
        String sql = "UPDATE requests SET status = 'WITHDRAWN', updated_at = CURRENT_TIMESTAMP"
                + " WHERE id = ? AND sender_id = ? AND status = 'OPEN' AND requested_at > ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, senderId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() == 1;
        }
    }

    /** 休息予定の再依頼のとき、同じ予定の古い受付中の依頼を取り下げ済みにする（並行して2つ受け付けない）。 */
    public void withdrawOpenForRest(long restId, long exceptRequestId) throws Exception {
        String sql = "UPDATE requests SET status = 'WITHDRAWN', updated_at = CURRENT_TIMESTAMP"
                + " WHERE rest_id = ? AND status = 'OPEN' AND id <> ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, restId);
            ps.setLong(2, exceptRequestId);
            ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------------- 回答

    /**
     * 宛先が回答する（1つのトランザクション）。
     * 取り下げ済み・期限切れ・他の人が引受済みのときは保存しない。
     * 「担当する」なら、休息予定は確定、タスク依頼ならそのタスクの担当者に設定する。
     * 引き受けた本人が別の回答に変更したときは、引受を解除して元に戻す。
     */
    public RespondResult respond(long requestId, long groupId, long userId, String response, String alternative)
            throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                RespondResult result = doRespond(con, requestId, groupId, userId, response, alternative);
                if (result == RespondResult.SAVED) {
                    con.commit();
                } else {
                    con.rollback();
                }
                return result;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    private RespondResult doRespond(Connection con, long requestId, long groupId, long userId, String response,
            String alternative) throws Exception {
        String status;
        LocalDateTime requestedAt;
        Long acceptedBy;
        Long taskId;
        Long restId;
        // 依頼の行をロックして、同時に2人が引き受けられないようにする
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT status, requested_at, accepted_by, task_id, rest_id FROM requests"
                + " WHERE id = ? AND group_id = ? FOR UPDATE")) {
            ps.setLong(1, requestId);
            ps.setLong(2, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return RespondResult.NOT_FOUND;
                }
                status = rs.getString(1);
                requestedAt = rs.getTimestamp(2).toLocalDateTime();
                long a = rs.getLong(3);
                acceptedBy = rs.wasNull() ? null : a;
                long t = rs.getLong(4);
                taskId = rs.wasNull() ? null : t;
                long r = rs.getLong(5);
                restId = rs.wasNull() ? null : r;
            }
        }
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM request_recipients WHERE request_id = ? AND user_id = ?")) {
            ps.setLong(1, requestId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    return RespondResult.NOT_RECIPIENT;
                }
            }
        }
        if ("WITHDRAWN".equals(status)) {
            return RespondResult.WITHDRAWN;
        }
        if (requestedAt.isBefore(LocalDateTime.now())) {
            return RespondResult.EXPIRED;
        }
        boolean mine = acceptedBy != null && acceptedBy == userId;
        if ("ACCEPTED".equals(status) && !mine) {
            return RespondResult.TAKEN;
        }

        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE request_recipients SET response = ?, alternative = ?, responded_at = ?"
                + " WHERE request_id = ? AND user_id = ?")) {
            ps.setString(1, response);
            ps.setString(2, "ALT".equals(response) && alternative != null && !alternative.isEmpty() ? alternative : null);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(4, requestId);
            ps.setLong(5, userId);
            ps.executeUpdate();
        }

        if ("ACCEPT".equals(response)) {
            if ("OPEN".equals(status)) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE requests SET status = 'ACCEPTED', accepted_by = ?, updated_at = CURRENT_TIMESTAMP"
                        + " WHERE id = ? AND status = 'OPEN'")) {
                    ps.setLong(1, userId);
                    ps.setLong(2, requestId);
                    if (ps.executeUpdate() != 1) {
                        return RespondResult.TAKEN;
                    }
                }
                if (restId != null) {
                    exec(con, "UPDATE rest_plans SET status = 'CONFIRMED', covered_by = ?, updated_at = CURRENT_TIMESTAMP"
                            + " WHERE id = ? AND status <> 'CANCELLED'", userId, restId);
                }
                if (taskId != null) {
                    exec(con, "UPDATE tasks SET assignee_id = ?, lock_version = lock_version + 1,"
                            + " updated_at = CURRENT_TIMESTAMP WHERE id = ? AND group_id = ?", userId, taskId, groupId);
                }
            }
        } else if ("ACCEPTED".equals(status) && mine) {
            // 引き受けた本人が回答を変えた：引受を解除して回答待ちに戻す
            exec(con, "UPDATE requests SET status = 'OPEN', accepted_by = NULL, updated_at = CURRENT_TIMESTAMP"
                    + " WHERE id = ?", requestId);
            if (restId != null) {
                exec(con, "UPDATE rest_plans SET status = 'ADJUSTING', covered_by = NULL, updated_at = CURRENT_TIMESTAMP"
                        + " WHERE id = ? AND status = 'CONFIRMED' AND covered_by = ?", restId, userId);
            }
            if (taskId != null) {
                exec(con, "UPDATE tasks SET assignee_id = NULL, lock_version = lock_version + 1,"
                        + " updated_at = CURRENT_TIMESTAMP WHERE id = ? AND assignee_id = ?", taskId, userId);
            }
        }
        return RespondResult.SAVED;
    }

    // ---------------------------------------------------------------- 内部

    private void exec(Connection con, String sql, Object... params) throws Exception {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    private List<RequestCard> queryWithRecipients(String sql, Object... params) throws Exception {
        List<RequestCard> cards = new ArrayList<>();
        try (Connection con = getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        long task = rs.getLong("task_id");
                        Long taskId = rs.wasNull() ? null : task;
                        long rest = rs.getLong("rest_id");
                        Long restId = rs.wasNull() ? null : rest;
                        int dur = rs.getInt("duration_min");
                        Integer duration = rs.wasNull() ? null : dur;
                        long acc = rs.getLong("accepted_by");
                        Long acceptedBy = rs.wasNull() ? null : acc;
                        cards.add(new RequestCard(rs.getLong("id"), rs.getLong("group_id"), rs.getString("type"),
                                rs.getString("content"), rs.getTimestamp("requested_at").toLocalDateTime(), duration,
                                taskId, restId, rs.getLong("sender_id"), rs.getString("sender_name"),
                                rs.getString("status"), acceptedBy, rs.getString("accepted_name")));
                    }
                }
            }
            if (cards.isEmpty()) {
                return cards;
            }
            Map<Long, RequestCard> byId = new HashMap<>();
            StringBuilder marks = new StringBuilder();
            for (RequestCard c : cards) {
                byId.put(c.getId(), c);
                marks.append(marks.length() == 0 ? "?" : ",?");
            }
            String rsql = "SELECT q.request_id, q.user_id, u.display_name, q.response, q.alternative, q.responded_at"
                    + " FROM request_recipients q JOIN users u ON u.id = q.user_id"
                    + " WHERE q.request_id IN (" + marks + ") ORDER BY q.request_id, u.id";
            try (PreparedStatement ps = con.prepareStatement(rsql)) {
                int i = 1;
                for (RequestCard c : cards) {
                    ps.setLong(i++, c.getId());
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Timestamp at = rs.getTimestamp("responded_at");
                        byId.get(rs.getLong("request_id")).getRecipients().add(new RequestRecipient(
                                rs.getLong("user_id"), rs.getString("display_name"), rs.getString("response"),
                                rs.getString("alternative"), at == null ? null : at.toLocalDateTime()));
                    }
                }
            }
        }
        return cards;
    }

    private static void setNullableLong(PreparedStatement ps, int index, Long value) throws Exception {
        if (value == null) ps.setNull(index, Types.BIGINT); else ps.setLong(index, value);
    }

    private static void setNullableInt(PreparedStatement ps, int index, Integer value) throws Exception {
        if (value == null) ps.setNull(index, Types.INTEGER); else ps.setInt(index, value);
    }
}
