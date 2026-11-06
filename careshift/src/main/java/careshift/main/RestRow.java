package careshift.main;

import bean.RequestCard;
import bean.RestPlan;

/** 休息リクエスト画面の1行分（休息予定と、その最新の依頼の状況）。 */
public class RestRow {
    private final RestPlan plan;
    private final RequestCard latest;      // 最新の依頼（なければ null）
    private final boolean awaiting;        // 回答待ちの依頼がある
    private final boolean allDeclined;     // 最新の依頼は全員が「今回は難しい」

    public RestRow(RestPlan plan, RequestCard latest) {
        this.plan = plan;
        this.latest = latest;
        // 「回答待ち」は、受付中で、まだ回答していない宛先がいる間だけ。全員が回答済み（全員が難しい等）なら再依頼できる
        boolean anyUnanswered = false;
        if (latest != null) {
            for (bean.RequestRecipient r : latest.getRecipients()) {
                if (!r.isAnswered()) anyUnanswered = true;
            }
        }
        this.awaiting = latest != null && latest.isOpenForAnswer() && anyUnanswered;
        this.allDeclined = latest != null && latest.isAllDeclined();
    }

    public RestPlan getPlan() { return plan; }
    public RequestCard getLatest() { return latest; }
    public boolean isAwaiting() { return awaiting; }
    public boolean isAllDeclined() { return allDeclined; }

    /** 調整中のまま再依頼できる：調整中で、開始前で、未回答の宛先がいる依頼がない */
    public boolean isCanRerequest() { return plan.isAdjusting() && !plan.isStarted() && !awaiting; }

    public boolean isCanCancel() { return !plan.isCancelled(); }

    /** 全員が難しい場合の、相談先の案内を出す */
    public boolean isShowGuidance() { return plan.isAdjusting() && allDeclined; }
}
