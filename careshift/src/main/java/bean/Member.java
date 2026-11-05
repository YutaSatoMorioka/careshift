package bean;

/** グループのメンバー（group_members と users を結合した1行）。 */
public class Member {
    public static final String ROLE_PRIMARY = "PRIMARY"; // 主介護者
    public static final String ROLE_FAMILY = "FAMILY";   // 家族等

    private final long userId;
    private final String displayName;
    private final long groupId;
    private final String role;
    private final String relationship;
    private final String contact;

    public Member(long userId, String displayName, long groupId, String role, String relationship, String contact) {
        this.userId = userId;
        this.displayName = displayName;
        this.groupId = groupId;
        this.role = role;
        this.relationship = relationship == null ? "" : relationship;
        this.contact = contact == null ? "" : contact;
    }

    public long getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
    public long getGroupId() { return groupId; }
    public String getRole() { return role; }
    public String getRelationship() { return relationship; }
    public String getContact() { return contact; }
    public boolean isPrimary() { return ROLE_PRIMARY.equals(role); }
    public String getRoleLabel() { return isPrimary() ? "主介護者" : "家族等"; }
}
