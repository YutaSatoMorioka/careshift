package careshift.main;

import bean.Member;
import bean.Task;
import bean.User;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S08-2 / S09 タスクの削除。URL: /careshift/main/TaskDelete.action（POST）
 * 画面側で確認ダイアログを出し、担当者にも通知する。すでに削除済みなら一覧へ戻る。
 */
public class TaskDeleteAction extends Action {

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
            flashError(req, "タスクはすでに削除されています。");
            resp.sendRedirect(listUrl);
            return;
        }
        if (!task.canEdit(user.getId(), me.isPrimary())) {
            flashError(req, "このタスクを削除できるのは、登録者・担当者・主介護者のみです。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskDetail.action?id=" + id);
            return;
        }
        if (dao.delete(id, me.getGroupId())) {
            TaskSupport.notifyDeleted(user, task);
            flash(req, "タスク「" + task.getTitle() + "」を削除しました。");
        } else {
            flashError(req, "タスクはすでに削除されています。");
        }
        resp.sendRedirect(listUrl);
    }
}
