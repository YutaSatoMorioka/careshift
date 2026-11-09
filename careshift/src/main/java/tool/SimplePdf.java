package tool;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 外部ライブラリなしで、日本語の表つきPDFを作る小さな道具（A4縦）。
 *
 * 日本語フォントは PDF に標準で用意されている「HeiseiKakuGo-W5」を名前で指定する方式で、フォントファイルは埋め込まない。
 * そのため、表示はPDFを開くアプリ（ブラウザ・Acrobat・プレビューなど）が持つ日本語フォントに頼る。
 * 文字は Unicode（UCS-2）の16進文字列で書き込む。
 */
public final class SimplePdf {

    private static final float PAGE_W = 595f;
    private static final float PAGE_H = 842f;
    private static final float MARGIN = 40f;
    private static final float TITLE_SIZE = 15f;
    private static final float INFO_SIZE = 9f;
    private static final float BODY_SIZE = 10f;
    private static final float LEADING = 14f;      // 本文の行の高さ
    private static final float CELL_PAD = 3f;      // セルの余白

    private final String title;
    private final List<String> infoLines = new ArrayList<>();
    private String[] headers = new String[0];
    private float[] widths = new float[0];
    private final List<String[]> rows = new ArrayList<>();

    public SimplePdf(String title) {
        this.title = title;
    }

    public SimplePdf info(String line) {
        infoLines.add(line);
        return this;
    }

    /** 表の見出しと、各列の幅（pt）。幅の合計は 515pt 以内にすること。 */
    public SimplePdf columns(String[] headers, float[] widths) {
        this.headers = headers;
        this.widths = widths;
        return this;
    }

    public SimplePdf row(String... cells) {
        rows.add(cells);
        return this;
    }

    // ------------------------------------------------------------------ 組版

