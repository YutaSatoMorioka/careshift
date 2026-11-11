package bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class GroupPlanTest {

    private final LocalDate today = LocalDate.of(2026, 11, 10);

    @Test
    void 無料は有料ではない() {
        assertFalse(GroupPlan.free().isPaid(today));
    }

    @Test
    void 有料は更新日が過ぎても_解約しなければ有料のまま() {
        GroupPlan p = new GroupPlan("PAID", LocalDate.of(2026, 8, 20), false);
        assertTrue(p.isPaid(today));
        assertEquals(LocalDate.of(2026, 11, 20), p.getNextDate(today)); // 1か月ずつ進む
    }

    @Test
    void 解約予約中は期限まで有料_過ぎたら無料() {
        assertTrue(new GroupPlan("PAID", today, true).isPaid(today));            // 期限当日までは有料
        assertTrue(new GroupPlan("PAID", today.plusDays(5), true).isPaid(today));
        assertFalse(new GroupPlan("PAID", today.minusDays(1), true).isPaid(today));
    }

    @Test
    void 解約予約中の表示日は期限のまま() {
        GroupPlan p = new GroupPlan("PAID", today.plusDays(5), true);
        assertEquals(today.plusDays(5), p.getNextDate(today));
    }
}
