package careshift.main;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import bean.Article;
import bean.User;
import dao.ChecklistDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.ColumnCatalog;
import tool.Validator;

/** S18 チェックリストの保存（自分だけ）。URL: /careshift/main/ChecklistSave.action（POST） */
public class ChecklistSaveAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;

        String back = req.getContextPath() + "/careshift/main/Column.action";
        Article article = ColumnCatalog.find(Validator.trim(req.getParameter("articleId")));
        if (article == null) {
            flashError(req, "記事が見つかりません。");
            resp.sendRedirect(back);
            return;
        }

        // 記事にある項目の番号だけを受け付ける
        Set<Integer> checked = new LinkedHashSet<>();
        String[] values = req.getParameterValues("check");
        if (values != null) {
            for (String v : values) {
                String t = Validator.trim(v);
                if (t.matches("\\d{1,3}") && Integer.parseInt(t) < article.getChecklist().size()) {
                    checked.add(Integer.parseInt(t));
                }
            }
        }

        String target = back + "?id=" + article.getId();
        String category = Validator.trim(req.getParameter("category"));
        if (ColumnCatalog.isCategory(category)) {
            target += "&category=" + URLEncoder.encode(category, StandardCharsets.UTF_8);
        }
        try {
            new ChecklistDao().save(user.getId(), article.getId(), checked);
            flash(req, "チェックを保存しました（あなただけに保存され、家族には共有されません）。");
        } catch (Exception e) {
            e.printStackTrace();
            flashError(req, "保存に失敗しました。もう一度お試しください。");
        }
        resp.sendRedirect(target);
    }
}
