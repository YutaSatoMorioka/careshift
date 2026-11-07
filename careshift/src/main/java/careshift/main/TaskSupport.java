package careshift.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

import bean.Member;
import bean.Task;
import bean.User;
import dao.NotificationDao;
import tool.Validator;

/** タスク系の Action で共通に使う小さな道具。 */
final class TaskSupport {

    private TaskSupport() {}

    /** 数字でなければ -1。 */
    static long parseId(String s) {
        String t = Validator.trim(s);
        return t.matches("\\d{1,18}") ? Long.parseLong(t) : -1;
    }

    static Set<Long> memberIds(List<Member> members) {
        Set<Long> ids = new HashSet<>();
        for (Member m : members) {
            ids.add(m.getUserId());
        }
        return ids;
    }

    /** 担当者に選ばれた本人へ通知する（自分で自分を担当にした場合は送らない）。失敗しても処理は続ける。 */
    static void notifyAssigned(User actor, Long assigneeId, Task task) {
        if (assigneeId == null || assigneeId == actor.getId()) {
            return;
        }
        try {
            new NotificationDao().add(assigneeId, "TASK",
                    actor.getDisplayName() + " さんが「" + task.getTitle() + "」の担当にしました",
                    "/careshift/main/TaskDetail.action?id=" + task.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** タスクの担当者へ「削除された」ことを通知する。 */
    static void notifyDeleted(User actor, Task task) {
        Long assigneeId = task.getAssigneeId();
        if (assigneeId == null || assigneeId == actor.getId()) {
            return;
        }
        try {
            new NotificationDao().add(assigneeId, "TASK",
                    actor.getDisplayName() + " さんが担当タスク「" + task.getTitle() + "」を削除しました",
                    "/careshift/main/TaskList.action");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 宛先（recipients）の検証。1人以上必須で、自分以外のグループのメンバーだけ選べる。
     * 問題があれば errors に "recipients" を入れる。正しい宛先の一覧を返す。
     */
    static List<Long> parseRecipients(String[] values, long selfId, Set<Long> memberIds, Map<String, String> errors) {
        List<Long> result = new ArrayList<>();
        boolean invalid = false;
        if (values != null) {
            for (String v : values) {
                String t = Validator.trim(v);
                if (t.isEmpty()) continue;
                if (t.matches("\\d{1,18}")) {
                    long id = Long.parseLong(t);
                    if (id != selfId && memberIds.contains(id)) {
                        if (!result.contains(id)) result.add(id);
                        continue;
                    }
                }
                invalid = true;
            }
        }
        if (result.isEmpty()) {
            errors.put("recipients", "宛先を1人以上選んでください");
        } else if (invalid) {
            errors.put("recipients", "宛先は自分以外の家族から選んでください");
        }
        return result;
    }
}
