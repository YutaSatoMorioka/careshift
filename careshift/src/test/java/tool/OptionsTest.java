package tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OptionsTest {

    @Test
    void 空と一覧にある続柄は有効() {
        assertTrue(Options.isValidRelationship(""));
        assertTrue(Options.isValidRelationship(null));
        assertTrue(Options.isValidRelationship("子"));
    }

    @Test
    void 一覧にない続柄は無効() {
        assertFalse(Options.isValidRelationship("宇宙人"));
    }
}
