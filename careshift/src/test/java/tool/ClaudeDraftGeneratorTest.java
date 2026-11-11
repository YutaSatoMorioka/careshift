package tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClaudeDraftGeneratorTest {

    @Test
    void 日時_内容_所要時間に分ける() throws Exception {
        DraftGenerator.Draft d = ClaudeDraftGenerator.parse("日時: 2026-11-20 10:00\n内容: 通院の付き添いをお願いします。\n所要時間: 120");
        assertEquals("2026-11-20T10:00", d.requestedAt);
        assertEquals("通院の付き添いをお願いします。", d.content);
        assertEquals(Integer.valueOf(120), d.durationMin);
    }

    @Test
    void 内容が複数行でも取れる() throws Exception {
        DraftGenerator.Draft d = ClaudeDraftGenerator.parse("日時: 未定\n内容: 1行目\n2行目\n所要時間: 30");
        assertEquals("1行目\n2行目", d.content);
        assertEquals("", d.requestedAt);
    }

    @Test
    void 内容がなければ利用不可() {
        try {
            ClaudeDraftGenerator.parse("日時: 2026-11-20 10:00\n所要時間: 30");
            assertTrue(false, "例外になるはず");
        } catch (DraftGenerator.UnavailableException expected) {
            assertTrue(true);
        }
    }

    @Test
    void 不正な日付_範囲外の所要時間は空にする() throws Exception {
        DraftGenerator.Draft d = ClaudeDraftGenerator.parse("日時: 2026-13-45 25:99\n内容: 内容\n所要時間: 5000");
        assertEquals("", d.requestedAt);
        assertEquals(null, d.durationMin);
    }

    @Test
    void 長すぎる内容は500文字に切る() throws Exception {
        DraftGenerator.Draft d = ClaudeDraftGenerator.parse("日時: 未定\n内容: " + "あ".repeat(800) + "\n所要時間: 30");
        assertEquals(500, d.content.length());
    }

    @Test
    void 指示には医療判断をさせない旨が入る() {
        String p = ClaudeDraftGenerator.systemPrompt();
        assertTrue(p.contains("医療的な助言や判断は、絶対にしない"));
        assertTrue(p.contains("決めつけない"));
    }
}
