package careshift.main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
 * S08-2 タスク更新（task_edit.jsp の保存ボタンの送信先）。URL: /careshift/main/TaskEditExecute.action
 * 入力検証は登録画面と共通。先に誰かが更新していた場合は、最新の内容を再読込して表示する。
 */
public class TaskEditExecuteAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String listUrl = req.getContextPath() + "/careshift/main/TaskList.action";
        long id = TaskSupport.parseId(req.getParameter("id"));
        TaskDao dao = new TaskDao();
        Task task = dao.find(id, me.getGroupId());
        if (task == null) {
            flashError(req, "タスクが見つかりません。すでに削除されています。");
            resp.sendRedirect(listUrl);
            return;
        }
        if (!task.canEdit(user.getId(), me.isPrimary())) {
            flashError(req, "このタスクを編集できるのは、登録者・担当者・主介護者のみです。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskDetail.action?id=" + id);
            return;
        }

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        TaskForm form = TaskForm.fromRequest(req);
        Map<String, String> errors = form.validate(true, TaskSupport.memberIds(members));
        List<String> warnings = errors.isEmpty() ? form.warnings(task) : List.of();
        boolean confirmed = "1".equals(req.getParameter("confirm"));
        if (!errors.isEmpty() || (!warnings.isEmpty() && !confirmed)) {
            redisplay(req, resp, task, form, errors, warnings, members);
            return;
        }

        try {
            boolean updated = dao.update(id, me.getGroupId(), form.title(), form.dueDateValue(),
                    form.durationValue(), form.frequencyValue(), form.assigneeValue(), form.statusValue(),
                    form.versionValue());
            if (!updated) {
                // 先行更新との競合（または削除）。最新の内容を読み直して表示する
                if (dao.find(id, me.getGroupId()) == null) {
                    flashError(req, "タスクはすでに削除されています。");
                    resp.sendRedirect(listUrl);
                } else {
                    flashError(req, "他の人が先に更新したため、最新の内容を表示しました。内容を確認して、もう一度保存してください。");
                    resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskEdit.action?id=" + id);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            errors = new LinkedHashMap<>();
            errors.put("save", "保存に失敗しました。入力内容はそのままです。もう一度お試しください。");
            redisplay(req, resp, task, form, errors, List.of(), members);
            return;
        }

        if (!Objects.equals(form.assigneeValue(), task.getAssigneeId())) {
            TaskSupport.notifyAssigned(user, form.assigneeValue(), task);
        }
        flash(req, "タスクを更新しました。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskDetail.action?id=" + id);
    }

    private void redisplay(HttpServletRequest req, HttpServletResponse resp, Task task, TaskForm form,
            Map<String, String> errors, List<String> warnings, List<Member> members) throws Exception {
        req.setAttribute("task", task);
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        req.setAttribute("warnings", warnings);
        req.setAttribute("members", members);
        req.setAttribute("frequencies", Options.FREQUENCIES);
        req.setAttribute("statuses", Options.STATUSES);
        req.getRequestDispatcher("/careshift/main/task_edit.jsp").forward(req, resp);
    }
}
