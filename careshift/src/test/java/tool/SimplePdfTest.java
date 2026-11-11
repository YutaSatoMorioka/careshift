package tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

class SimplePdfTest {

    @Test
    void 文字はUCS2の16進文字列になる() {
        assertEquals("62C5", SimplePdf.hex("担"));
        assertEquals("00410042", SimplePdf.hex("AB"));
        assertEquals("0020", SimplePdf.hex("\n")); // 制御文字は空白
    }

    @Test
    void 全角は1文字分_半角は半分で数える() {
        assertEquals(20f, SimplePdf.textWidth("あい", 10f));
        assertEquals(10f, SimplePdf.textWidth("ab", 10f));
    }

    @Test
    void 幅を超えたら折り返す() {
        List<String> lines = SimplePdf.wrap("あいうえおかきくけこ", 50f, 10f); // 全角5文字まで
        assertEquals(List.of("あいうえお", "かきくけこ"), lines);
        assertEquals(List.of("あ", "い"), SimplePdf.wrap("あ\nい", 100f, 10f)); // 改行はそのまま
    }

    @Test
    void PDFとして必要な部品がそろう() {
        byte[] bytes = new SimplePdf("題名").info("情報").columns(new String[] { "A", "B" }, new float[] { 100, 100 })
                .row("1", "2").build();
        String text = new String(bytes, StandardCharsets.ISO_8859_1);
        assertTrue(text.startsWith("%PDF-1.4"));
        assertTrue(text.contains("/UniJIS-UCS2-HW-H"));
        assertTrue(text.contains("/Count 1"));
        assertTrue(text.trim().endsWith("%%EOF"));
        assertTrue(text.contains(SimplePdf.hex("題名")));
    }

    @Test
    void 行が多いと複数ページになり見出しが繰り返される() {
        SimplePdf pdf = new SimplePdf("多い").columns(new String[] { "見出し" }, new float[] { 200 });
        for (int i = 0; i < 100; i++) {
            pdf.row("行" + i);
        }
        String text = new String(pdf.build(), StandardCharsets.ISO_8859_1);
        int pages = text.split("/Type /Page /Parent", -1).length - 1;
        assertTrue(pages >= 3);
        int headerCount = text.split(SimplePdf.hex("見出し"), -1).length - 1;
        assertEquals(pages, headerCount); // 各ページに見出し
    }
}
