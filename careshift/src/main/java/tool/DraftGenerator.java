package tool;

/**
 * AI001：相談文から、依頼の下書き（日時・内容・所要時間）を作る。
 * 実装を差し替えられるようにしてある（本番は ClaudeDraftGenerator、AIが使えない環境では Unavailable）。
 */
public interface DraftGenerator {

    /** 生成した下書き */
    final class Draft {
        public final String content;
        public final String requestedAt;   // "yyyy-MM-ddTHH:mm"。不明なら空
        public final Integer durationMin;  // 不明なら null

        public Draft(String content, String requestedAt, Integer durationMin) {
            this.content = content;
            this.requestedAt = requestedAt == null ? "" : requestedAt;
            this.durationMin = durationMin;
        }
    }

    /** 生成できなかったとき（AIが未設定・通信失敗・応答が読めない）。利用者には「手入力」を案内する。 */
    class UnavailableException extends Exception {
        private static final long serialVersionUID = 1L;

        public UnavailableException(String message) {
            super(message);
        }
    }

    Draft generate(String prompt) throws UnavailableException;
}
