package careshift.main;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bean.Member;
import bean.RestPlan;
import bean.User;
import dao.RestDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tool.Action;
import tool.SimplePdf;
import tool.Validator;

/** S15 休息レポートのPDF出力。URL: /careshift/main/RestReportPdf.action?targetMonth=2026-11 */
public class RestReportPdfAction extends Action {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User user = requireLogin(req, resp);
        if (user == null) return;
        Member me = requireMember(req, resp, user);
        if (me == null) return;

        YearMonth ym;
        try {
            ym = YearMonth.parse(Validator.trim(req.getParameter("targetMonth")));
        } catch (DateTimeParseException e) {
            ym = YearMonth.now();
        }
        List<RestPlan> plans = new RestDao().findByDateRange(me.getGroupId(), ym.atDay(1), ym.atEndOfMonth());
        List<RestPlan> confirmed = new ArrayList<>();
        int adjusting = 0, cancelled = 0, minutes = 0;
        for (RestPlan p : plans) {
            if (p.isConfirmed()) {
                confirmed.add(p);
                minutes += p.getMinutes();
            } else if (p.isCancelled()) {
                cancelled++;
            } else {
                adjusting++;
            }
        }

        SimplePdf pdf = new SimplePdf("休息レポート　" + ym.getYear() + "年" + ym.getMonthValue() + "月")
                .info("確定した休息: " + confirmed.size() + "回　合計時間: " + MonthStat.format(minutes))
                .info("実績に含めないもの: 調整中 " + adjusting + "件／取消 " + cancelled + "件")
                .columns(new String[] { "日付", "時間", "休息時間", "担当" }, new float[] { 100, 130, 100, 120 });
        for (RestPlan p : confirmed) {
            pdf.row(p.getDateLabel(), p.getTimeRange(), p.getDurationLabel(), p.getCoveredName());
        }

        byte[] bytes = pdf.build();
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"rest-report.pdf\"");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }
}
