package careshift.main;

import java.util.List;

import bean.FamilyGroup;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

/**
 * S19 タスク分担表（プレビュー）。URL: /careshift/main/TaskSheet.action?period=week&assignee=&order=assignee
 * 期間（今週／今月／全て）・担当者・並び順を指定して、タスク・担当者・期限の表を表示する。
 * そこから PDF出力・印刷ができる。0件・200件超は出力できない。
 */
public class TaskSheetAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        TaskSheet sheet = TaskSheet.build(new TaskDao(), me.getGroupId(), req.getParameter("period"),
                req.getParameter("assignee"), req.getParameter("order"), members);
        FamilyGroup group = new GroupDao().findGroup(me.getGroupId());

        req.setAttribute("sheet", sheet);
        req.setAttribute("members", members);
        req.setAttribute("group", group);
        req.getRequestDispatcher("/careshift/main/task_sheet.jsp").forward(req, resp);
    }
}
