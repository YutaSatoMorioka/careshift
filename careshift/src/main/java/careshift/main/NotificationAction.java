package careshift.main;
import bean.Member;import bean.User;import dao.CareFeatureDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;
public class NotificationAction extends FeatureActionSupport {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{Member me=requireMember(req,resp);if(me==null)return;User user=loginUser(req);CareFeatureDao dao=new CareFeatureDao();if("POST".equalsIgnoreCase(req.getMethod())){dao.markNotificationsRead(user.getId());redirect(req,resp,"Notification");return;}req.setAttribute("notifications",dao.notifications(user.getId()));req.getRequestDispatcher("/careshift/main/notifications.jsp").forward(req,resp);}
}
