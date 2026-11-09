package careshift.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 遠方家族向けの固定候補（電話・予約・買い物・情報収集）。AI は使わず、常にこの固定候補を3〜5件ずつ出す。
 * 医療的な判断にあたるものは含めない。
 */
final class SuggestionCatalog {

    /** 実施可能条件（コード → 表示名）。空は「すべて」 */
    static final Map<String, String> CONDITIONS = new LinkedHashMap<>();
    static {
        CONDITIONS.put("", "すべて");
        CONDITIONS.put("CALL", "電話");
        CONDITIONS.put("RESERVE", "予約");
        CONDITIONS.put("SHOP", "買い物（ネット注文）");
        CONDITIONS.put("INFO", "情報収集");
    }

    private static final List<Suggestion> ALL = List.of(
        new Suggestion("CALL", "週に1回、電話で本人の話し相手になる", "決まった曜日・時間に電話をして、近況を聞きます。", 20, 3),
        new Suggestion("CALL", "ケアマネジャーに電話で近況を共有する", "家族を代表して、状況の共有や相談の窓口になります。", 30, 5),
        new Suggestion("CALL", "親戚・家族への連絡係を引き受ける", "状況の共有が必要な親戚への連絡をまとめて担当します。", 20, 5),
        new Suggestion("CALL", "近くにいる家族に電話で困りごとを聞く", "負担の大きい家族の話を聞き、手伝えることを探します。", 20, 2),

        new Suggestion("RESERVE", "介護タクシー・送迎サービスを予約する", "通院や外出の予定に合わせて、送迎の手配をします。", 30, 5),
        new Suggestion("RESERVE", "デイサービスの見学を予約する", "候補の事業所に連絡して、見学の日時を決めます。", 30, 7),
        new Suggestion("RESERVE", "ショートステイの空き状況を問い合わせる", "家族の休息に備えて、利用できる時期を確認します。", 40, 7),
        new Suggestion("RESERVE", "宅配弁当・食事サービスを手配する", "食事の準備の負担を減らすため、サービスを探して申し込みます。", 30, 5),

        new Suggestion("SHOP", "日用品・食料品をネットで注文して届ける", "必要なものを近くの家族と確認して、まとめて注文します。", 30, 3),
        new Suggestion("SHOP", "消耗品（日用品）の定期便を設定する", "なくなりやすいものを、定期的に届くように設定します。", 40, 7),
        new Suggestion("SHOP", "季節の衣類・寝具をネットで注文する", "本人の希望を聞いて、季節に合わせたものを選んで注文します。", 40, 7),

        new Suggestion("INFO", "利用できる介護サービス・制度を調べてまとめる", "公式の情報をもとに、家族で共有できる資料にまとめます。", 60, 7),
        new Suggestion("INFO", "地域の相談窓口を調べて共有する", "地域包括支援センターなどの場所・連絡先・受付時間を調べます。", 40, 5),
        new Suggestion("INFO", "近隣のショートステイ・施設を比較する", "場所・費用・受け入れ条件を表にして比べます。", 60, 10));

    private SuggestionCatalog() {}

    /** 条件に合う候補を3〜5件。「すべて」は各条件から1〜2件ずつで5件。 */
    static List<Suggestion> forCondition(String condition) {
        List<Suggestion> result = new ArrayList<>();
        if (condition == null || condition.isEmpty() || !CONDITIONS.containsKey(condition)) {
            // 各条件の先頭から順に1件ずつ、5件になるまで
            for (int round = 0; round < 2 && result.size() < 5; round++) {
                for (String c : List.of("CALL", "RESERVE", "SHOP", "INFO")) {
                    int seen = 0;
                    for (Suggestion s : ALL) {
                        if (s.getCondition().equals(c) && seen++ == round && result.size() < 5) {
                            result.add(s);
                        }
                    }
                }
            }
            return result;
        }
        for (Suggestion s : ALL) {
            if (s.getCondition().equals(condition) && result.size() < 5) {
                result.add(s);
            }
        }
        return result;
    }
}
