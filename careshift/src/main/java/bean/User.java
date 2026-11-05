package bean;

/** ユーザー（users テーブルの1行）。ログイン後はセッションに "user" という名前で保存する。 */
public class User {
    private final long id;
    private final String displayName;
    private final String email;
    private final String passwordHash;
    private final boolean emailVerified;

    public User(long id, String displayName, String email, String passwordHash, boolean emailVerified) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.emailVerified = emailVerified;
    }

    public long getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEmailVerified() { return emailVerified; }
}
