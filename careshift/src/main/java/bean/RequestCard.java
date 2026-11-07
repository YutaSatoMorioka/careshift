package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import tool.Options;

/** 依頼カード（requests と、送信者名・引受者名を結合した1行。宛先は recipients に入る）。 */
public class RequestCard {
    public static final String OPEN = "OPEN";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String WITHDRAWN = "WITHDRAWN";

    private final long id;
    private final long groupId;
    private final String type;
    private final String content;
    private final LocalDateTime requestedAt;
    private final Integer durationMin;
    private final Long taskId;
    private final Long restId;
    private final long senderId;
    private final String senderName;
    private final String status;
    private final Long acceptedBy;
    private final String acceptedName;
    private final List<RequestRecipient> recipients = new ArrayList<>();

    public RequestCard(long id, long groupId, String type, String content, LocalDateTime requestedAt,
            Integer durationMin, Long taskId, Long restId, long senderId, String senderName, String status,
            Long acceptedBy, String acceptedName) {
        this.id = id;
        this.groupId = groupId;
        this.type = type;
        this.content = content;
        this.requestedAt = requestedAt;
        this.durationMin = durationMin;
        this.taskId = taskId;
        this.restId = restId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.status = status;
        this.acceptedBy = acceptedBy;
        this.acceptedName = acceptedName;
    }

    public long getId() { return id; }
    public long getGroupId() { return groupId; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public Integer getDurationMin() { return durationMin; }
    public Long getTaskId() { return taskId; }
    public Long getRestId() { return restId; }
    public long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getStatus() { return status; }
    public Long getAcceptedBy() { return acceptedBy; }
    public String getAcceptedName() { return acceptedName; }
    public List<RequestRecipient> getRecipients() { return recipients; }

    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    /** 例: 2026/12/01 09:00 */
    public String getRequestedAtLabel() { return requestedAt.format(LABEL); }

    public String getTypeLabel() { return Options.REQUEST_TYPES.getOrDefault(type, type); }

    /** 希望日時・期限を過ぎている */
    public boolean isExpired() { return requestedAt.isBefore(LocalDateTime.now()); }

    /** 回答を受け付けている（取り下げ・期限切れ・引受済みでない） */
    public boolean isOpenForAnswer() { return OPEN.equals(status) && !isExpired(); }

    /** 送信者が取り下げられる状態（回答待ちで、期限前） */
    public boolean isWithdrawable() { return isOpenForAnswer(); }

    /** 画面表示用の状態。取り下げ済み／引受済み／期限切れ／回答待ち */
    public String getStatusLabel() {
        if (WITHDRAWN.equals(status)) return "取り下げ済み";
        if (ACCEPTED.equals(status)) return "引受済み（" + acceptedName + "）";
        if (isExpired()) return "期限切れ";
        return "回答待ち";
    }

    /** 取り下げできない理由（できるなら空） */
    public String getWithdrawBlockedReason() {
        if (WITHDRAWN.equals(status)) return "すでに取り下げ済みです。";
        if (ACCEPTED.equals(status)) return acceptedName + " さんがすでに引き受けているため、取り下げできません。";
        if (isExpired()) return "希望日時・期限を過ぎているため、取り下げできません。";
        return "";
    }

    /** 回答できない理由（できるなら空）。viewerId は回答しようとしている人 */
    public String getAnswerBlockedReason(long viewerId) {
        if (WITHDRAWN.equals(status)) return "この依頼は取り下げられたため、回答できません。";
        if (isExpired()) return "希望日時・期限を過ぎているため、回答できません。";
        if (ACCEPTED.equals(status) && (acceptedBy == null || acceptedBy != viewerId)) {
            return acceptedName + " さんがすでに引き受けているため、回答できません。";
        }
        return "";
    }

    /** 全員が「今回は難しい」と回答済み（引受なし） */
    public boolean isAllDeclined() {
        if (recipients.isEmpty() || !OPEN.equals(status)) return false;
        for (RequestRecipient r : recipients) {
            if (!"DECLINE".equals(r.getResponse())) return false;
        }
        return true;
    }

    public RequestRecipient recipient(long userId) {
        for (RequestRecipient r : recipients) {
            if (r.getUserId() == userId) return r;
        }
        return null;
    }
}
