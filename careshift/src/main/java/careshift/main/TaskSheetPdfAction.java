package careshift.main;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import bean.FamilyGroup;
import bean.Member;
import bean.User;
import dao.GroupDao;
import dao.TaskDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.SimplePdf;

/**
 * S19 タスク分担表のPDF出力。URL: /careshift/main/TaskSheetPdf.action?period=...&assignee=...&order=...
 * プレビューと同じ内容をPDFにして返す。0件・200件超のときは出力せず、理由を案内して画面に戻す。
 */
public class TaskSheetPdfAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        List<Member> members = new GroupDao().findMembers(me.getGroupId());
        TaskSheet sheet = TaskSheet.build(new TaskDao(), me.getGroupId(), req.getParameter("period"),
                req.getParameter("assignee"), req.getParameter("order"), members);
        if (sheet.isBlocked()) {
            flashError(req, sheet.getBlockedReason());
            resp.sendRedirect(req.getContextPath() + "/careshift/main/TaskSheet.action");
            return;
        }
        FamilyGroup group = new GroupDao().findGroup(me.getGroupId());

        String assigneeLabel = "全員";
        if ("none".equals(sheet.getAssignee())) {
            assigneeLabel = "担当なし";
        } else if (!sheet.getAssignee().isEmpty()) {
            for (Member m : members) {
                if (String.valueOf(m.getUserId()).equals(sheet.getAssignee())) assigneeLabel = m.getDisplayName();
            }
        }

        SimplePdf pdf = new SimplePdf("タスク分担表")
                .info("グループ: " + group.getName() + "　　作成日: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .info("対象期間: " + sheet.getPeriodLabel() + "　担当者: " + assigneeLabel + "　並び順: " + sheet.getOrderLabel()
                        + "　件数: " + sheet.getRows().size() + "件")
                .info("担当者がいないタスクは「担当なし」と表示しています。");
        if (sheet.getMaskedCount() > 0) {
            pdf.info("医療情報を含む可能性のある内容 " + sheet.getMaskedCount() + "件は、内容を表示していません。");
        }
        pdf.columns(new String[] { "タスク", "担当者", "期限", "所要時間", "状態" }, new float[] { 215, 85, 70, 65, 55 });
        for (TaskSheet.Row r : sheet.getRows()) {
            pdf.row(r.getTitle(), r.getAssignee(), r.getDue(), r.getDuration(), r.getStatus());
        }

        byte[] bytes = pdf.build();
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"task-sheet.pdf\"");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }
}
