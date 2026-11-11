package careshift.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class SuggestionCatalogTest {

    @Test
    void 条件ごとに3件から5件() {
        for (String c : List.of("CALL", "RESERVE", "SHOP", "INFO")) {
            int n = SuggestionCatalog.forCondition(c).size();
            assertTrue(n >= 3 && n <= 5, c + " は " + n + " 件");
        }
    }

    @Test
    void すべては5件で複数の条件から選ぶ() {
        List<Suggestion> all = SuggestionCatalog.forCondition("");
        assertEquals(5, all.size());
        assertEquals(4, all.stream().map(Suggestion::getCondition).distinct().count());
    }

    @Test
    void 不正な条件はすべてと同じ() {
        assertEquals(SuggestionCatalog.forCondition("").size(), SuggestionCatalog.forCondition("ZZZ").size());
    }

    @Test
    void 所要時間の目安は登録できる範囲() {
        for (Suggestion s : SuggestionCatalog.forCondition("")) {
            assertTrue(s.getDurationMin() >= 5 && s.getDurationMin() <= 1440);
            assertTrue(s.getTitle().length() <= 100);
        }
    }
}
