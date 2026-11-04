package tool;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ログイン失敗の回数管理（S01 の設計書「5回連続失敗で15分間ログイン制限」）。
 * Action はリクエストごとに作られるため、回数はこのクラスの static な共有インスタンスに持たせる。
 */
public class LoginLimiter {

    private static final LoginLimiter SHARED = new LoginLimiter(5, Duration.ofMinutes(15));

    public static LoginLimiter shared() {
        return SHARED;
    }

    private static class Entry {
        int failures;
        Instant lockedUntil;
    }

    private final int maxFailures;
    private final Duration lockTime;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();

    public LoginLimiter(int maxFailures, Duration lockTime) {
        this.maxFailures = maxFailures;
        this.lockTime = lockTime;
    }

    /** ロック中なら true。ロック期間が過ぎていれば解除して false。 */
    public boolean isLocked(String email) {
        Entry e = entries.get(key(email));
        if (e == null) return false;
        synchronized (e) {
            if (e.lockedUntil == null) return false;
            if (Instant.now().isBefore(e.lockedUntil)) return true;
            e.lockedUntil = null;
            e.failures = 0;
            return false;
        }
    }

    public void recordFailure(String email) {
        Entry e = entries.computeIfAbsent(key(email), k -> new Entry());
        synchronized (e) {
            e.failures++;
            if (e.failures >= maxFailures) {
                e.lockedUntil = Instant.now().plus(lockTime);
            }
        }
    }

    public void recordSuccess(String email) {
        entries.remove(key(email));
    }

    private String key(String email) {
        return email.toLowerCase();
    }
}
