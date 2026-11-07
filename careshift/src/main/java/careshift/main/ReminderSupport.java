package careshift.main;

import java.time.LocalDate;

import bean.Member;
import bean.Task;
import bean.User;
import dao.NotificationDao;
import dao.SettingsDao;
import dao.TaskDao;

/**
 * 期限リマインド（種別 DEADLINE）の作成。
 * 自分が担当で、まだ完了していないタスクのうち、期限が「明日・今日」または「すでに過ぎている」ものを通知する。
 * 常駐の処理は使わず、ホームや通知一覧を開いたときに作る（同じ通知は二重に作らない）。
 * S20 の「期限リマインド」がオフの人には作らない。
 */
final class ReminderSupport {

    private ReminderSupport() {}

    static void generate(User user, Member me) {
        try {
            if (!new SettingsDao().isReminderOn(user.getId())) {
                return;
            }
            LocalDate today = LocalDate.now();
            NotificationDao notifications = new NotificationDao();
            for (Task t : new TaskDao().findOpenAssignedDueBy(me.getGroupId(), user.getId(), today.plusDays(1))) {
                String kind;
                String message;
                if (t.getDueDate().isBefore(today)) {
                    kind = "OVER";
                    message = "「" + t.getTitle() + "」は期限（" + t.getDueDate() + "）を過ぎています";
                } else if (t.getDueDate().equals(today)) {
                    kind = "TODAY";
                    message = "「" + t.getTitle() + "」の期限は今日です";
                } else {
                    kind = "TMRW";
                    message = "「" + t.getTitle() + "」の期限は明日です";
                }
                // 期限日を変えたタスクには、あらためて通知する
                String key = "DUE:" + t.getId() + ":" + t.getDueDate() + ":" + kind;
                notifications.addOnce(user.getId(), "DEADLINE", message,
                        "/careshift/main/TaskDetail.action?id=" + t.getId(), key);
            }
        } catch (Exception e) {
            e.printStackTrace(); // リマインドの失敗で、画面は止めない
        }
    }
}
