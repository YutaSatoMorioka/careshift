package careshift.main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.Task;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/**
 * S12 依頼カード作成（入力画面）。URL: /careshift/main/RequestCreate.action
 * S09 の「依頼する」から ?taskId=... で開くと、タスクの内容・所要時間・期限を初期値にする。
 * プレビューの「修正」からは POST で戻り、入力を復元する。
 */
public class RequestCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        RequestForm form;
        if (req.getParameter("content") != null) {
            form = RequestForm.fromRequest(req); // プレビューから戻ったとき
        } else {
            form = RequestForm.blank();
            long taskId = TaskSupport.parseId(req.getParameter("taskId"));
            Task task = taskId < 0 ? null : new TaskDao().find(taskId, me.getGroupId());
            if (task != null) {
                LocalDateTime due = LocalDateTime.of(task.getDueDate(), LocalTime.of(9, 0));
                form.prefill("「" + task.getTitle() + "」をお願いできますか？", task.getDurationMin(),
                        due.isAfter(LocalDateTime.now()) ? due.toString().substring(0, 16) : "", "" + task.getId());
            }
        }
        show(req, resp, user, me, form, null);
    }

    /** 入力画面を表示する（プレビュー・送信の失敗時にも使う）。 */
    static void show(HttpServletRequest req, HttpServletResponse resp, User user, Member me, RequestForm form,
            Map<String, String> errors) throws Exception {
        List<Member> others = new ArrayList<>();
        for (Member m : new GroupDao().findMembers(me.getGroupId())) {
            if (m.getUserId() != user.getId()) others.add(m);
        }
        req.setAttribute("form", form);
        req.setAttribute("errors", errors == null ? new java.util.LinkedHashMap<String, String>() : errors);
        req.setAttribute("others", others);
        req.setAttribute("types", Options.REQUEST_TYPES);
        req.getRequestDispatcher("/careshift/main/request_create.jsp").forward(req, resp);
    }
}
