package careshift.main;

import java.time.YearMonth;

import bean.GroupPlan;
import bean.Member;
import bean.User;
import dao.AiDao;
import dao.PlanDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.PlanCatalog;

/**
 * AI001 AI依頼下書き。URL: /careshift/main/AiDraft.action
 * 相談文から、依頼の下書き（日時・内容・所要時間）を作る。AIは下書きだけを作り、医療判断や責任分担は決めない。
 * 保存した下書きがあれば、続きから修正できる。
 */
public class AiDraftAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        AiDao.Draft saved = new AiDao().findDraft(user.getId());
        if (saved != null) {
            req.setAttribute("hasDraft", Boolean.TRUE);
            req.setAttribute("draftContent", saved.content);
            req.setAttribute("draftRequestedAt", saved.requestedAt);
            req.setAttribute("draftDuration", saved.durationMin);
        }
        show(req, resp, me);
    }

    /** 画面を表示する（生成の結果・エラーの再表示でも使う）。 */
    static void show(HttpServletRequest req, HttpServletResponse resp, Member me) throws Exception {
        GroupPlan plan = new PlanDao().find(me.getGroupId());
        int limit = PlanCatalog.aiLimit(plan.isPaid());
        int used = new AiDao().used(me.getGroupId(), YearMonth.now().toString());
        req.setAttribute("aiLimit", limit);
        req.setAttribute("aiUsed", used);
        req.setAttribute("aiRemaining", Math.max(0, limit - used));
        req.setAttribute("paid", plan.isPaid());
        req.getRequestDispatcher("/careshift/main/ai_draft.jsp").forward(req, resp);
    }
}
