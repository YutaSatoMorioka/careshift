package careshift.main;
import bean.Member;import dao.CareFeatureDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;
public class PlanAction extends FeatureActionSupport {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{Member me=requireMember(req,resp);if(me==null)return;req.setAttribute("plan",new CareFeatureDao().plan(me.getGroupId()));req.getRequestDispatcher("/careshift/main/plan.jsp").forward(req,resp);}
}
