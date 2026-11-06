package careshift;
import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import jakarta.servlet.http.HttpSession;import tool.Action;
public class AdminLogoutAction extends Action {@Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{HttpSession s=req.getSession(false);if(s!=null)s.removeAttribute("careshiftAdmin");resp.sendRedirect(req.getContextPath()+"/admin_login.jsp");}}
