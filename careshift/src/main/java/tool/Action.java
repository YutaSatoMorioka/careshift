package tool;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
}
