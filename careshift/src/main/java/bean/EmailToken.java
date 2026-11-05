package bean;

import java.time.LocalDateTime;

/** メールで送るリンク用のトークン（本登録確認 / パスワード再設定）。DBにはハッシュだけを保存する。 */
public class EmailToken {
    public static final String VERIFY = "VERIFY"; // 本登録（24時間有効）
    public static final String RESET = "RESET";   // パスワード再設定（1時間有効・一度限り）

    private final long id;
    private final long userId;
    private final String purpose;
    private final LocalDateTime expiresAt;
    private final boolean used;

    public EmailToken(long id, long userId, String purpose, LocalDateTime expiresAt, boolean used) {
        this.id = id;
        this.userId = userId;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public String getPurpose() { return purpose; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
}
