package tool;

import java.util.List;

/**
 * プランの金額・条件。設計書に具体的な金額・機能の定めがないため、ここに仮の値を置いている。
 * 企画書の内容に合わせて、この1か所を書き換えれば、プラン画面・AI下書きの上限に反映される。
 */
public final class PlanCatalog {

    private PlanCatalog() {}

    /** 有料プランの月額（円）。仮の値 */
    public static final int PAID_PRICE_YEN = 480;

    /** AI下書きの月あたりの利用回数の上限（グループごと）。AI の利用にはコストがかかるため、プランで差をつける */
    public static final int FREE_AI_LIMIT = 5;
    public static final int PAID_AI_LIMIT = 50;

    /** プランの機能・料金の一覧（機能名、無料、有料） */
    public static final List<String[]> FEATURES = List.of(
        new String[] { "月額料金", "0円", PAID_PRICE_YEN + "円" },
        new String[] { "タスク・依頼・休息・通知・カレンダー", "利用できる", "利用できる" },
        new String[] { "負担状況・コラム・分担表（PDF／印刷）", "利用できる", "利用できる" },
        new String[] { "AI依頼下書き（月あたり・グループ全体）", FREE_AI_LIMIT + "回まで", PAID_AI_LIMIT + "回まで" });

    public static int aiLimit(boolean paid) {
        return paid ? PAID_AI_LIMIT : FREE_AI_LIMIT;
    }
}
