package tool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 選択肢の一覧（画面の select に使う）。 */
public final class Options {

    private Options() {}

    /** 続柄（任意項目。未選択も可） */
    public static final List<String> RELATIONSHIPS = List.of("配偶者", "子", "子の配偶者", "きょうだい", "孫", "その他");

    /** 空（未選択）または一覧にある値なら true。 */
    public static boolean isValidRelationship(String s) {
        return s == null || s.isEmpty() || RELATIONSHIPS.contains(s);
    }

    /** タスクの頻度（コード → 表示名） */
    public static final Map<String, String> FREQUENCIES = new LinkedHashMap<>();

    /** タスクの状態（コード → 表示名） */
    public static final Map<String, String> STATUSES = new LinkedHashMap<>();

    /** 依頼の種別（コード → 表示名） */
    public static final Map<String, String> REQUEST_TYPES = new LinkedHashMap<>();

    /** 依頼への回答（コード → 表示名）。設計書 S13 の3択 */
    public static final Map<String, String> RESPONSES = new LinkedHashMap<>();

    static {
        REQUEST_TYPES.put("TASK", "タスク");
        REQUEST_TYPES.put("REST", "休息");

        RESPONSES.put("ACCEPT", "担当する");
        RESPONSES.put("DECLINE", "今回は難しい");
        RESPONSES.put("ALT", "別タスクなら可能");

        FREQUENCIES.put("ONCE", "単発");
        FREQUENCIES.put("DAILY", "毎日");
        FREQUENCIES.put("WEEKLY", "毎週");
        FREQUENCIES.put("MONTHLY", "毎月");

        STATUSES.put("TODO", "未対応");
        STATUSES.put("DOING", "対応中");
        STATUSES.put("DONE", "完了");
    }
}
