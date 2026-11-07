package tool;

import java.io.IOException;

import bean.User;
import dao.NotificationDao;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * /careshift/main/ 配下（ログイン後の画面）に、ログインしていない人が URL を直接打って入るのを防ぐ。
 * JSP を WEB-INF の外に置く構成では、この Filter が必須。
 */
@WebFilter("/careshift/main/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // ヘッダの「通知（未読件数）」用。取得に失敗しても画面は表示する
            try {
                req.setAttribute("unreadCount",
                        new NotificationDao().countUnread(((User) session.getAttribute("user")).getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}
