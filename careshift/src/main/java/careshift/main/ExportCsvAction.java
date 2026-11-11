package careshift.main;

import java.time.LocalDate;
import java.util.List;

import bean.Member;
import bean.Task;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.Options;

/**
 * S20 データを出力（家族・タスクのCSV）。URL: /careshift/main/ExportCsv.action（主介護者のみ）
 * 失敗したときは、設定画面に戻して再試行できるようにする。
 */
public class ExportCsvAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        String back = req.getContextPath() + "/careshift/main/Settings.action";
        if (!me.isPrimary()) {
            flashError(req, "データを出力できるのは主介護者のみです。");
            resp.sendRedirect(back);
            return;
        }

        byte[] csv;
        try {
            csv = build(me);
        } catch (Exception e) {
            e.printStackTrace();
            flashError(req, "CSVの出力に失敗しました。もう一度お試しください。");
            resp.sendRedirect(back);
            return;
        }
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"careshift-" + LocalDate.now() + ".csv\"");
        resp.setContentLength(csv.length);
        resp.getOutputStream().write(csv);
    }

    private byte[] build(Member me) throws Exception {
        CsvBuilder csv = new CsvBuilder();

        csv.row("［家族］");
        csv.row("表示名", "続柄", "役割", "連絡先");
        for (Member m : new GroupDao().findMembers(me.getGroupId())) {
            csv.row(m.getDisplayName(), m.getRelationship(), m.getRoleLabel(), m.getContact());
        }

        csv.endRow();
        csv.row("［タスク］");
        csv.row("ID", "タスク内容", "担当者", "期限", "所要時間（分）", "頻度", "状態", "登録者");
        List<Task> tasks = new TaskDao().search(me.getGroupId(), "", null, null, "");
        for (Task t : tasks) {
            csv.row(String.valueOf(t.getId()), t.getTitle(), t.getAssigneeName() == null ? "担当なし" : t.getAssigneeName(),
                    t.getDueDate().toString(), String.valueOf(t.getDurationMin()),
                    Options.FREQUENCIES.getOrDefault(t.getFrequency(), t.getFrequency()), t.getStatusLabel(),
                    t.getCreatorName());
        }
        return csv.bytes();
    }
}