    /** 1行に入る長さ（pt）。半角は全角の半分として数える。 */
    static float textWidth(String s, float size) {
        float w = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            w += (c < 0x80 || (c >= 0xFF61 && c <= 0xFF9F)) ? size * 0.5f : size;
        }
        return w;
    }

    /** 幅に収まるように、文字単位で折り返す。 */
    static List<String> wrap(String text, float maxWidth, float size) {
        List<String> lines = new ArrayList<>();
        for (String paragraph : text.replace("\r", "").split("\n", -1)) {
            StringBuilder line = new StringBuilder();
            float w = 0;
            for (int i = 0; i < paragraph.length(); i++) {
                char c = paragraph.charAt(i);
                float cw = (c < 0x80 || (c >= 0xFF61 && c <= 0xFF9F)) ? size * 0.5f : size;
                if (w + cw > maxWidth && line.length() > 0) {
                    lines.add(line.toString());
                    line.setLength(0);
                    w = 0;
                }
                line.append(c);
                w += cw;
            }
            lines.add(line.toString());
        }
        return lines;
    }

    /** ページごとの描画命令を作る。 */
    private List<StringBuilder> layout() {
        List<StringBuilder> pages = new ArrayList<>();
        StringBuilder page = new StringBuilder();
        pages.add(page);
        float y = PAGE_H - MARGIN;

        text(page, MARGIN, y - TITLE_SIZE, TITLE_SIZE, title);
        y -= TITLE_SIZE + 10;
        for (String info : infoLines) {
            text(page, MARGIN, y - INFO_SIZE, INFO_SIZE, info);
            y -= INFO_SIZE + 5;
        }
        y -= 8;

        float tableW = 0;
        for (float w : widths) tableW += w;
        y = headerRow(page, y, tableW);

        for (String[] row : rows) {
            // 各セルを折り返して、行の高さを決める
            List<List<String>> cells = new ArrayList<>();
            int maxLines = 1;
            for (int c = 0; c < widths.length; c++) {
                String cell = c < row.length && row[c] != null ? row[c] : "";
                List<String> lines = wrap(cell, widths[c] - CELL_PAD * 2, BODY_SIZE);
                cells.add(lines);
                maxLines = Math.max(maxLines, lines.size());
            }
            float rowH = maxLines * LEADING + CELL_PAD * 2;
            if (y - rowH < MARGIN + 20) { // 次のページへ（見出しをもう一度書く）
                page = new StringBuilder();
                pages.add(page);
                y = PAGE_H - MARGIN;
                y = headerRow(page, y, tableW);
            }
            float x = MARGIN;
            for (int c = 0; c < widths.length; c++) {
                float ty = y - CELL_PAD - BODY_SIZE;
                for (String line : cells.get(c)) {
                    text(page, x + CELL_PAD, ty, BODY_SIZE, line);
                    ty -= LEADING;
                }
                x += widths[c];
            }
            y -= rowH;
            line(page, MARGIN, y, MARGIN + tableW, y);
        }

        // ページ番号
        for (int i = 0; i < pages.size(); i++) {
            text(pages.get(i), PAGE_W / 2 - 40, MARGIN - 14, INFO_SIZE, "Care Shift  " + (i + 1) + " / " + pages.size());
        }
        return pages;
    }

    private float headerRow(StringBuilder page, float y, float tableW) {
        float rowH = LEADING + CELL_PAD * 2;
        page.append("0.9 g ").append(fmt(MARGIN)).append(' ').append(fmt(y - rowH)).append(' ')
                .append(fmt(tableW)).append(' ').append(fmt(rowH)).append(" re f 0 g\n");
        float x = MARGIN;
        for (int c = 0; c < widths.length; c++) {
            text(page, x + CELL_PAD, y - CELL_PAD - BODY_SIZE, BODY_SIZE, c < headers.length ? headers[c] : "");
            x += widths[c];
        }
        y -= rowH;
        line(page, MARGIN, y, MARGIN + tableW, y);
        return y;
    }

    private static void line(StringBuilder sb, float x1, float y1, float x2, float y2) {
        sb.append("0.5 w ").append(fmt(x1)).append(' ').append(fmt(y1)).append(" m ")
                .append(fmt(x2)).append(' ').append(fmt(y2)).append(" l S\n");
    }

    private static void text(StringBuilder sb, float x, float y, float size, String s) {
        if (s.isEmpty()) return;
        sb.append("BT /F1 ").append(fmt(size)).append(" Tf ").append(fmt(x)).append(' ').append(fmt(y))
                .append(" Td <").append(hex(s)).append("> Tj ET\n");
    }

    /** UCS-2（上位バイト・下位バイトの順）の16進文字列。制御文字は空白にする。 */
    static String hex(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 0x20) c = ' ';
            sb.append(String.format("%04X", (int) c));
        }
        return sb.toString();
    }

    private static String fmt(float f) {
        return String.format(java.util.Locale.ROOT, "%.2f", f);
    }

    // ------------------------------------------------------------------ PDF本体

    public byte[] build() {
        List<StringBuilder> pages = layout();
        int n = pages.size();

        // オブジェクト番号: 1=カタログ 2=ページ一覧 3=フォント(Type0) 4=CIDフォント 5=フォント記述
        //                  6以降=ページ(奇数番)と内容(偶数番)の組
        List<String> objects = new ArrayList<>();
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) kids.append(6 + i * 2).append(" 0 R ");
        objects.add("<< /Type /Pages /Kids [" + kids + "] /Count " + n + " >>");
        objects.add("<< /Type /Font /Subtype /Type0 /BaseFont /HeiseiKakuGo-W5-UniJIS-UCS2-HW-H"
                + " /Encoding /UniJIS-UCS2-HW-H /DescendantFonts [4 0 R] >>");
        objects.add("<< /Type /Font /Subtype /CIDFontType0 /BaseFont /HeiseiKakuGo-W5"
                + " /CIDSystemInfo << /Registry (Adobe) /Ordering (Japan1) /Supplement 2 >>"
                + " /FontDescriptor 5 0 R /DW 1000 /W [231 632 500] >>");
        objects.add("<< /Type /FontDescriptor /FontName /HeiseiKakuGo-W5 /Flags 4 /FontBBox [-92 -250 1010 922]"
                + " /ItalicAngle 0 /Ascent 752 /Descent -271 /CapHeight 737 /StemV 114 >>");

        List<byte[]> streams = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int contentNo = 7 + i * 2;
            objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) PAGE_W + " " + (int) PAGE_H + "]"
                    + " /Resources << /Font << /F1 3 0 R >> >> /Contents " + contentNo + " 0 R >>");
            byte[] content = pages.get(i).toString().getBytes(StandardCharsets.ISO_8859_1);
            streams.add(content);
            objects.add(null); // 内容（ストリーム）は書き込み時に作る
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out, "%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        int streamIndex = 0;
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(out.size());
            write(out, (i + 1) + " 0 obj\n");
            if (objects.get(i) == null) {
                byte[] content = streams.get(streamIndex++);
                write(out, "<< /Length " + content.length + " >>\nstream\n");
                out.write(content, 0, content.length);
                write(out, "\nendstream\n");
            } else {
                write(out, objects.get(i) + "\n");
            }
            write(out, "endobj\n");
        }
        int xref = out.size();
        write(out, "xref\n0 " + (objects.size() + 1) + "\n0000000000 65535 f \n");
        for (int off : offsets) {
            write(out, String.format("%010d 00000 n \n", off));
        }
        write(out, "trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        return out.toByteArray();
    }

    private static void write(ByteArrayOutputStream out, String s) {
        byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
        out.write(b, 0, b.length);
    }
}
