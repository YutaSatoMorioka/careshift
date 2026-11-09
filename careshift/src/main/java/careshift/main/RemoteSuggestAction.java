package careshift.main;

import bean.Member;
import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * AI002 遠方家族向け提案。URL: /careshift/main/RemoteSuggest.action?conditions=CALL
 * 電話・予約・ネット購入・情報収集など、遠方からできるタスクの候補を3〜5件表示する（固定候補）。
 * 利用者が選ぶまで登録せず、「タスクに追加」を押すと、内容を入れた状態でタスク登録画面（S08）へ移る。
 */
public class RemoteSuggestAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String condition = Validator.trim(req.getParameter("conditions"));
        if (!SuggestionCatalog.CONDITIONS.containsKey(condition)) {
            condition = ""; // 不正な値は「すべて」
        }
        req.setAttribute("conditionList", SuggestionCatalog.CONDITIONS);
        req.setAttribute("conditions", condition);
        req.setAttribute("suggestions", SuggestionCatalog.forCondition(condition));
        req.getRequestDispatcher("/careshift/main/remote_suggest.jsp").forward(req, resp);
    }
}
