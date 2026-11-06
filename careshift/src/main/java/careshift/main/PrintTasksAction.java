package careshift.main;
import bean.Member;import dao.CareFeatureDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;
public class PrintTasksAction extends FeatureActionSupport {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{Member me=requireMember(req,resp);if(me==null)return;req.setAttribute("tasks",new CareFeatureDao().tasks(me.getGroupId(),null,null));req.setAttribute("members",new CareFeatureDao().members(me.getGroupId()));req.getRequestDispatcher("/careshift/main/tasks_print.jsp").forward(req,resp);}
}
