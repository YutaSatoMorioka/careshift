package bean;

import java.time.LocalDateTime;

/** 招待（invitations と、グループ名・招待者名を結合した1行）。 */
public class Invitation {
    private final long id;
    private final long groupId;
    private final String groupName;
    private final String relationship;
    private final String role;
    private final long invitedBy;
    private final String inviterName;
    private final LocalDateTime expiresAt;
    private final boolean used;

    public Invitation(long id, long groupId, String groupName, String relationship, String role, long invitedBy,
            String inviterName, LocalDateTime expiresAt, boolean used) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.relationship = relationship == null ? "" : relationship;
        this.role = role;
        this.invitedBy = invitedBy;
        this.inviterName = inviterName;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public long getId() { return id; }
    public long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getRelationship() { return relationship; }
    public String getRole() { return role; }
    public long getInvitedBy() { return invitedBy; }
    public String getInviterName() { return inviterName; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }

    /** 画面表示用「続柄／役割」。例: 子／家族等 */
    public String getRelationshipRole() {
        String roleLabel = Member.ROLE_PRIMARY.equals(role) ? "主介護者" : "家族等";
        return relationship.isEmpty() ? roleLabel : relationship + "／" + roleLabel;
    }
}
