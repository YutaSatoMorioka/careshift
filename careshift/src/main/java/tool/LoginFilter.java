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
 * ログイン後画面を保護するFilter。
 *
 * 対象:
 * /careshift/main/*
 *
 * 未ログインの場合はログイン画面へ移動する。
 *
 * ログイン済みの場合は、ブラウザーへ認証済み画面を
 * 保存しないようキャッシュ制御ヘッダーを付ける。
 */
@WebFilter("/careshift/main/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req =
                (HttpServletRequest) request;

        HttpServletResponse resp =
                (HttpServletResponse) response;

        HttpSession session =
                req.getSession(false);

        User user =
                session == null
                        ? null
                        : (User) session.getAttribute(
                                "user"
                        );

        if (user == null) {
            /*
             * 未ログイン状態のレスポンスもキャッシュさせない。
             */
            setNoStoreHeaders(resp);

            resp.sendRedirect(
                    req.getContextPath()
                    + "/login.jsp"
            );

            return;
        }

        /*
         * ホーム、タスク、依頼、メンバー情報などには
         * 家族情報が含まれるため、ブラウザーや中間キャッシュへ
         * 保存しないようにする。
         */
        setNoStoreHeaders(resp);

        /*
         * 共通ヘッダーの未読通知件数。
         *
         * 通知件数の取得に失敗しても、
         * 本来の画面表示は継続する。
         */
        try {
            int unreadCount =
                    new NotificationDao()
                            .countUnread(
                                    user.getId()
                            );

            req.setAttribute(
                    "unreadCount",
                    unreadCount
            );

        } catch (Exception e) {
            /*
             * 利用者情報や例外メッセージはログへ含めない。
             */
            AppLog.write(
                    "ERROR",
                    "共通ヘッダーの未読通知件数を取得できませんでした"
                    + "（"
                    + e.getClass().getSimpleName()
                    + "）"
            );
        }

        chain.doFilter(
                request,
                response
        );
    }

    /**
     * 認証済み画面をブラウザーやプロキシへ保存させない。
     *
     * Cache-Control:
     * HTTP/1.1向け。
     *
     * Pragma:
     * 古いHTTPクライアントとの互換用。
     *
     * Expires:
     * 即時期限切れとして扱う。
     */
    private void setNoStoreHeaders(
            HttpServletResponse resp
    ) {

        resp.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate, max-age=0, private"
        );

        resp.setHeader(
                "Pragma",
                "no-cache"
        );

        resp.setDateHeader(
                "Expires",
                0L
        );
    }
}