package careshift.main;

import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bean.Member;

public class TaskFormAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        Member me=requireMember(req,resp);if(me==null)return;
        CareFeatureDao dao=new CareFeatureDao();long taskId=id(req.getParameter("id"));
        if(taskId>0){var task=dao.task(me.getGroupId(),taskId);if(task==null){fail(req,resp,"タスクが見つかりません。","TaskList");return;}req.setAttribute("task",task);Object due=task.get("due_at");if(due instanceof java.sql.Timestamp ts)req.setAttribute("dueAtValue",ts.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));}
        req.setAttribute("members",dao.members(me.getGroupId()));req.getRequestDispatcher("/careshift/main/task_form.jsp").forward(req,resp);
    }
}
