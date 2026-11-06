package bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class RestPlanTest {

    private RestPlan plan(String start, String end, String status) {
        return new RestPlan(1, 1, LocalDate.of(2030, 1, 1), LocalTime.parse(start), LocalTime.parse(end), "休息",
                status, 1, "A", null, null);
    }

    @Test
    void 時間の長さと表示() {
        assertEquals(180, plan("10:00", "13:00", "ADJUSTING").getMinutes());
        assertEquals("3時間", plan("10:00", "13:00", "ADJUSTING").getDurationLabel());
        assertEquals("1時間30分", plan("10:00", "11:30", "ADJUSTING").getDurationLabel());
        assertEquals("45分", plan("10:00", "10:45", "ADJUSTING").getDurationLabel());
        assertEquals("10:00〜13:00", plan("10:00", "13:00", "ADJUSTING").getTimeRange());
    }

    @Test
    void 状態の表示() {
        assertEquals("調整中", plan("10:00", "11:00", "ADJUSTING").getStatusLabel());
        assertEquals("確定", plan("10:00", "11:00", "CONFIRMED").getStatusLabel());
        assertEquals("取消", plan("10:00", "11:00", "CANCELLED").getStatusLabel());
    }

    @Test
    void 開始前かどうか() {
        assertFalse(plan("10:00", "11:00", "ADJUSTING").isStarted());
    }

    private RequestCard card(String status, LocalDateTime at, Long acceptedBy, String acceptedName) {
        return new RequestCard(1, 1, "TASK", "内容", at, null, null, null, 10, "送信者", status, acceptedBy, acceptedName);
    }

    @Test
    void 依頼の状態と回答可否() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        assertEquals("回答待ち", card("OPEN", future, null, null).getStatusLabel());
        assertEquals("期限切れ", card("OPEN", past, null, null).getStatusLabel());
        assertEquals("取り下げ済み", card("WITHDRAWN", future, null, null).getStatusLabel());
        assertTrue(card("ACCEPTED", future, 2L, "B").getStatusLabel().contains("B"));

        assertEquals("", card("OPEN", future, null, null).getAnswerBlockedReason(5));
        assertFalse(card("WITHDRAWN", future, null, null).getAnswerBlockedReason(5).isEmpty());
        assertFalse(card("OPEN", past, null, null).getAnswerBlockedReason(5).isEmpty());
        assertFalse(card("ACCEPTED", future, 2L, "B").getAnswerBlockedReason(5).isEmpty());
        assertEquals("", card("ACCEPTED", future, 5L, "me").getAnswerBlockedReason(5)); // 引き受けた本人は変更できる
    }

    @Test
    void 取り下げできるのは回答待ちで期限前だけ() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        assertTrue(card("OPEN", future, null, null).isWithdrawable());
        assertFalse(card("ACCEPTED", future, 2L, "B").isWithdrawable());
        assertFalse(card("WITHDRAWN", future, null, null).isWithdrawable());
        assertFalse(card("OPEN", LocalDateTime.now().minusMinutes(1), null, null).isWithdrawable());
    }
}
