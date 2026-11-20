package careshift.main;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.AiDao;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AppLog;
import tool.DraftGenerator;
import tool.DraftGenerators;
import tool.PlanCatalog;
import tool.Validator;

/**
 * AI001 下書き生成。
 *
 * URL:
 * /careshift/main/AiDraftGenerate.action
 *
 * ・相談内容は必須、1000文字以内
 * ・相談内容はDBやログへ保存しない
 * ・利用回数はグループ、月ごとに上限を管理する
 * ・生成に失敗した場合は、確保した利用回数を戻す
 */
public class AiDraftGenerateAction extends Action {

    @Override
    public void execute(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws Exception {

        User user =
                requireLogin(
                        req,
                        resp
                );

        if (user == null) {
            return;
        }

        Member me =
                requireMember(
                        req,
                        resp,
                        user
                );

        if (me == null) {
            return;
        }

        String prompt =
                Validator.trim(
                        req.getParameter("prompt")
                );

        /*
         * 入力内容を画面へ戻すためのリクエスト属性。
         * DB、セッション、ログには保存しない。
         */
        req.setAttribute(
                "prompt",
                prompt
        );

        Map<String, String> errors =
                new LinkedHashMap<>();

        if (prompt.isEmpty()) {

            errors.put(
                    "prompt",
                    "相談内容を入力してください"
            );

        } else if (prompt.length() > 1000) {

            errors.put(
                    "prompt",
                    "相談内容は1000文字以内で入力してください"
            );
        }

        if (!errors.isEmpty()) {

            req.setAttribute(
                    "errors",
                    errors
            );

            AiDraftAction.show(
                    req,
                    resp,
                    me
            );

            return;
        }

        GroupPlan plan =
                new PlanDao().find(
                        me.getGroupId()
                );

        int limit =
                PlanCatalog.aiLimit(
                        plan.isPaid()
                );

        String yearMonth =
                YearMonth.now().toString();

        AiDao aiDao =
                new AiDao();

        boolean consumed =
                aiDao.tryConsume(
                        me.getGroupId(),
                        yearMonth,
                        limit
                );

        if (!consumed) {

            String message =
                    "今月のAI下書きの利用回数（"
                    + limit
                    + "回）に達しました。";

            if (plan.isPaid()) {

                message +=
                        "来月までお待ちいただくか、"
                        + "手入力で依頼を作成してください。";

            } else {

                message +=
                        "有料プランにすると回数が増えます。"
                        + "手入力で依頼を作成することもできます。";
            }

            errors.put(
                    "prompt",
                    message
            );

            req.setAttribute(
                    "errors",
                    errors
            );

            req.setAttribute(
                    "limitReached",
                    Boolean.TRUE
            );

            AiDraftAction.show(
                    req,
                    resp,
                    me
            );

            return;
        }

        try {
            DraftGenerator generator =
                    DraftGenerators.get();

            DraftGenerator.Draft draft =
                    generator.generate(prompt);

            req.setAttribute(
                    "hasDraft",
                    Boolean.TRUE
            );

            req.setAttribute(
                    "generated",
                    Boolean.TRUE
            );

            req.setAttribute(
                    "draftContent",
                    draft.content
            );

            req.setAttribute(
                    "draftRequestedAt",
                    draft.requestedAt
            );

            req.setAttribute(
                    "draftDuration",
                    draft.durationMin
            );

        } catch (DraftGenerator.UnavailableException e) {

            /*
             * AI生成に失敗したため、先に確保した利用回数を戻す。
             */
            try {
                aiDao.refund(
                        me.getGroupId(),
                        yearMonth
                );

            } catch (Exception refundException) {

                AppLog.write(
                        "ERROR",
                        "AI下書き生成失敗後の利用回数返却に失敗しました"
                        + "（"
                        + refundException
                                .getClass()
                                .getSimpleName()
                        + "）"
                );
            }

            /*
             * APIキーや相談内容を含まない安全な理由だけを記録する。
             */
            String reason =
                    safeReason(
                            e.getMessage()
                    );

            if (reason.isEmpty()) {
                reason =
                        "AIサービスから詳細な理由を取得できませんでした";
            }

            AppLog.write(
                    "ERROR",
                    "AI下書きを生成できませんでした"
                    + "（"
                    + reason
                    + "）"
            );

            req.setAttribute(
                    "unavailable",
                    Boolean.TRUE
            );

            req.setAttribute(
                    "unavailableReason",
                    reason
            );
        }

        AiDraftAction.show(
                req,
                resp,
                me
        );
    }

    /**
     * ログや画面へ表示できる安全な短い理由にする。
     */
    private static String safeReason(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return "";
        }

        String sanitized =
                value.replaceAll(
                        "[\\r\\n\\t]+",
                        " "
                ).replaceAll(
                        "\\s{2,}",
                        " "
                ).trim();

        if (sanitized.length() > 200) {
            return sanitized.substring(
                    0,
                    200
            );
        }

        return sanitized;
    }
}