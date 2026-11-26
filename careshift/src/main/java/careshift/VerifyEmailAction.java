package careshift;

import bean.EmailToken;
import dao.TokenDao;
import dao.TokenDao.TokenProcessResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AppLog;
import tool.TokenUtil;
import tool.Validator;

/**
 * S02 本登録リンク。
 *
 * URL:
 * /careshift/VerifyEmail.action?token=...
 *
 * 有効な確認リンクを使用すると、
 * メールアドレス確認済み状態へ変更する。
 *
 * メール確認済み化とトークン使用済み化は、
 * TokenDaoの同一トランザクション内で実行する。
 */
public class VerifyEmailAction extends Action {

    private static final String TOKEN_PATTERN =
            "[A-Za-z0-9_-]{20,100}";

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        String token =
                Validator.trim(
                        req.getParameter("token")
                );

        if (!token.matches(
                TOKEN_PATTERN
        )) {
            showInvalidLink(
                    req,
                    resp
            );

            return;
        }

        TokenProcessResult result;

        try {
            result =
                    new TokenDao()
                            .verifyEmail(
                                    TokenUtil.hash(token),
                                    EmailToken.VERIFY
                            );

        } catch (Exception e) {
            AppLog.write(
                    "ERROR",
                    "メール確認処理に失敗しました"
                    + "（"
                    + e.getClass()
                            .getSimpleName()
                    + "）"
            );

            showError(
                    req,
                    resp,
                    "メールアドレスの確認処理を完了できませんでした。"
                    + "時間をおいて、メール内のリンクをもう一度開いてください。"
            );

            return;
        }

        switch (result) {

        case SUCCESS:

            AppLog.write(
                    "AUTH",
                    "メールアドレスの確認が完了しました"
            );

            showResult(
                    req,
                    resp,
                    "本登録が完了しました",
                    "ご登録ありがとうございます。"
                    + "ログインしてCare Shiftをご利用ください。",
                    "/login.jsp",
                    "ログインへ"
            );

            return;

        case EXPIRED:

            showResult(
                    req,
                    resp,
                    "リンクの有効期限が切れています",
                    "確認リンクの有効期限（24時間）が切れました。"
                    + "ログイン画面でログインすると、"
                    + "確認メールを再送します。",
                    "/login.jsp",
                    "ログインへ"
            );

            return;

        case USED:

            showResult(
                    req,
                    resp,
                    "このリンクは使用できません",
                    "すでに本登録が完了しているか、"
                    + "新しい確認メールが送られて無効になっています。"
                    + "ログインをお試しください。",
                    "/login.jsp",
                    "ログインへ"
            );

            return;

        case USER_NOT_FOUND:
        case NOT_FOUND:
        default:

            showInvalidLink(
                    req,
                    resp
            );

            return;
        }
    }

    /**
     * 不正なリンクに対する共通案内。
     */
    private void showInvalidLink(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        showResult(
                req,
                resp,
                "リンクが正しくありません",
                "確認用のリンクが正しくありません。"
                + "メールに記載されたリンクをそのまま開いてください。",
                "/login.jsp",
                "ログインへ"
        );
    }
}