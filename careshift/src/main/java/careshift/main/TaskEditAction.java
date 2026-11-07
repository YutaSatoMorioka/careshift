package careshift.main;

import bean.Member;
import bean.Task;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/** S08-2 タスク編集（入力画面）。URL: /careshift/main/TaskEdit.action?id=... 編集できるのは登録者・担当者・主介護者。 */
public class TaskEditAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        long id = TaskSupport.parseId(req.getParameter("id"));
        Task task = new TaskDao().find(id, me.getGroupId());
        if (task == null) {
            flashError(req, "タスクが見つかりません。すでに削除されています。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskList.action");
            return;
        }
        if (!task.canEdit(user.getId(), me.isPrimary())) {
            flashError(req, "このタスクを編集できるのは、登録者・担当者・主介護者のみです。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskDetail.action?id=" + id);
            return;
        }
        req.setAttribute("task", task);
        req.setAttribute("form", TaskForm.fromTask(task));
        req.setAttribute("members", new GroupDao().findMembers(me.getGroupId()));
        req.setAttribute("frequencies", Options.FREQUENCIES);
        req.setAttribute("statuses", Options.STATUSES);
        req.getRequestDispatcher("/careshift/main/task_edit.jsp").forward(req, resp);
    }
}
