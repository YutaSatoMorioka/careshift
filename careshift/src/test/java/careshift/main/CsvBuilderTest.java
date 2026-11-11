package careshift.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class CsvBuilderTest {

    private String text(CsvBuilder b) {
        return new String(b.bytes(), StandardCharsets.UTF_8);
    }

    @Test
    void BOMつきでCRLF() {
        String s = text(new CsvBuilder().row("a", "b"));
        assertTrue(s.startsWith("\uFEFF"));
        assertEquals("\uFEFFa,b\r\n", s);
    }

    @Test
    void カンマ_引用符_改行は囲む() {
        String s = text(new CsvBuilder().row("a,b", "c\"d", "e\nf"));
        assertEquals("\uFEFF\"a,b\",\"c\"\"d\",\"e\nf\"\r\n", s);
    }

    @Test
    void 式として実行されないよう先頭に引用符を付ける() {
        String s = text(new CsvBuilder().row("=1+1", "+2", "-3", "@x", "ふつう"));
        assertEquals("\uFEFF'=1+1,'+2,'-3,'@x,ふつう\r\n", s);
    }

    @Test
    void nullは空() {
        assertEquals("\uFEFF,x\r\n", text(new CsvBuilder().row(null, "x")));
    }
}
