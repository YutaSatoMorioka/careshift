package careshift.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

class RequestFormTest {

    private static final Set<Long> MEMBERS = Set.of(1L, 2L, 3L);

    private RequestForm form(String content, String requestedAt, String duration, String... recipients) {
        RequestForm f = RequestForm.blank();
        f.prefill(content, null, requestedAt, "");
        try {
            java.lang.reflect.Field d = RequestForm.class.getDeclaredField("duration");
            d.setAccessible(true);
            d.set(f, duration);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        for (String r : recipients) {
            f.getRecipients().add(r);
        }
        return f;
    }

    private String tomorrow() {
        return LocalDateTime.now().plusDays(1).withSecond(0).withNano(0).toString().substring(0, 16);
    }

    @Test
    void 必須項目が空ならエラー() {
        Map<String, String> errors = form("", "", "").validate(1, MEMBERS);
        assertEquals(Set.of("content", "requestedAt", "recipients"), errors.keySet());
    }

    @Test
    void 依頼内容は500文字まで() {
        assertTrue(form("a".repeat(500), tomorrow(), "", "2").validate(1, MEMBERS).isEmpty());
        assertTrue(form("a".repeat(501), tomorrow(), "", "2").validate(1, MEMBERS).containsKey("content"));
    }

    @Test
    void 希望日時は未来だけ() {
        String past = LocalDateTime.now().minusDays(1).toString().substring(0, 16);
        assertTrue(form("a", past, "", "2").validate(1, MEMBERS).containsKey("requestedAt"));
    }

    @Test
    void 宛先は自分以外のメンバーだけ() {
        assertTrue(form("a", tomorrow(), "", "1").validate(1, MEMBERS).containsKey("recipients"));
        assertTrue(form("a", tomorrow(), "", "99").validate(1, MEMBERS).containsKey("recipients"));
        assertTrue(form("a", tomorrow(), "", "2", "99").validate(1, MEMBERS).containsKey("recipients"));
        assertTrue(form("a", tomorrow(), "", "2", "3").validate(1, MEMBERS).isEmpty());
    }

    @Test
    void 所要時間は任意で5分から1440分() {
        assertTrue(form("a", tomorrow(), "", "2").validate(1, MEMBERS).isEmpty());
        assertTrue(form("a", tomorrow(), "5", "2").validate(1, MEMBERS).isEmpty());
        assertTrue(form("a", tomorrow(), "4", "2").validate(1, MEMBERS).containsKey("duration"));
        assertTrue(form("a", tomorrow(), "1441", "2").validate(1, MEMBERS).containsKey("duration"));
    }
}
