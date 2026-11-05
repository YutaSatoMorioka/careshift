package bean;

/** 家族グループ（family_groups テーブルの1行）。 */
public class FamilyGroup {
    private final long id;
    private final String name;
    private final long createdBy;

    public FamilyGroup(long id, String name, long createdBy) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public long getCreatedBy() { return createdBy; }
}
