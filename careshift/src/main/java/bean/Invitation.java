package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 招待情報。
 *
 * invitationsテーブルの情報に、
 * 家族グループ名と招待者名を結合したデータを保持する。
 */
public class Invitation {

    private static final DateTimeFormatter EXPIRES_AT_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy年M月d日 HH:mm"
            );

    private final long id;

    private final long groupId;

    private final String groupName;

    private final String relationship;

    private final String role;

    private final long invitedBy;

    private final String inviterName;

    private final LocalDateTime expiresAt;

    private final boolean used;

    public Invitation(
            long id,
            long groupId,
            String groupName,
            String relationship,
            String role,
            long invitedBy,
            String inviterName,
            LocalDateTime expiresAt,
            boolean used
    ) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.relationship =
                relationship == null
                        ? ""
                        : relationship;
        this.role = role;
        this.invitedBy = invitedBy;
        this.inviterName = inviterName;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getRole() {
        return role;
    }

    public long getInvitedBy() {
        return invitedBy;
    }

    public String getInviterName() {
        return inviterName;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    /**
     * 現在時刻が招待期限を過ぎているか判定する。
     */
    public boolean isExpired() {

        if (expiresAt == null) {
            return true;
        }

        return LocalDateTime.now()
                .isAfter(expiresAt);
    }

    /**
     * 画面表示用の続柄・役割。
     *
     * 例:
     * 子／家族等
     *
     * 続柄が未設定の場合:
     * 家族等
     */
    public String getRelationshipRole() {

        String roleLabel =
                Member.ROLE_PRIMARY.equals(role)
                        ? "主介護者"
                        : "家族等";

        if (relationship.isEmpty()) {
            return roleLabel;
        }

        return relationship
                + "／"
                + roleLabel;
    }

    /**
     * 画面表示用の有効期限。
     *
     * 例:
     * 2026年11月30日 18:00
     */
    public String getExpiresAtLabel() {

        if (expiresAt == null) {
            return "不明";
        }

        return expiresAt.format(
                EXPIRES_AT_FORMAT
        );
    }
}