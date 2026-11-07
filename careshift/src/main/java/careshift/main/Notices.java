package careshift.main;

import dao.NotificationDao;

/** 通知の送信（失敗しても本処理は止めない）。type は REQUEST（依頼）/ ANSWER（回答）/ REST（休息）/ TASK / GROUP。 */
final class Notices {

    private Notices() {}

    static void send(long userId, String type, String message, String linkPath) {
        try {
            new NotificationDao().add(userId, type, message, linkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String requestLink(long requestId) {
        return "/careshift/main/RequestDetail.action?id=" + requestId;
    }

    /** 通知文に入れる内容は先頭だけにする（長い相談文をそのまま通知に載せない）。 */
    static String brief(String text, int max) {
        String t = text.replace("\n", " ").replace("\r", "");
        return t.length() > max ? t.substring(0, max) + "…" : t;
    }
}
