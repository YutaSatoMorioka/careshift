package careshift.main;

import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/** S16 家族メンバー（一覧と編集）。URL: /careshift/main/MemberList.action */
public class MemberListAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        GroupDao dao = new GroupDao();
        Member me = dao.findMember(user.getId());
        if (me == null) {
            flashError(req, "家族グループにまだ所属していません。グループを作成するか、招待リンクから参加してください。");
            resp.sendRedirect(req.getContextPath() + "/careshift/main/Group.action");
            return;
        }
        req.setAttribute("member", me); // 閲覧している本人
        req.setAttribute("group", dao.findGroup(me.getGroupId()));
        req.setAttribute("members", dao.findMembers(me.getGroupId()));
        req.setAttribute("maxMembers", GroupDao.MAX_MEMBERS);
        req.setAttribute("relationships", Options.RELATIONSHIPS);
        req.getRequestDispatcher("/careshift/main/member_list.jsp").forward(req, resp);
    }
}
