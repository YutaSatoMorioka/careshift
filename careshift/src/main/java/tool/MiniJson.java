package tool;

/** JSON から「最初に見つかった、指定した名前の文字列の値」を取り出す小さな道具（AI の応答の本文を取るため）。 */
public final class MiniJson {

    private MiniJson() {}

    /** "name": "..." の値（エスケープを戻したもの）。なければ null。 */
    public static String firstString(String json, String name) {
        String key = "\"" + name + "\"";
        int from = 0;
        while (true) {
            int k = json.indexOf(key, from);
            if (k < 0) return null;
            int i = k + key.length();
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
            if (i < json.length() && json.charAt(i) == ':') {
                i++;
                while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
                if (i < json.length() && json.charAt(i) == '"') {
                    return readString(json, i + 1);
                }
            }
            from = k + key.length();
        }
    }

    private static String readString(String s, int i) {
        StringBuilder sb = new StringBuilder();
        while (i < s.length()) {
            char c = s.charAt(i++);
            if (c == '"') return sb.toString();
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (i >= s.length()) return null;
            char e = s.charAt(i++);
            switch (e) {
                case 'n': sb.append('\n'); break;
                case 't': sb.append('\t'); break;
                case 'r': sb.append('\r'); break;
                case 'b': sb.append('\b'); break;
                case 'f': sb.append('\f'); break;
                case 'u':
                    if (i + 4 > s.length()) return null;
                    sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                    i += 4;
                    break;
                default: sb.append(e); // \" \\ \/
            }
        }
        return null; // 文字列が閉じていない
    }

    /** JSON の文字列として書き込めるように、エスケープする。 */
    public static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }
}
