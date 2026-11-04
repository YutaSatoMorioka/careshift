package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.FamilyGroup;
import bean.Member;

/** family_groups と group_members の操作。 */
public class GroupDao extends Dao {

    /** グループの人数上限（設計書：S05・S16） */
    public static final int MAX_MEMBERS = 6;

    private static final String MEMBER_SELECT =
            "SELECT m.user_id, u.display_name, m.group_id, m.role, m.relationship, m.contact"
            + " FROM group_members m JOIN users u ON u.id = m.user_id";

    /** そのユーザーの所属情報。どのグループにも所属していなければ null。 */
    public Member findMember(long userId) throws Exception {
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(MEMBER_SELECT + " WHERE m.user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapMember(rs) : null;
            }
        }
    }

    /** グループのメンバー一覧（主介護者が先頭、あとは参加順）。 */
    public List<Member> findMembers(long groupId) throws Exception {
        String sql = MEMBER_SELECT + " WHERE m.group_id = ?"
                + " ORDER BY CASE WHEN m.role = 'PRIMARY' THEN 0 ELSE 1 END, m.joined_at, m.id";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Member> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapMember(rs));
                }
                return list;
            }
        }
    }

    public FamilyGroup findGroup(long groupId) throws Exception {
        String sql = "SELECT id, name, created_by FROM family_groups WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new FamilyGroup(rs.getLong(1), rs.getString(2), rs.getLong(3)) : null;
            }
        }
    }

    public int countMembers(long groupId) throws Exception {
        String sql = "SELECT COUNT(*) FROM group_members WHERE group_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** グループを作り、作成者を初期メンバー（主介護者）として登録する。グループの id を返す。 */
    public long createGroup(long userId, String name) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                long groupId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO family_groups (name, created_by) VALUES (?, ?)", new String[] { "id" })) {
                    ps.setString(1, name);
                    ps.setLong(2, userId);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        groupId = keys.getLong(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO group_members (group_id, user_id, role) VALUES (?, ?, ?)")) {
                    ps.setLong(1, groupId);
                    ps.setLong(2, userId);
                    ps.setString(3, Member.ROLE_PRIMARY);
                    ps.executeUpdate();
                }
                con.commit();
                return groupId;
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void rename(long groupId, String name) throws Exception {
        String sql = "UPDATE family_groups SET name = ? WHERE id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setLong(2, groupId);
            ps.executeUpdate();
        }
    }

    /** 続柄・連絡先の更新。空文字は未設定（NULL）として保存する。 */
    public void updateProfile(long userId, String relationship, String contact) throws Exception {
        String sql = "UPDATE group_members SET relationship = ?, contact = ? WHERE user_id = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, relationship == null || relationship.isEmpty() ? null : relationship);
            ps.setString(2, contact == null || contact.isEmpty() ? null : contact);
            ps.setLong(3, userId);
            ps.executeUpdate();
        }
    }

    /** 主介護者を付け替える（グループ内の主介護者は常に1名）。 */
    public void setPrimary(long groupId, long userId) throws Exception {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE group_members SET role = ? WHERE group_id = ?")) {
                    ps.setString(1, Member.ROLE_FAMILY);
                    ps.setLong(2, groupId);
                    ps.executeUpdate();
                }
                int updated;
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE group_members SET role = ? WHERE group_id = ? AND user_id = ?")) {
                    ps.setString(1, Member.ROLE_PRIMARY);
                    ps.setLong(2, groupId);
                    ps.setLong(3, userId);
                    updated = ps.executeUpdate();
                }
                if (updated != 1) {
                    throw new IllegalStateException("主介護者に設定するメンバーが見つかりません");
                }
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Member mapMember(ResultSet rs) throws Exception {
        return new Member(rs.getLong("user_id"), rs.getString("display_name"), rs.getLong("group_id"),
                rs.getString("role"), rs.getString("relationship"), rs.getString("contact"));
    }
}
