package careshift.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import bean.CalendarCell;

class TaskFormTest {

    private TaskForm form(String title, String due, String duration) {
        TaskForm f = TaskForm.blank();
        try {
            // 画面からの入力を模して、各項目を直接セットする代わりに fromTask は使わず、リフレクションで設定
            set(f, "title", title);
            set(f, "dueDate", due);
            set(f, "duration", duration);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return f;
    }

    private void set(TaskForm f, String field, String value) throws Exception {
        java.lang.reflect.Field fld = TaskForm.class.getDeclaredField(field);
        fld.setAccessible(true);
        fld.set(f, value);
    }

    @Test
    void 必須項目が空ならエラー() {
        Map<String, String> errors = form("", "", "").validate(false, Set.of());
        assertEquals(Set.of("taskTitle", "dueDate", "duration"), errors.keySet());
    }

    @Test
    void 所要時間は5分から1440分まで() {
        assertTrue(form("a", "2026-11-30", "5").validate(false, Set.of()).isEmpty());
        assertTrue(form("a", "2026-11-30", "1440").validate(false, Set.of()).isEmpty());
        assertTrue(form("a", "2026-11-30", "4").validate(false, Set.of()).containsKey("duration"));
        assertTrue(form("a", "2026-11-30", "1441").validate(false, Set.of()).containsKey("duration"));
        assertTrue(form("a", "2026-11-30", "abc").validate(false, Set.of()).containsKey("duration"));
    }

    @Test
    void タスク内容は100文字まで() {
        assertTrue(form("あ".repeat(100), "2026-11-30", "30").validate(false, Set.of()).isEmpty());
        assertTrue(form("あ".repeat(101), "2026-11-30", "30").validate(false, Set.of()).containsKey("taskTitle"));
    }

    @Test
    void 医療情報らしい語を検出する() {
        assertTrue(TaskForm.looksMedical("糖尿病の薬を渡す"));
        assertFalse(TaskForm.looksMedical("病院へ付き添う"));
    }

    @Test
    void カレンダーは日曜始まりで週ごとに7日() {
        List<List<CalendarCell>> weeks = CalendarAction.buildWeeks(YearMonth.of(2026, 2));
        assertEquals(4, weeks.size()); // 2026年2月は日曜始まり・28日
        assertEquals(DayOfWeek.SUNDAY, weeks.get(0).get(0).getDate().getDayOfWeek());
        assertTrue(weeks.stream().allMatch(w -> w.size() == 7));
    }
}
