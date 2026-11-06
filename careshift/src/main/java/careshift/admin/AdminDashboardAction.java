package careshift.admin;
import dao.CareFeatureDao;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import tool.Action;import tool.BackupService;
public class AdminDashboardAction extends Action {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{req.setAttribute("logs",new CareFeatureDao().recentLogs(500));req.setAttribute("backups",BackupService.list());req.getRequestDispatcher("/careshift/admin/dashboard.jsp").forward(req,resp);}
}
