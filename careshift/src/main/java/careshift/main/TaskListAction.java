package careshift.main;

import dao.CareFeatureDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bean.Member;

public class TaskListAction extends FeatureActionSupport {
    @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception {
        Member me=requireMember(req,resp);if(me==null)return;
        CareFeatureDao dao=new CareFeatureDao();req.setAttribute("tasks",dao.tasks(me.getGroupId(),null,null));req.setAttribute("members",dao.members(me.getGroupId()));
        req.getRequestDispatcher("/careshift/main/task_list.jsp").forward(req,resp);
    }
}
