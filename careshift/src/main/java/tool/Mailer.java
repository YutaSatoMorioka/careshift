package tool;

import jakarta.servlet.http.HttpServletRequest;

/**
 * メール送信の窓口。
 * いまは「開発用」で、メール本文を Eclipse のコンソールに出力するだけ（実際には送らない）。
 * 本番では、ここを SMTP 送信（Jakarta Mail）に差し替える。呼び出し側は変更不要。
 */
public final class Mailer {

    private Mailer() {}

    public static void send(String to, String subject, String body) throws Exception {
        System.out.println("==================== [開発用メール] ====================");
        System.out.println("宛先: " + to);
        System.out.println("件名: " + subject);
        System.out.println(body);
        System.out.println("========================================================");
    }

    /** メール内リンクの先頭部分（例: http://localhost:8080/careshift）を、リクエストから組み立てる。 */
    public static String baseUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        int port = req.getServerPort();
        boolean defaultPort = ("http".equals(scheme) && port == 80) || ("https".equals(scheme) && port == 443);
        return scheme + "://" + req.getServerName() + (defaultPort ? "" : ":" + port) + req.getContextPath();
    }
}
