package tool;

import java.io.IOException;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * すべての「〇〇Action」の親クラス。
 * 画面ごとに Action を継承したクラスを作り、execute に処理を書く。
 */
public abstract class Action {

    public abstract void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;

    /**
     * 結果メッセージ画面（result.jsp）を表示する。
     * linkPath は「/login.jsp」のようにコンテキストパスを除いた先頭 / 付きの道筋で渡す。
     */
    protected void showResult(HttpServletRequest req, HttpServletResponse resp, String title, String message,
            String linkPath, String linkLabel) throws Exception {
        req.setAttribute("resultTitle", title);
        req.setAttribute("resultMessage", message);
        req.setAttribute("resultLinkPath", linkPath);
        req.setAttribute("resultLinkLabel", linkLabel);
        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }

    /** エラー画面（ERROR01 / error.jsp）を表示する。機微情報はメッセージに含めないこと。 */
    protected void showError(HttpServletRequest req, HttpServletResponse resp, String message) throws Exception {
        req.setAttribute("errorMessage", message);
        req.getRequestDispatcher("/error.jsp").forward(req, resp);
    }

    /** ログイン中のユーザー。ログインしていなければ null。 */
    protected User loginUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    /** 次に表示する画面の上部に出す「成功」メッセージ（common/header.jsp が表示して消す）。 */
    protected void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }

    /** 次に表示する画面の上部に出す「エラー」メッセージ。 */
    protected void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    /** ログイン中のユーザーを返す。未ログインならログイン画面へ移動して null を返す。 */
    protected User requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
        return user;
    }

    /** 所属グループでのメンバー情報を返す。未所属ならグループ画面へ移動して null を返す。 */
    protected Member requireMember(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        Member me = new GroupDao().findMember(user.getId());
        if (me == null) {
            flashError(req, "先に家族グループを作成するか、招待リンクから参加してください。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/Group.action");
        }
        return me;
    }
}
