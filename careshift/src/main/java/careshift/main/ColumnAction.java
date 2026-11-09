package careshift.main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bean.Article;
import bean.User;
import dao.ChecklistDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.ColumnCatalog;
import tool.Validator;

/**
 * S18 コラム・チェックリスト。URL: /careshift/main/Column.action?category=休息&id=rest-01
 * カテゴリ別の固定コラムを表示する。記事がなければ「準備中」。チェック状態は本人だけに保存し、家族とは共有しない。
 */
public class ColumnAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;

        String category = Validator.trim(req.getParameter("category"));
        if (!ColumnCatalog.isCategory(category)) {
            category = ""; // 不正な値・未指定は「すべて」
        }
        List<Article> articles = ColumnCatalog.byCategory(category);

        Article article = ColumnCatalog.find(Validator.trim(req.getParameter("id")));
        Set<Integer> checked = new HashSet<>();
        boolean checklistError = false;
        if (article != null) {
            try {
                checked = new ChecklistDao().load(user.getId(), article.getId());
            } catch (Exception e) {
                e.printStackTrace();
                checklistError = true; // 記事は表示し、チェック状態だけ再試行を案内する
            }
        }

        req.setAttribute("categories", ColumnCatalog.CATEGORIES);
        req.setAttribute("category", category);
        req.setAttribute("articles", articles);
        req.setAttribute("article", article);
        req.setAttribute("checked", checked);
        req.setAttribute("checklistError", checklistError);
        req.getRequestDispatcher("/careshift/main/column.jsp").forward(req, resp);
    }
}
