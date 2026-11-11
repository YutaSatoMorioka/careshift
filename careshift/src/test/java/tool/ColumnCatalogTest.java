package tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import bean.Article;

class ColumnCatalogTest {

    @Test
    void 記事IDは重複しない() {
        Set<String> ids = new HashSet<>();
        for (Article a : ColumnCatalog.ALL) {
            assertTrue(ids.add(a.getId()), "重複: " + a.getId());
        }
    }

    @Test
    void すべてのカテゴリに記事があり_本文とチェックリストがある() {
        for (String cat : ColumnCatalog.CATEGORIES) {
            assertFalse(ColumnCatalog.byCategory(cat).isEmpty(), cat + " の記事がない");
        }
        for (Article a : ColumnCatalog.ALL) {
            assertTrue(ColumnCatalog.isCategory(a.getCategory()));
            assertFalse(a.getParagraphs().isEmpty());
            assertFalse(a.getChecklist().isEmpty());
            assertTrue(a.getChecklist().size() < 100); // 保存時の番号は3桁まで
        }
    }

    @Test
    void カテゴリが空なら全記事() {
        assertEquals(ColumnCatalog.ALL.size(), ColumnCatalog.byCategory("").size());
        assertEquals(ColumnCatalog.ALL.size(), ColumnCatalog.byCategory(null).size());
    }

    @Test
    void IDで探せる() {
        assertEquals("share-01", ColumnCatalog.find("share-01").getId());
        assertEquals(null, ColumnCatalog.find("nope"));
    }

    @Test
    void おすすめは日付で決まる() {
        LocalDate d = LocalDate.of(2026, 11, 5);
        assertEquals(ColumnCatalog.featured(d), ColumnCatalog.featured(d));
        assertTrue(ColumnCatalog.ALL.contains(ColumnCatalog.featured(d)));
    }
}
