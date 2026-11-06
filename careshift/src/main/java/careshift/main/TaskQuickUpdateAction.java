package careshift.main;

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
import tool.Validator;

/**
 * S09 担当者・進捗の更新（task_detail.jsp の送信先）。URL: /careshift/main/TaskQuickUpdate.action
 * 権限がなければ、画面に表示されない操作を直接送られても拒否する。競合時は最新内容を再表示する。
 */
public class TaskQuickUpdateAction extends Action {

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
        String detailUrl = req.getContextPath() + "/careshift/main/TaskDetail.action?id=" + id;
        if (!task.canEdit(user.getId(), me.isPrimary())) {
            flashError(req, "このタスクを更新できるのは、登録者・担当者・主介護者のみです。");
            resp.sendRedirect(detailUrl);
            return;
        }

        String assigneeText = Validator.trim(req.getParameter("assignee"));
        String status = Validator.trim(req.getParameter("status"));
        Long assignee = null;
        if (!assigneeText.isEmpty()) {
            boolean ok = assigneeText.matches("\\d{1,18}")
                    && TaskSupport.memberIds(new GroupDao().findMembers(me.getGroupId()))
                            .contains(Long.parseLong(assigneeText));
            if (!ok) {
                flashError(req, "担当者はグループのメンバーから選んでください。");
                resp.sendRedirect(detailUrl);
                return;
            }
            assignee = Long.parseLong(assigneeText);
        }
        if (!Options.STATUSES.containsKey(status)) {
            flashError(req, "進捗を選択してください。");
            resp.sendRedirect(detailUrl);
            return;
        }

        String versionText = Validator.trim(req.getParameter("version"));
        int version = versionText.matches("\\d{1,9}") ? Integer.parseInt(versionText) : -1;
        if (!dao.updateAssigneeStatus(id, me.getGroupId(), assignee, status, version)) {
            if (dao.find(id, me.getGroupId()) == null) {
                flashError(req, "タスクはすでに削除されています。");
                resp.sendRedirect(listUrl);
            } else {
                flashError(req, "他の人が先に更新したため、最新の内容を表示しました。内容を確認して、もう一度操作してください。");
                resp.sendRedirect(detailUrl);
            }
            return;
        }

        if (!Objects.equals(assignee, task.getAssigneeId())) {
            TaskSupport.notifyAssigned(user, assignee, task);
        }
        flash(req, "タスクを更新しました。");
        resp.sendRedirect(detailUrl);
    }
}
