package tool;

import java.util.List;

/** 選択肢の一覧（画面の select に使う）。 */
public final class Options {

    private Options() {}

    /** 続柄（任意項目。未選択も可） */
    public static final List<String> RELATIONSHIPS = List.of("配偶者", "子", "子の配偶者", "きょうだい", "孫", "その他");

    /** 空（未選択）または一覧にある値なら true。 */
    public static boolean isValidRelationship(String s) {
        return s == null || s.isEmpty() || RELATIONSHIPS.contains(s);
    }
}
