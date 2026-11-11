package bean;

import java.time.LocalDate;

/** グループのプラン（group_plans の1行。行がなければ無料）。 */
public class GroupPlan {
    public static final String FREE = "FREE";
    public static final String PAID = "PAID";

    private final String plan;
    private final LocalDate renewsOn;       // 次回更新日（解約予約中は有料の利用期限）。無料は null
    private final boolean cancelRequested;  // 「無料へ戻す」を予約済み

    public GroupPlan(String plan, LocalDate renewsOn, boolean cancelRequested) {
        this.plan = plan;
        this.renewsOn = renewsOn;
        this.cancelRequested = cancelRequested;
    }

    public static GroupPlan free() {
        return new GroupPlan(FREE, null, false);
    }

    public String getPlan() { return plan; }
    public LocalDate getRenewsOn() { return renewsOn; }
    public boolean isCancelRequested() { return cancelRequested; }

    /** いま有料機能を使えるか。解約予約中でも、利用期限（次回更新日）までは使える。 */
    public boolean isPaid() { return isPaid(LocalDate.now()); }

    public boolean isPaid(LocalDate today) {
        if (!PAID.equals(plan)) return false;
        return !(cancelRequested && renewsOn != null && today.isAfter(renewsOn));
    }

    /** 表示用の「次回更新日／利用期限」。解約しない有料は、過ぎた分を1か月ずつ進める（自動更新の想定）。 */
    public LocalDate getNextDate(LocalDate today) {
        if (renewsOn == null) return null;
        LocalDate d = renewsOn;
        if (!cancelRequested) {
            while (d.isBefore(today)) d = d.plusMonths(1);
        }
        return d;
    }

    public LocalDate getNextDate() { return getNextDate(LocalDate.now()); }

    public String getPlanLabel() { return isPaid() ? "有料" : "無料"; }
}
