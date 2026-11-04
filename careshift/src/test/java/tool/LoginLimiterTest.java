package tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class LoginLimiterTest {

    @Test
    void 五回連続で失敗するとロックされる() {
        LoginLimiter limiter = new LoginLimiter(5, Duration.ofMinutes(15));
        for (int i = 0; i < 4; i++) {
            limiter.recordFailure("a@b.com");
        }
        assertFalse(limiter.isLocked("a@b.com"));
        limiter.recordFailure("a@b.com");
        assertTrue(limiter.isLocked("a@b.com"));
    }

    @Test
    void 成功すると回数がリセットされる() {
        LoginLimiter limiter = new LoginLimiter(2, Duration.ofMinutes(15));
        limiter.recordFailure("a@b.com");
        limiter.recordSuccess("a@b.com");
        limiter.recordFailure("a@b.com");
        assertFalse(limiter.isLocked("a@b.com"));
    }

    @Test
    void ロック期間が過ぎれば解除される() {
        LoginLimiter limiter = new LoginLimiter(1, Duration.ZERO);
        limiter.recordFailure("a@b.com");
        assertFalse(limiter.isLocked("a@b.com"));
    }

    @Test
    void メールの大文字小文字は区別しない() {
        LoginLimiter limiter = new LoginLimiter(1, Duration.ofMinutes(15));
        limiter.recordFailure("A@B.com");
        assertTrue(limiter.isLocked("a@b.COM"));
    }
}
