package careshift.main;

import java.util.LinkedHashMap;
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
 * S08 タスク登録（task_create.jsp の送信先）。URL: /careshift/main/TaskCreateExecute.action
 * 内容・期限は必須、内容は100文字以内、所要時間は5分〜24時間。
 * 過去の期限・医療情報らしい語は、注意を表示して確認後に登録できる。担当者を選んだら本人へ通知する。
 * 保存に失敗したときは入力を保持して再試行できる。
 */
public class TaskCreateExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        TaskForm form = TaskForm.fromRequest(req);
        Map<String, String> errors = form.validate(false, TaskSupport.memberIds(members));

        // 確認が必要な事項は、確認済み（confirm=1）でなければ再表示して確かめる
        List<String> warnings = errors.isEmpty() ? form.warnings(null) : List.of();
        boolean confirmed = "1".equals(req.getParameter("confirm"));
        if (!errors.isEmpty() || (!warnings.isEmpty() && !confirmed)) {
            redisplay(req, resp, form, errors, warnings, members);
            return;
        }

        try {
            TaskDao dao = new TaskDao();
            long id = dao.insert(me.getGroupId(), form.title(), form.dueDateValue(), form.durationValue(),
                    form.frequencyValue(), form.assigneeValue(), user.getId());
            Task created = dao.find(id, me.getGroupId());
            TaskSupport.notifyAssigned(user, form.assigneeValue(), created);
            flash(req, "タスク「" + form.title() + "」を登録しました。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskList.action");
        } catch (Exception e) {
            e.printStackTrace();
            errors = new LinkedHashMap<>();
            errors.put("save", "保存に失敗しました。入力内容はそのままです。もう一度お試しください。");
            redisplay(req, resp, form, errors, List.of(), members); // 入力を保持して再試行
        }
    }

    private void redisplay(HttpServletRequest req, HttpServletResponse resp, TaskForm form,
            Map<String, String> errors, List<String> warnings, List<Member> members) throws Exception {
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        req.setAttribute("warnings", warnings);
        req.setAttribute("members", members);
        req.setAttribute("frequencies", Options.FREQUENCIES);
        req.getRequestDispatcher("/careshift/main/task_create.jsp").forward(req, resp);
    }
}
