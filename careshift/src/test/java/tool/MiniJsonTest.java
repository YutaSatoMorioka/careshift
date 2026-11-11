package tool;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MiniJsonTest {

    @Test
    void 文字列の値を取り出す() {
        String json = "{\"id\":\"msg_1\",\"content\":[{\"type\":\"text\",\"text\":\"こんにちは\"}]}";
        assertEquals("こんにちは", MiniJson.firstString(json, "text"));
        assertEquals("msg_1", MiniJson.firstString(json, "id"));
    }

    @Test
    void エスケープを元に戻す() {
        String json = "{\"text\":\"1行目\\n2行目\\t\\\"引用\\\"\\\\\\u3042\"}";
        assertEquals("1行目\n2行目\t\"引用\"\\あ", MiniJson.firstString(json, "text"));
    }

    @Test
    void 見つからない_閉じていない場合はnull() {
        assertEquals(null, MiniJson.firstString("{\"a\":1}", "text"));
        assertEquals(null, MiniJson.firstString("{\"text\":\"閉じていない", "text"));
        assertEquals(null, MiniJson.firstString("{\"text\":123}", "text"));
    }

    @Test
    void 値のほうに名前が出てきても取り違えない() {
        assertEquals("本物", MiniJson.firstString("{\"memo\":\"text\",\"text\":\"本物\"}", "text"));
    }

    @Test
    void 書き込み用のエスケープ() {
        assertEquals("\"a\\\"b\\\\c\\nd\"", MiniJson.quote("a\"b\\c\nd"));
        assertEquals("\"\\u0001\"", MiniJson.quote("\u0001"));
    }
}
