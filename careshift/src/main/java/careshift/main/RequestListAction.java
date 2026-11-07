package careshift.main;

import java.util.ArrayList;
import java.util.List;

import bean.Member;
import bean.RequestCard;
import bean.User;
import dao.RequestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * S11 依頼一覧。URL: /careshift/main/RequestList.action?tab=received|sent
 * 送った依頼／受け取った依頼を切り替える（初期は受け取った依頼）。読み込みに失敗したら再試行を表示する。
 */
public class RequestListAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String tab = "sent".equals(Validator.trim(req.getParameter("tab"))) ? "sent" : "received";
        List<RequestCard> requests = new ArrayList<>();
        boolean loadError = false;
        try {
            RequestDao dao = new RequestDao();
            requests = "sent".equals(tab) ? dao.findSent(user.getId(), me.getGroupId())
                    : dao.findReceived(user.getId(), me.getGroupId());
        } catch (Exception e) {
            e.printStackTrace();
            loadError = true;
        }
        req.setAttribute("tab", tab);
        req.setAttribute("requests", requests);
        req.setAttribute("loadError", loadError);
        req.getRequestDispatcher("/careshift/main/request_list.jsp").forward(req, resp);
    }
}
