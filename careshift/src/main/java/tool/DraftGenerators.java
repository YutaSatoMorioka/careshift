package tool;

/** 使う下書き生成の実装を選ぶ。設定がなければ「利用不可」（手入力を案内）。試験では差し替えられる。 */
public final class DraftGenerators {

    private static volatile DraftGenerator override;

    private DraftGenerators() {}

    /** 試験用：実装を差し替える（null で元に戻す）。 */
    public static void override(DraftGenerator g) {
        override = g;
    }

    public static DraftGenerator get() {
        if (override != null) return override;
        DraftGenerator claude = ClaudeDraftGenerator.fromConfig();
        if (claude != null) return claude;
        return prompt -> {
            throw new DraftGenerator.UnavailableException("AIが設定されていません");
        };
    }
}
