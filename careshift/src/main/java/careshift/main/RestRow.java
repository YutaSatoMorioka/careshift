package careshift.main;

import bean.RequestCard;
import bean.RequestRecipient;
import bean.RestPlan;

/**
 * 休息リクエスト画面の1行分。
 *
 * 休息予定と、その予定に対して最後に送信された依頼の状態をまとめる。
 */
public class RestRow {

    private final RestPlan plan;

    /**
     * 最新の依頼。
     * 依頼が一度も送信されていない場合はnull。
     */
    private final RequestCard latest;

    /**
     * 最新の依頼に未回答者が残っている。
     */
    private final boolean awaiting;

    /**
     * 最新の依頼について、全員が「今回は難しい」と回答した。
     */
    private final boolean allDeclined;

    public RestRow(
            RestPlan plan,
            RequestCard latest
    ) {

        this.plan = plan;
        this.latest = latest;

        boolean anyUnanswered = false;

        if (latest != null) {
            for (RequestRecipient recipient : latest.getRecipients()) {
                if (!recipient.isAnswered()) {
                    anyUnanswered = true;
                    break;
                }
            }
        }

        this.awaiting =
                latest != null
                && latest.isOpenForAnswer()
                && anyUnanswered;

        this.allDeclined =
                latest != null
                && latest.isAllDeclined();
    }

    public RestPlan getPlan() {
        return plan;
    }

    public RequestCard getLatest() {
        return latest;
    }

    public boolean isAwaiting() {
        return awaiting;
    }

    public boolean isAllDeclined() {
        return allDeclined;
    }

    /**
     * 再依頼できる条件。
     *
     * ・休息予定が調整中
     * ・開始時刻前
     * ・回答待ちの依頼が残っていない
     */
    public boolean isCanRerequest() {
        return plan.isAdjusting()
                && !plan.isStarted()
                && !awaiting;
    }

    /**
     * 取消できる条件。
     *
     * ・取消済みではない
     * ・開始時刻前
     *
     * 開始済みの予定を取り消すと、月次レポートの過去実績が
     * 後から変化するため、通常は開始前だけ許可する。
     */
    public boolean isCanCancel() {
        return !plan.isCancelled()
                && !plan.isStarted();
    }

    /**
     * 全員が対応できない場合に、外部相談先の案内を表示する。
     */
    public boolean isShowGuidance() {
        return plan.isAdjusting()
                && allDeclined;
    }
}