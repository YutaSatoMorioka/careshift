package tool;

import java.util.regex.Pattern;

/** 画面共通の入力チェック。 */
public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validator() {}

    public static boolean isEmail(String s) {
        return s != null && s.length() <= 254 && EMAIL.matcher(s).matches();
    }

    /** null を空文字にし、前後の空白を除く。 */
    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** null を空文字にする（パスワードは空白も有効な文字なので trim しない）。 */
    public static String nz(String s) {
        return s == null ? "" : s;
    }
}
