package careshift.main;

import java.util.LinkedHashMap;
import java.util.Map;

import bean.FamilyGroup;
import bean.Member;
import bean.User;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import tool.Action;
import tool.Options;

/**
 * S05 家族グループ作成・管理。URL: /careshift/main/Group.action
 * グループ未所属なら作成フォーム、所属済みなら名称変更（主介護者のみ）と招待リンク発行を表示する。
 */
public class GroupAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        show(req, resp, user, new LinkedHashMap<>(), null);
    }

    /** 画面を表示する（GroupSaveAction から、エラー付きで再表示するときにも使う）。 */
    static void show(HttpServletRequest req, HttpServletResponse resp, User user, Map<String, String> errors,
            String groupNameInput) throws Exception {
        GroupDao dao = new GroupDao();
        Member me = dao.findMember(user.getId());
        if (me != null) {
            FamilyGroup group = dao.findGroup(me.getGroupId());
            req.setAttribute("member", me);
            req.setAttribute("group", group);
            req.setAttribute("memberCount", dao.countMembers(me.getGroupId()));
            req.setAttribute("maxMembers", GroupDao.MAX_MEMBERS);
            req.setAttribute("groupName", groupNameInput != null ? groupNameInput : group.getName());
        } else {
            req.setAttribute("groupName", groupNameInput == null ? "" : groupNameInput);
        }
        req.setAttribute("relationships", Options.RELATIONSHIPS);
        req.setAttribute("errors", errors);

        // 招待リンクは発行直後の1回だけ表示する（DBにはハッシュしか残らないため再表示できない）
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("inviteLink") != null) {
            req.setAttribute("inviteLink", session.getAttribute("inviteLink"));
            session.removeAttribute("inviteLink");
        }
        req.getRequestDispatcher("/careshift/main/group.jsp").forward(req, resp);
    }
}
