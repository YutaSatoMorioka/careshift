package careshift.main;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
import tool.Validator;

/**
 * S07 タスク一覧。URL: /careshift/main/TaskList.action
 * キーワード・担当者・期限（この日まで）・状態で絞り込む。担当なしも識別する。
 */
public class TaskListAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String keyword = Validator.trim(req.getParameter("keyword"));
        if (keyword.length() > 60) {
            keyword = keyword.substring(0, 60);
        }
        String assignee = Validator.trim(req.getParameter("assignee"));
        String status = Validator.trim(req.getParameter("status"));
        if (!Options.STATUSES.containsKey(status)) {
            status = "";
        }

        Map<String, String> errors = new LinkedHashMap<>();
        String dueDateText = Validator.trim(req.getParameter("dueDate"));
        LocalDate dueTo = null;
        if (!dueDateText.isEmpty()) {
            try {
                dueTo = LocalDate.parse(dueDateText);
            } catch (DateTimeParseException e) {
                errors.put("dueDate", "期限は日付の形式で入力してください");
            }
        }

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        List<Task> tasks = new TaskDao().search(me.getGroupId(), keyword, assignee, dueTo, status);

        req.setAttribute("tasks", tasks);
        req.setAttribute("members", members);
        req.setAttribute("statuses", Options.STATUSES);
        req.setAttribute("keyword", keyword);
        req.setAttribute("assignee", assignee);
        req.setAttribute("assigneeId", assignee.matches("\\d{1,18}") ? Long.valueOf(assignee) : null);
        req.setAttribute("dueDate", dueDateText);
        req.setAttribute("status", status);
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/careshift/main/task_list.jsp").forward(req, resp);
    }
}
