package bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 通知（notifications テーブルの1行）。 */
public class Notification {
    private final long id;
    private final String type;       // REQUEST / ANSWER / REST / DEADLINE / TASK / GROUP
    private final String message;
    private final String linkPath;   // 開く先（コンテキストパスを除いた道筋）。なければ空
    private final boolean read;
    private final LocalDateTime createdAt;

    public Notification(long id, String type, String message, String linkPath, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.linkPath = linkPath == null ? "" : linkPath;
        this.read = read;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getLinkPath() { return linkPath; }
    public boolean isRead() { return read; }
    public boolean isUnread() { return !read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getCreatedAtLabel() { return createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")); }

    /** 設計書の種別（依頼／回答／休息／期限）。それ以外（家族・タスクの通知）は「その他」 */
    public String getTypeLabel() {
        switch (type) {
            case "REQUEST": return "依頼";
            case "ANSWER": return "回答";
            case "REST": return "休息";
            case "DEADLINE": return "期限";
            default: return "その他";
        }
    }
}
