package careshift.main;

import java.nio.charset.StandardCharsets;

/**
 * CSV を作る小さな道具（Excel で開ける UTF-8・BOM付き・改行はCRLF）。
 * 表計算ソフトで式として実行されないよう、= + - @ で始まるセルの先頭には ' を付ける。
 */
final class CsvBuilder {

    private final StringBuilder sb = new StringBuilder("\uFEFF");
    private boolean lineStart = true;

    CsvBuilder cell(String value) {
        if (!lineStart) sb.append(',');
        lineStart = false;
        String v = value == null ? "" : value;
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        sb.append(v);
        return this;
    }

    CsvBuilder row(String... cells) {
        for (String c : cells) cell(c);
        return endRow();
    }

    CsvBuilder endRow() {
        sb.append("\r\n");
        lineStart = true;
        return this;
    }

    byte[] bytes() {
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
