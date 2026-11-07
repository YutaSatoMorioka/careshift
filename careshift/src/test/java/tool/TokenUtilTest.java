package tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class TokenUtilTest {

    @Test
    void トークンは毎回違う値になる() {
        assertNotEquals(TokenUtil.generate(), TokenUtil.generate());
    }

    @Test
    void ハッシュは同じ入力なら同じで64文字() {
        assertEquals(TokenUtil.hash("abc"), TokenUtil.hash("abc"));
        assertEquals(64, TokenUtil.hash("abc").length());
    }
}
