package careshift.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bean.Member;
import bean.Notification;
import bean.User;
import dao.NotificationDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Validator;

/**
 * NOTI001 通知一覧。URL: /careshift/main/NotificationList.action?type=REQUEST
 * 種別（依頼／回答／休息／期限）で絞り込み、未読を識別する。新しい順に最新100件。
 * 読み込みに失敗したら再試行を表示する。
 */
public class NotificationListAction extends Action {

    static final Map<String, String> TYPES = new LinkedHashMap<>();
    static {
        TYPES.put("", "すべて");
        TYPES.put("REQUEST", "依頼");
        TYPES.put("ANSWER", "回答");
        TYPES.put("REST", "休息");
        TYPES.put("DEADLINE", "期限");
        TYPES.put(NotificationDao.OTHER, "その他（家族・タスク）");
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        ReminderSupport.generate(user, me); // 期限リマインドを（なければ）作る

        String type = Validator.trim(req.getParameter("type"));
        if (!TYPES.containsKey(type)) {
            type = ""; // 不正な種別は「すべて」
        }
        List<Notification> list = new ArrayList<>();
        boolean loadError = false;
        try {
            NotificationDao dao = new NotificationDao();
            list = dao.find(user.getId(), type, 100);
            req.setAttribute("unreadCount", dao.countUnread(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            loadError = true;
        }
        req.setAttribute("types", TYPES);
        req.setAttribute("type", type);
        req.setAttribute("notifications", list);
        req.setAttribute("loadError", loadError);
        req.getRequestDispatcher("/careshift/main/notification_list.jsp").forward(req, resp);
    }
}
