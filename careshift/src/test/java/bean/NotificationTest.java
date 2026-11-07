package bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class NotificationTest {

    private Notification n(String type, boolean read) {
        return new Notification(1, type, "内容", null, read, LocalDateTime.of(2026, 11, 5, 9, 5));
    }

    @Test
    void 設計書の種別名で表示する() {
        assertEquals("依頼", n("REQUEST", false).getTypeLabel());
        assertEquals("回答", n("ANSWER", false).getTypeLabel());
        assertEquals("休息", n("REST", false).getTypeLabel());
        assertEquals("期限", n("DEADLINE", false).getTypeLabel());
        assertEquals("その他", n("TASK", false).getTypeLabel());
        assertEquals("その他", n("GROUP", false).getTypeLabel());
    }

    @Test
    void 未読と既読を識別する() {
        assertTrue(n("ANSWER", false).isUnread());
        assertFalse(n("ANSWER", true).isUnread());
    }

    @Test
    void 日時の表示形式() {
        assertEquals("2026/11/05 09:05", n("ANSWER", false).getCreatedAtLabel());
    }

    @Test
    void リンクがなければ空文字() {
        assertEquals("", n("ANSWER", false).getLinkPath());
    }
}
