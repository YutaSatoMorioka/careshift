package careshift.main;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bean.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.AdminSupport;
import tool.AppLog;
import tool.BackupManager;
import tool.Validator;

/**
 * S90 管理者。URL: /careshift/main/Admin.action?logFrom=2026-11-01&logTo=2026-11-05&logType=ERROR
 * バックアップ（履歴・手動実行・復元）と障害ログの閲覧。管理者以外は権限エラーを出してホームへ戻す。
 */
public class AdminAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        if (!requireAdmin(req, resp, user)) return;

        Map<String, String> errors = new LinkedHashMap<>();
        String from = Validator.trim(req.getParameter("logFrom"));
        String to = Validator.trim(req.getParameter("logTo"));
        String type = Validator.trim(req.getParameter("logType"));
        if (!AppLog.TYPES.contains(type)) type = "";

        LocalDate fromDate = parse(from, "logFrom", errors);
        LocalDate toDate = parse(to, "logTo", errors);
        if (errors.isEmpty() && fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            errors.put("logPeriod", "開始日は終了日以前にしてください");
        }

        List<AppLog.Entry> logs = new ArrayList<>();
        if (errors.isEmpty()) {
            logs = AppLog.read(fromDate, toDate, type, 200);
        }
        List<BackupManager.Record> history = BackupManager.recent(BackupManager.GENERATIONS);
        List<BackupManager.Record> restorable = new ArrayList<>();
        for (BackupManager.Record r : history) {
            if (r.isRestorable()) restorable.add(r);
        }

        req.setAttribute("supported", BackupManager.isSupported());
        req.setAttribute("history", history);
        req.setAttribute("restorable", restorable);
        req.setAttribute("logs", logs);
        req.setAttribute("logTypes", AppLog.TYPES);
        req.setAttribute("logFrom", from);
        req.setAttribute("logTo", to);
        req.setAttribute("logType", type);
        req.setAttribute("errors", errors);
        req.getRequestDispatcher("/careshift/main/admin.jsp").forward(req, resp);
    }

    /** 管理者でなければ、権限エラーを出してホームへ戻す。 */
    static boolean requireAdmin(HttpServletRequest req, HttpServletResponse resp, User user) throws Exception {
        if (AdminSupport.isAdmin(user)) return true;
        req.getSession().setAttribute("flashError", "この画面は管理者のみ利用できます（権限がありません）。");
        resp.sendRedirect(req.getContextPath() + "/careshift/main/Menu.action");
        return false;
    }

    private static LocalDate parse(String s, String key, Map<String, String> errors) {
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            errors.put(key, "日付の形式で入力してください");
            return null;
        }
    }
}
