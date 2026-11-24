package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import bean.FamilyGroup;
import bean.Member;

/**
 * family_groupsとgroup_membersの操作。
 *
 * グループ内のメンバー取得、グループ作成、名称変更、
 * メンバー情報更新、主介護者変更、メンバー削除を扱う。
 */
public class GroupDao extends Dao {

    /**
     * グループの人数上限。
     */
    public static final int MAX_MEMBERS = 6;

    /**
     * メンバー削除結果。
     */
    public enum RemoveMemberResult {

        /**
         * 削除成功。
         */
        REMOVED,

        /**
         * 対象が見つからない、または別グループ。
         */
        NOT_FOUND,

        /**
         * 自分自身のため削除不可。
         */
        SELF,

        /**
         * 主介護者のため削除不可。
         */
        PRIMARY
    }

    private static final String MEMBER_SELECT =
            "SELECT"
            + " m.user_id,"
            + " u.display_name,"
            + " m.group_id,"
            + " m.role,"
            + " m.relationship,"
            + " m.contact"
            + " FROM group_members m"
            + " JOIN users u ON u.id = m.user_id";

    /**
     * ユーザーの所属情報を取得する。
     *
     * どのグループにも所属していない場合はnull。
     */
    public Member findMember(
            long userId
    ) throws Exception {

        String sql =
                MEMBER_SELECT
                + " WHERE m.user_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next()
                        ? mapMember(rs)
                        : null;
            }
        }
    }

    /**
     * 同じグループに所属する特定のメンバーを取得する。
     */
    public Member findMember(
            long groupId,
            long userId
    ) throws Exception {

        String sql =
                MEMBER_SELECT
                + " WHERE m.group_id = ?"
                + " AND m.user_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            ps.setLong(
                    2,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next()
                        ? mapMember(rs)
                        : null;
            }
        }
    }

    /**
     * グループのメンバー一覧を取得する。
     *
     * 主介護者を先頭にし、その後は参加順で表示する。
     */
    public List<Member> findMembers(
            long groupId
    ) throws Exception {

        String sql =
                MEMBER_SELECT
                + " WHERE m.group_id = ?"
                + " ORDER BY"
                + " CASE"
                + " WHEN m.role = 'PRIMARY' THEN 0"
                + " ELSE 1"
                + " END,"
                + " m.joined_at,"
                + " m.id";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                List<Member> members =
                        new ArrayList<>();

                while (rs.next()) {
                    members.add(
                            mapMember(rs)
                    );
                }

                return members;
            }
        }
    }

    /**
     * 家族グループを取得する。
     */
    public FamilyGroup findGroup(
            long groupId
    ) throws Exception {

        String sql =
                "SELECT"
                + " id,"
                + " name,"
                + " created_by"
                + " FROM family_groups"
                + " WHERE id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                if (!rs.next()) {
                    return null;
                }

                return new FamilyGroup(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("created_by")
                );
            }
        }
    }

    /**
     * グループの現在のメンバー数を取得する。
     */
    public int countMembers(
            long groupId
    ) throws Exception {

        String sql =
                "SELECT COUNT(*)"
                + " FROM group_members"
                + " WHERE group_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                rs.next();

                return rs.getInt(1);
            }
        }
    }

    /**
     * グループを作成し、作成者を主介護者として登録する。
     *
     * 作成したグループIDを返す。
     */
    public long createGroup(
            long userId,
            String name
    ) throws Exception {

        try (
            Connection con =
                    getConnection()
        ) {
            con.setAutoCommit(false);

            try {
                /*
                 * 同時に複数回グループ作成が実行されても、
                 * すでに所属済みなら処理を中止する。
                 */
                if (isAlreadyMember(
                        con,
                        userId
                )) {
                    throw new IllegalStateException(
                            "すでに家族グループに所属しています"
                    );
                }

                long groupId;

                String groupSql =
                        "INSERT INTO family_groups"
                        + " (name, created_by)"
                        + " VALUES (?, ?)";

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    groupSql,
                                    new String[] { "id" }
                            )
                ) {
                    ps.setString(
                            1,
                            name
                    );

                    ps.setLong(
                            2,
                            userId
                    );

                    ps.executeUpdate();

                    try (
                        ResultSet keys =
                                ps.getGeneratedKeys()
                    ) {
                        if (!keys.next()) {
                            throw new IllegalStateException(
                                    "グループIDを取得できません"
                            );
                        }

                        groupId =
                                keys.getLong(1);
                    }
                }

                String memberSql =
                        "INSERT INTO group_members"
                        + " (group_id, user_id, role)"
                        + " VALUES (?, ?, ?)";

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    memberSql
                            )
                ) {
                    ps.setLong(
                            1,
                            groupId
                    );

                    ps.setLong(
                            2,
                            userId
                    );

                    ps.setString(
                            3,
                            Member.ROLE_PRIMARY
                    );

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

    /**
     * グループ名を変更する。
     */
    public boolean rename(
            long groupId,
            String name
    ) throws Exception {

        String sql =
                "UPDATE family_groups"
                + " SET name = ?"
                + " WHERE id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setString(
                    1,
                    name
            );

            ps.setLong(
                    2,
                    groupId
            );

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 続柄と連絡先を更新する。
     *
     * groupIdとuserIdの両方で絞り込み、
     * 別グループのメンバーを更新できないようにする。
     *
     * 空文字はNULLとして保存する。
     */
    public boolean updateProfile(
            long groupId,
            long userId,
            String relationship,
            String contact
    ) throws Exception {

        String sql =
                "UPDATE group_members"
                + " SET relationship = ?,"
                + " contact = ?"
                + " WHERE group_id = ?"
                + " AND user_id = ?";

        try (
            Connection con =
                    getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            if (relationship == null
                    || relationship.isEmpty()) {

                ps.setNull(
                        1,
                        java.sql.Types.VARCHAR
                );

            } else {
                ps.setString(
                        1,
                        relationship
                );
            }

            if (contact == null
                    || contact.isEmpty()) {

                ps.setNull(
                        2,
                        java.sql.Types.VARCHAR
                );

            } else {
                ps.setString(
                        2,
                        contact
                );
            }

            ps.setLong(
                    3,
                    groupId
            );

            ps.setLong(
                    4,
                    userId
            );

            return ps.executeUpdate() == 1;
        }
    }

    /**
     * 主介護者を付け替える。
     *
     * 対象メンバーの存在を確認してから、
     * 現在の主介護者を家族等へ変更する。
     *
     * グループ内の主介護者は常に1名になる。
     */
    public boolean setPrimary(
            long groupId,
            long userId
    ) throws Exception {

        try (
            Connection con =
                    getConnection()
        ) {
            con.setAutoCommit(false);

            try {
                /*
                 * グループ行をロックし、
                 * 同時に複数の主介護者変更が実行されないようにする。
                 */
                if (!lockGroup(
                        con,
                        groupId
                )) {
                    con.rollback();
                    return false;
                }

                if (!isMember(
                        con,
                        groupId,
                        userId
                )) {
                    con.rollback();
                    return false;
                }

                String demoteSql =
                        "UPDATE group_members"
                        + " SET role = ?"
                        + " WHERE group_id = ?"
                        + " AND role = ?";

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    demoteSql
                            )
                ) {
                    ps.setString(
                            1,
                            Member.ROLE_FAMILY
                    );

                    ps.setLong(
                            2,
                            groupId
                    );

                    ps.setString(
                            3,
                            Member.ROLE_PRIMARY
                    );

                    ps.executeUpdate();
                }

                String promoteSql =
                        "UPDATE group_members"
                        + " SET role = ?"
                        + " WHERE group_id = ?"
                        + " AND user_id = ?";

                int updated;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    promoteSql
                            )
                ) {
                    ps.setString(
                            1,
                            Member.ROLE_PRIMARY
                    );

                    ps.setLong(
                            2,
                            groupId
                    );

                    ps.setLong(
                            3,
                            userId
                    );

                    updated =
                            ps.executeUpdate();
                }

                if (updated != 1) {
                    con.rollback();
                    return false;
                }

                con.commit();

                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * 家族等のメンバーをグループから削除する。
     *
     * 本人と主介護者は削除できない。
     *
     * 正式テーブルと旧互換テーブルの担当情報を解除し、
     * 依頼・休息予定の整合性を保ったうえで
     * group_membersから削除する。
     */
    public RemoveMemberResult removeMember(
            long groupId,
            long operatorUserId,
            long targetUserId
    ) throws Exception {

        if (operatorUserId == targetUserId) {
            return RemoveMemberResult.SELF;
        }

        try (
            Connection con =
                    getConnection()
        ) {
            con.setAutoCommit(false);

            try {
                if (!lockGroup(
                        con,
                        groupId
                )) {
                    con.rollback();

                    return RemoveMemberResult.NOT_FOUND;
                }

                String role =
                        findMemberRoleForUpdate(
                                con,
                                groupId,
                                targetUserId
                        );

                if (role == null) {
                    con.rollback();

                    return RemoveMemberResult.NOT_FOUND;
                }

                if (Member.ROLE_PRIMARY.equals(role)) {
                    con.rollback();

                    return RemoveMemberResult.PRIMARY;
                }

                /*
                 * 正式タスクの担当を解除する。
                 */
                executeUpdate(
                        con,
                        "UPDATE tasks"
                        + " SET assignee_id = NULL,"
                        + " lock_version = lock_version + 1,"
                        + " updated_at = CURRENT_TIMESTAMP"
                        + " WHERE group_id = ?"
                        + " AND assignee_id = ?",
                        groupId,
                        targetUserId
                );

                /*
                 * 旧care_tasksにもデータが残っている場合に備え、
                 * 担当を解除する。
                 */
                executeUpdate(
                        con,
                        "UPDATE care_tasks"
                        + " SET assignee_user_id = NULL,"
                        + " updated_at = CURRENT_TIMESTAMP"
                        + " WHERE group_id = ?"
                        + " AND assignee_user_id = ?",
                        groupId,
                        targetUserId
                );

                /*
                 * 削除対象者が引き受けている依頼を回答待ちへ戻す。
                 */
                executeUpdate(
                        con,
                        "UPDATE requests"
                        + " SET status = 'OPEN',"
                        + " accepted_by = NULL,"
                        + " updated_at = CURRENT_TIMESTAMP"
                        + " WHERE group_id = ?"
                        + " AND status = 'ACCEPTED'"
                        + " AND accepted_by = ?",
                        groupId,
                        targetUserId
                );

                /*
                 * 削除対象者が担当している確定済み休息予定を
                 * 調整中へ戻す。
                 */
                executeUpdate(
                        con,
                        "UPDATE rest_plans"
                        + " SET status = 'ADJUSTING',"
                        + " covered_by = NULL,"
                        + " updated_at = CURRENT_TIMESTAMP"
                        + " WHERE group_id = ?"
                        + " AND status = 'CONFIRMED'"
                        + " AND covered_by = ?",
                        groupId,
                        targetUserId
                );

                /*
                 * 依頼の宛先から削除する。
                 */
                executeUpdate(
                        con,
                        "DELETE FROM request_recipients"
                        + " WHERE user_id = ?"
                        + " AND request_id IN ("
                        + " SELECT id"
                        + " FROM requests"
                        + " WHERE group_id = ?"
                        + " )",
                        targetUserId,
                        groupId
                );

                /*
                 * 旧care_request_responsesに残っている回答を削除する。
                 */
                executeUpdate(
                        con,
                        "DELETE FROM care_request_responses"
                        + " WHERE responder_id = ?"
                        + " AND request_id IN ("
                        + " SELECT id"
                        + " FROM care_requests"
                        + " WHERE group_id = ?"
                        + " )",
                        targetUserId,
                        groupId
                );

                String deleteMemberSql =
                        "DELETE FROM group_members"
                        + " WHERE group_id = ?"
                        + " AND user_id = ?"
                        + " AND role <> ?";

                int deleted;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    deleteMemberSql
                            )
                ) {
                    ps.setLong(
                            1,
                            groupId
                    );

                    ps.setLong(
                            2,
                            targetUserId
                    );

                    ps.setString(
                            3,
                            Member.ROLE_PRIMARY
                    );

                    deleted =
                            ps.executeUpdate();
                }

                if (deleted != 1) {
                    con.rollback();

                    return RemoveMemberResult.NOT_FOUND;
                }

                con.commit();

                return RemoveMemberResult.REMOVED;

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * ユーザーがいずれかのグループに所属しているか確認する。
     */
    private boolean isAlreadyMember(
            Connection con,
            long userId
    ) throws Exception {

        String sql =
                "SELECT 1"
                + " FROM group_members"
                + " WHERE user_id = ?";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    /**
     * 指定グループにメンバーが存在するか確認する。
     */
    private boolean isMember(
            Connection con,
            long groupId,
            long userId
    ) throws Exception {

        String sql =
                "SELECT 1"
                + " FROM group_members"
                + " WHERE group_id = ?"
                + " AND user_id = ?";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            ps.setLong(
                    2,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    /**
     * グループ行をロックする。
     */
    private boolean lockGroup(
            Connection con,
            long groupId
    ) throws Exception {

        String sql =
                "SELECT id"
                + " FROM family_groups"
                + " WHERE id = ?"
                + " FOR UPDATE";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next();
            }
        }
    }

    /**
     * メンバー行をロックして役割を取得する。
     */
    private String findMemberRoleForUpdate(
            Connection con,
            long groupId,
            long userId
    ) throws Exception {

        String sql =
                "SELECT role"
                + " FROM group_members"
                + " WHERE group_id = ?"
                + " AND user_id = ?"
                + " FOR UPDATE";

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            ps.setLong(
                    1,
                    groupId
            );

            ps.setLong(
                    2,
                    userId
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {
                return rs.next()
                        ? rs.getString("role")
                        : null;
            }
        }
    }

    /**
     * 可変個の引数を設定して更新SQLを実行する。
     */
    private int executeUpdate(
            Connection con,
            String sql,
            Object... values
    ) throws Exception {

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {
            for (int i = 0;
                    i < values.length;
                    i++) {

                ps.setObject(
                        i + 1,
                        values[i]
                );
            }

            return ps.executeUpdate();
        }
    }

    /**
     * ResultSetをMemberへ変換する。
     */
    private Member mapMember(
            ResultSet rs
    ) throws Exception {

        return new Member(
                rs.getLong("user_id"),
                rs.getString("display_name"),
                rs.getLong("group_id"),
                rs.getString("role"),
                rs.getString("relationship"),
                rs.getString("contact")
        );
    }
}