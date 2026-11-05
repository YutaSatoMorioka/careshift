package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import bean.Invitation;
import bean.Member;

/** invitations テーブルの操作と、招待を使ったグループ参加。 */
public class InvitationDao extends Dao {

    /** 参加の結果 */
    public enum JoinResult { JOINED, USED, EXPIRED, FULL, ALREADY_IN_GROUP, NOT_FOUND }

    public void issue(long groupId, String tokenHash, String relationship, String role, long invitedBy,
            LocalDateTime expiresAt) throws Exception {
        String sql = "INSERT INTO invitations (group_id, token_hash, relationship, role, invited_by, expires_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ps.setString(2, tokenHash);
            ps.setString(3, relationship == null || relationship.isEmpty() ? null : relationship);
            ps.setString(4, role);
            ps.setLong(5, invitedBy);
            ps.setTimestamp(6, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();
        }
    }

    /** トークンのハッシュから招待を探す（期限切れ・使用済みも返す。判定は呼び出し側）。いなければ null。 */
    public Invitation find(String tokenHash) throws Exception {
        String sql = "SELECT i.id, i.group_id, g.name, i.relationship, i.role, i.invited_by, u.display_name,"
                + " i.expires_at, i.used_by"
                + " FROM invitations i"
                + " JOIN family_groups g ON g.id = i.group_id"
                + " JOIN users u ON u.id = i.invited_by"
                + " WHERE i.token_hash = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                rs.getLong("used_by");
                boolean used = !rs.wasNull();
                return new Invitation(rs.getLong("id"), rs.getLong("group_id"), rs.getString("name"),
                        rs.getString("relationship"), rs.getString("role"), rs.getLong("invited_by"),
                        rs.getString("display_name"), rs.getTimestamp("expires_at").toLocalDateTime(), used);
            }
        }
    }

    /**
     * 招待を使ってグループに参加する（1つのトランザクション）。
     * 有効期限・使用済み・人数上限（6名）・別グループ所属を検証し、成功したら
     * メンバー登録、招待の使用済み化、招待者と主介護者への通知を行う。
     */
    public JoinResult join(long invitationId, long userId, String joinerName) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                JoinResult result = doJoin(con, invitationId, userId, joinerName);
                if (result == JoinResult.JOINED) {
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

    private JoinResult doJoin(Connection con, long invitationId, long userId, String joinerName) throws Exception {
        long groupId;
        String relationship;
        String role;
        long invitedBy;
        LocalDateTime expiresAt;
        boolean used;
        String groupName;

        try (PreparedStatement ps = con.prepareStatement(
                "SELECT i.group_id, i.relationship, i.role, i.invited_by, i.expires_at, i.used_by, g.name"
                + " FROM invitations i JOIN family_groups g ON g.id = i.group_id WHERE i.id = ?")) {
            ps.setLong(1, invitationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return JoinResult.NOT_FOUND;
                }
                groupId = rs.getLong(1);
                relationship = rs.getString(2);
                role = rs.getString(3);
                invitedBy = rs.getLong(4);
                expiresAt = rs.getTimestamp(5).toLocalDateTime();
                rs.getLong(6);
                used = !rs.wasNull();
                groupName = rs.getString(7);
            }
        }
        if (used) {
            return JoinResult.USED;
        }
        if (LocalDateTime.now().isAfter(expiresAt)) {
            return JoinResult.EXPIRED;
        }

        // グループの行をロックして、同時参加で6名を超えないようにする
        try (PreparedStatement ps = con.prepareStatement("SELECT id FROM family_groups WHERE id = ? FOR UPDATE")) {
            ps.setLong(1, groupId);
            ps.executeQuery().close();
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM group_members WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return JoinResult.ALREADY_IN_GROUP;
                }
            }
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM group_members WHERE group_id = ?")) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) >= GroupDao.MAX_MEMBERS) {
                    return JoinResult.FULL;
                }
            }
        }

        // 招待を使用済みにする（先に使用済みにできた人だけが参加できる）
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE invitations SET used_by = ?, used_at = CURRENT_TIMESTAMP WHERE id = ? AND used_by IS NULL")) {
            ps.setLong(1, userId);
            ps.setLong(2, invitationId);
            if (ps.executeUpdate() != 1) {
                return JoinResult.USED;
            }
        }

        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO group_members (group_id, user_id, role, relationship) VALUES (?, ?, ?, ?)")) {
            ps.setLong(1, groupId);
            ps.setLong(2, userId);
            ps.setString(3, role);
            ps.setString(4, relationship);
            ps.executeUpdate();
        }

        // 通知：招待者と主介護者（同じ人なら1通。本人には送らない）
        Set<Long> recipients = new LinkedHashSet<>();
        recipients.add(invitedBy);
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT user_id FROM group_members WHERE group_id = ? AND role = ?")) {
            ps.setLong(1, groupId);
            ps.setString(2, Member.ROLE_PRIMARY);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recipients.add(rs.getLong(1));
                }
            }
        }
        recipients.remove(userId);
        String message = joinerName + " さんが「" + groupName + "」に参加しました";
        for (long recipient : recipients) {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO notifications (user_id, type, message, link_path) VALUES (?, 'GROUP', ?, ?)")) {
                ps.setLong(1, recipient);
                ps.setString(2, message);
                ps.setString(3, "/careshift/main/MemberList.action");
                ps.executeUpdate();
            }
        }
        return JoinResult.JOINED;
    }
}
