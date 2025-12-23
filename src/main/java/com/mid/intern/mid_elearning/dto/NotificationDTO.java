package com.mid.intern.mid_elearning.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class NotificationDTO {
    private Long id;
    private String message;
    private boolean read;
    private Long timestamp; // epoch millis
    private String senderUsername;

    public NotificationDTO() {}

    public NotificationDTO(Long id, String message, boolean read, LocalDateTime timestamp, String senderUsername) {
        this.id = id;
        this.message = message;
        this.read = read;
        this.timestamp = toEpochMillis(timestamp);
        this.senderUsername = senderUsername;
    }

    private Long toEpochMillis(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public Long getTimestamp() { return timestamp; }
    public String getSenderUsername() { return senderUsername; }

    public void setId(Long id) { this.id = id; }
    public void setMessage(String message) { this.message = message; }
    public void setRead(boolean read) { this.read = read; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
}
