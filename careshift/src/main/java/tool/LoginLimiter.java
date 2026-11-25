package tool;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ログイン失敗回数を管理する。
 *
 * 5回連続失敗すると15分間ログインを制限する。
 *
 * サーバー再起動時には記録が初期化される。
 * 複数サーバー構成では共有されないため、
 * 現在の単一Tomcat構成向けの制限である。
 */
public final class LoginLimiter {

    private static final int DEFAULT_MAX_FAILURES =
            5;

    private static final Duration DEFAULT_LOCK_TIME =
            Duration.ofMinutes(15);

    /**
     * 最後のアクセスから24時間経過した記録を削除する。
     */
    private static final Duration ENTRY_RETENTION =
            Duration.ofHours(24);

    private static final LoginLimiter SHARED =
            new LoginLimiter(
                    DEFAULT_MAX_FAILURES,
                    DEFAULT_LOCK_TIME
            );

    /**
     * ログイン失敗記録。
     */
    private static final class Entry {

        private int failures;

        private Instant lockedUntil;

        private Instant lastTouched =
                Instant.now();
    }

    private final int maxFailures;

    private final Duration lockTime;

    private final Map<String, Entry> entries =
            new ConcurrentHashMap<>();

    public LoginLimiter(
            int maxFailures,
            Duration lockTime
    ) {

        if (maxFailures < 1) {
            throw new IllegalArgumentException(
                    "maxFailuresは1以上である必要があります"
            );
        }

        if (lockTime == null
                || lockTime.isNegative()
                || lockTime.isZero()) {

            throw new IllegalArgumentException(
                    "lockTimeは正の時間である必要があります"
            );
        }

        this.maxFailures = maxFailures;
        this.lockTime = lockTime;
    }

    /**
     * 共有インスタンスを返す。
     */
    public static LoginLimiter shared() {
        return SHARED;
    }

    /**
     * ロック中ならtrueを返す。
     *
     * ロック期間を過ぎている場合は、
     * 失敗回数とロックを解除してfalseを返す。
     */
    public boolean isLocked(
            String identifier
    ) {

        cleanupExpiredEntries();

        String normalized =
                key(identifier);

        Entry entry =
                entries.get(normalized);

        if (entry == null) {
            return false;
        }

        synchronized (entry) {
            entry.lastTouched =
                    Instant.now();

            if (entry.lockedUntil == null) {
                return false;
            }

            if (Instant.now().isBefore(
                    entry.lockedUntil
            )) {
                return true;
            }

            entry.lockedUntil =
                    null;

            entry.failures =
                    0;

            return false;
        }
    }

    /**
     * ログイン失敗を記録する。
     */
    public void recordFailure(
            String identifier
    ) {

        cleanupExpiredEntries();

        String normalized =
                key(identifier);

        Entry entry =
                entries.computeIfAbsent(
                        normalized,
                        ignored -> new Entry()
                );

        synchronized (entry) {
            entry.lastTouched =
                    Instant.now();

            /*
             * すでにロック中なら失敗回数を増やさず、
             * 現在のロック期限を維持する。
             */
            if (entry.lockedUntil != null
                    && Instant.now().isBefore(
                            entry.lockedUntil
                    )) {
                return;
            }

            entry.failures++;

            if (entry.failures >= maxFailures) {
                entry.lockedUntil =
                        Instant.now()
                                .plus(lockTime);
            }
        }
    }

    /**
     * ログイン成功時に失敗記録を削除する。
     */
    public void recordSuccess(
            String identifier
    ) {

        entries.remove(
                key(identifier)
        );
    }

    /**
     * 最後のアクセスから24時間を超えた記録を削除する。
     *
     * 失敗途中で放置されたメールアドレスの記録が
     * メモリへ残り続けることを防ぐ。
     */
    private void cleanupExpiredEntries() {

        Instant threshold =
                Instant.now()
                        .minus(
                                ENTRY_RETENTION
                        );

        entries.entrySet()
                .removeIf(
                        entry -> {
                            Entry value =
                                    entry.getValue();

                            synchronized (value) {
                                return value.lastTouched
                                        .isBefore(threshold);
                            }
                        }
                );
    }

    /**
     * 識別子を比較用文字列へ変換する。
     */
    private String key(
            String identifier
    ) {

        if (identifier == null) {
            return "";
        }

        return identifier.trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }
}