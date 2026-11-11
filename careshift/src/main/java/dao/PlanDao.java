package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import bean.GroupPlan;

/** group_plans と payments の操作（S21 プラン）。カード情報は扱わない。 */
public class PlanDao extends Dao {

    /** 決済の記録 */
    public static final class Payment {
        public final long id;
        public final long groupId;
        public final long userId;
        public final int amount;
        public final String status;

        Payment(long id, long groupId, long userId, int amount, String status) {
            this.id = id;
            this.groupId = groupId;
            this.userId = userId;
            this.amount = amount;
            this.status = status;
        }
    }

    /** グループのプラン。行がなければ無料。 */
    public GroupPlan find(long groupId) throws Exception {
        String sql = "SELECT plan, renews_on, cancel_requested FROM group_plans WHERE group_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return GroupPlan.free();
                }
                Date d = rs.getDate(2);
                return new GroupPlan(rs.getString(1), d == null ? null : d.toLocalDate(), rs.getBoolean(3));
            }
        }
    }

    /** 決済セッションを作る（まだ未決済）。 */
    public void createPayment(long groupId, long userId, String tokenHash, int amount) throws Exception {
        String sql = "INSERT INTO payments (group_id, user_id, token_hash, amount) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ps.setLong(2, userId);
            ps.setString(3, tokenHash);
            ps.setInt(4, amount);
            ps.executeUpdate();
        }
    }

    public Payment findPayment(String tokenHash) throws Exception {
        String sql = "SELECT id, group_id, user_id, amount, status FROM payments WHERE token_hash = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Payment(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getInt(4), rs.getString(5)) : null;
            }
        }
    }

    /**
     * 決済の結果を反映する（1つのトランザクション）。まだ未決済の決済だけが対象で、二重に反映されない。
     * 成功なら有料にする（次回更新日は今日から1か月後）。失敗・キャンセルならプランは変えない。
     * 反映したら true。
     */
    public boolean complete(String tokenHash, boolean success) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                long groupId;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT group_id FROM payments WHERE token_hash = ? AND status = 'PENDING' FOR UPDATE")) {
                    ps.setString(1, tokenHash);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        groupId = rs.getLong(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE payments SET status = ?, completed_at = ? WHERE token_hash = ? AND status = 'PENDING'")) {
                    ps.setString(1, success ? "SUCCESS" : "FAILED");
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(3, tokenHash);
                    ps.executeUpdate();
                }
                if (success) {
                    Date renews = Date.valueOf(LocalDate.now().plusMonths(1));
                    int updated;
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE group_plans SET plan = 'PAID', renews_on = ?, cancel_requested = FALSE,"
                            + " updated_at = CURRENT_TIMESTAMP WHERE group_id = ?")) {
                        ps.setDate(1, renews);
                        ps.setLong(2, groupId);
                        updated = ps.executeUpdate();
                    }
                    if (updated == 0) {
                        try (PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO group_plans (group_id, plan, renews_on, cancel_requested)"
                                + " VALUES (?, 'PAID', ?, FALSE)")) {
                            ps.setLong(1, groupId);
                            ps.setDate(2, renews);
                            ps.executeUpdate();
                        }
                    }
                }
                con.commit();
                return true;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** 「無料へ戻す」を予約する。次回更新日（利用期限）までは有料のまま使える。予約できたら true。 */
    public boolean requestCancel(long groupId) throws Exception {
        String sql = "UPDATE group_plans SET cancel_requested = TRUE, updated_at = CURRENT_TIMESTAMP"
                + " WHERE group_id = ? AND plan = 'PAID' AND cancel_requested = FALSE";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            return ps.executeUpdate() == 1;
        }
    }
}
