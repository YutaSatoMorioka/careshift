package tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {

    @Test
    void 正しいパスワードだけが一致する() {
        String stored = PasswordUtil.hash("password123");
        assertTrue(PasswordUtil.verify("password123", stored));
        assertFalse(PasswordUtil.verify("wrong", stored));
    }

    @Test
    void 同じパスワードでもハッシュは毎回違う() {
        assertNotEquals(PasswordUtil.hash("pw"), PasswordUtil.hash("pw"));
    }
}
