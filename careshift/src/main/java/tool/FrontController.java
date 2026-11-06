package tool;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 「〇〇.action」で終わるURLをすべて受け取り、対応する Action クラスを呼び出す。
 *
 *   URL:   /careshift/main/Menu.action
 *   クラス: careshift.main.MenuAction
 *
 * 画面を増やすときは Action クラスを作るだけでよい（Servlet の登録は不要）。
 */
@WebServlet(urlPatterns = { "*.action" })
public class FrontController extends HttpServlet {

    /** この配下のクラスしか実行しない（URLから任意のクラスを動かされないようにする）。 */
    private static final String BASE_PACKAGE = "careshift.";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handle(req, resp);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // "/careshift/main/Menu.action" → "careshift.main.MenuAction"
        String path = req.getServletPath().substring(1);
        String className = path.replace(".action", "Action").replace('/', '.');

        if ("POST".equalsIgnoreCase(req.getMethod()) && !"careshift.AdminLoginAction".equals(className)) {
            jakarta.servlet.http.HttpSession session = req.getSession(false);
            String expected = session == null ? null : (String) session.getAttribute("csrfToken");
            String supplied = req.getParameter("_csrf");
            if (expected == null || supplied == null || !java.security.MessageDigest.isEqual(
                    expected.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    supplied.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "フォームの有効期限が切れました。画面を再読み込みしてください。");
                return;
            }
        }

        try {
            if (!className.startsWith(BASE_PACKAGE)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Class<?> clazz = Class.forName(className);
            if (!Action.class.isAssignableFrom(clazz)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            Action action = (Action) clazz.getDeclaredConstructor().newInstance();
            action.execute(req, resp);
        } catch (ClassNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            getServletContext().log("Care Shift action failed: " + req.getServletPath(), e);
            try {
                new dao.CareFeatureDao().log("ERROR", req.getServletPath(), e.getClass().getSimpleName());
            } catch (Exception loggingFailure) {
                getServletContext().log("Care Shift operation log could not be saved.", loggingFailure);
            }
            req.setAttribute("errorMessage", "処理中に問題が発生しました。時間をおいて、もう一度お試しください。");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
