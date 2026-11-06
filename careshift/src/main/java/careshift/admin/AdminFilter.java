package careshift.admin;

import jakarta.servlet.Filter;import jakarta.servlet.FilterChain;import jakarta.servlet.ServletException;import jakarta.servlet.ServletRequest;import jakarta.servlet.ServletResponse;import jakarta.servlet.annotation.WebFilter;import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpServletResponse;import jakarta.servlet.http.HttpSession;import java.io.IOException;
@WebFilter("/careshift/admin/*")
public class AdminFilter implements Filter {
 @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
  HttpServletRequest req=(HttpServletRequest)request;HttpServletResponse resp=(HttpServletResponse)response;HttpSession s=req.getSession(false);
  if(s!=null&&Boolean.TRUE.equals(s.getAttribute("careshiftAdmin"))){chain.doFilter(request,response);return;}
  resp.sendRedirect(req.getContextPath()+"/admin_login.jsp");
 }
}
