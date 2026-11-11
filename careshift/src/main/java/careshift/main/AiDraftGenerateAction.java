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
 * AI001 下書き生成（ai_draft.jsp の generate ボタンの送信先）。URL: /careshift/main/AiDraftGenerate.action
 *  - 相談内容は必須・1000文字以内。相談文は保存せず、ログにも残さない
 *  - 利用回数は、グループ・月ごとにプランで上限がある（失敗した分は数えない）
 *  - 生成できないとき（AI未設定・通信失敗など）は、手入力で依頼を作る案内を出す
 */
public class AiDraftGenerateAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String prompt = Validator.trim(req.getParameter("prompt"));
        req.setAttribute("prompt", prompt); // 入力は保持する（保存はしない）

        Map<String, String> errors = new LinkedHashMap<>();
        if (prompt.isEmpty()) {
            errors.put("prompt", "相談内容を入力してください");
        } else if (prompt.length() > 1000) {
            errors.put("prompt", "相談内容は1000文字以内で入力してください");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            AiDraftAction.show(req, resp, me);
            return;
        }

        // 利用回数の上限（先に1回分を確保し、失敗したら戻す）
        GroupPlan plan = new PlanDao().find(me.getGroupId());
        int limit = PlanCatalog.aiLimit(plan.isPaid());
        String ym = YearMonth.now().toString();
        AiDao ai = new AiDao();
        if (!ai.tryConsume(me.getGroupId(), ym, limit)) {
            errors.put("prompt", "今月のAI下書きの利用回数（" + limit + "回）に達しました。"
                    + (plan.isPaid() ? "来月までお待ちいただくか、手入力で依頼を作成してください。"
                    : "有料プランにすると回数が増えます。手入力で依頼を作成することもできます。"));
            req.setAttribute("errors", errors);
            req.setAttribute("limitReached", Boolean.TRUE);
            AiDraftAction.show(req, resp, me);
            return;
        }

        try {
            DraftGenerator.Draft draft = DraftGenerators.get().generate(prompt);
            req.setAttribute("hasDraft", Boolean.TRUE);
            req.setAttribute("generated", Boolean.TRUE);
            req.setAttribute("draftContent", draft.content);
            req.setAttribute("draftRequestedAt", draft.requestedAt);
            req.setAttribute("draftDuration", draft.durationMin);
        } catch (DraftGenerator.UnavailableException e) {
            ai.refund(me.getGroupId(), ym); // 生成できなかった分は数えない
            AppLog.write("ERROR", "AI下書きを生成できませんでした"); // 理由の詳細・相談文は記録しない
            req.setAttribute("unavailable", Boolean.TRUE);
        }
        AiDraftAction.show(req, resp, me);
    }
}
