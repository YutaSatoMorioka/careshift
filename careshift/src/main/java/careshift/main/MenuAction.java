package careshift.main;

import java.time.LocalDate;

import bean.User;
import dao.CareFeatureDao;
import dao.GroupDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;

public class MenuAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = loginUser(req);

        if (user != null) {
            var member = new GroupDao().findMember(user.getId());
            req.setAttribute("member", member);

            if (member != null) {
                CareFeatureDao features = new CareFeatureDao();
                LocalDate today = LocalDate.now();

                req.setAttribute("todayTasks",
                        features.tasks(member.getGroupId(),
                                today.atStartOfDay(),
                                today.plusDays(1).atStartOfDay()));
                req.setAttribute("openRequests", features.requests(member.getGroupId()));
                req.setAttribute("rests", features.rests(member.getGroupId()));
            }
        }

        req.getRequestDispatcher("/careshift/main/menu.jsp").forward(req, resp);
    }
}