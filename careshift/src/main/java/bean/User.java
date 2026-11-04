package bean;

/** ユーザー（S02 新規登録で作成、S01 ログインで参照）。セッションには "user" という名前で保存する。 */
public class User {
    private final long id;
    private final String displayName;
    private final String email;
    private final String passwordHash;

    public User(long id, String displayName, String email, String passwordHash) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
}
