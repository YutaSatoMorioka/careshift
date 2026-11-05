package careshift.main;

import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/** S06 ホーム（メニュー）。URL: /careshift/main/Menu.action */
public class MenuAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user != null) {
            // グループ未所属ならホームで案内する
            req.setAttribute("member", new GroupDao().findMember(user.getId()));
        }
        req.getRequestDispatcher("/careshift/main/menu.jsp").forward(req, resp);
    }
}
