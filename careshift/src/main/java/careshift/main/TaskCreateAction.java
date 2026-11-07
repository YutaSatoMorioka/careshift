package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/** S08 タスク登録（入力画面）。URL: /careshift/main/TaskCreate.action */
public class TaskCreateAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        req.setAttribute("form", TaskForm.blank());
        req.setAttribute("members", new GroupDao().findMembers(me.getGroupId()));
        req.setAttribute("frequencies", Options.FREQUENCIES);
        req.getRequestDispatcher("/careshift/main/task_create.jsp").forward(req, resp);
    }
}
