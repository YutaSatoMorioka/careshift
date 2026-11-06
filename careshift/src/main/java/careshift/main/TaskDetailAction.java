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

/**
 * S09 タスク詳細。URL: /careshift/main/TaskDetail.action?id=...
 * 編集・削除・担当者／進捗の更新は、登録者・担当者・主介護者だけに表示する。
 */
public class TaskDetailAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        Task task = new TaskDao().find(TaskSupport.parseId(req.getParameter("id")), me.getGroupId());
        if (task == null) {
            flashError(req, "タスクが見つかりません。削除された可能性があります。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskList.action");
            return;
        }
        req.setAttribute("task", task);
        req.setAttribute("canEdit", task.canEdit(user.getId(), me.isPrimary()));
        req.setAttribute("members", new GroupDao().findMembers(me.getGroupId()));
        req.setAttribute("statuses", Options.STATUSES);
        req.getRequestDispatcher("/careshift/main/task_detail.jsp").forward(req, resp);
    }
}
