package careshift;
import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import tool.Action;
/** 管理者ログイン。管理者パスワードはTomcat VM引数 careshift.admin.password で設定する。 */
public class AdminLoginAction extends Action {
 @Override public void execute(HttpServletRequest req,HttpServletResponse resp)throws Exception{
  if(!"POST".equalsIgnoreCase(req.getMethod())){resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);return;}
  String configured=System.getProperty("careshift.admin.password");String submitted=req.getParameter("password");
  if(configured==null||configured.isBlank()){req.setAttribute("error","管理者パスワードが未設定です。TomcatのVM引数に careshift.admin.password を設定してください。");req.getRequestDispatcher("/admin_login.jsp").forward(req,resp);return;}
  if(submitted==null||!java.security.MessageDigest.isEqual(configured.getBytes(java.nio.charset.StandardCharsets.UTF_8),submitted.getBytes(java.nio.charset.StandardCharsets.UTF_8))){req.setAttribute("error","パスワードが正しくありません。");req.getRequestDispatcher("/admin_login.jsp").forward(req,resp);return;}
  jakarta.servlet.http.HttpSession old=req.getSession(false);if(old!=null)old.invalidate();req.getSession(true).setAttribute("careshiftAdmin",Boolean.TRUE);resp.sendRedirect(req.getContextPath()+"/careshift/admin/AdminDashboard.action");
 }
}
