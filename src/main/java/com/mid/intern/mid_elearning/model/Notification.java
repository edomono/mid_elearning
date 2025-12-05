package com.mid.intern.mid_elearning.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id") // Nullable for system notifications
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public Notification() {
        this.timestamp = LocalDateTime.now();
    }

    public Notification(User recipient, User sender, String message) {
        this.recipient = recipient;
        this.sender = sender;
        this.message = message;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    public Notification(User recipient, String message) {
        this.recipient = recipient;
        this.message = message;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Notification{"
                + "id=" + id +
                ", recipient=" + (recipient != null ? recipient.getId() : "null") +
                ", sender=" + (sender != null ? sender.getId() : "null") +
                ", message='" + message + "'"
                + ", isRead=" + isRead +
                ", timestamp=" + timestamp +
                '}';
    }
}
