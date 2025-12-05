package com.mid.intern.mid_elearning.service;

import com.mid.intern.mid_elearning.model.Notification;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.NotificationRepository;
import com.mid.intern.mid_elearning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository; // To fetch User objects by ID

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create and save a new notification.
     * @param recipientId The ID of the user who will receive the notification.
     * @param senderId The ID of the user who sent the notification (can be null for system notifications).
     * @param message The content of the notification.
     * @return The saved Notification object.
     */
    @Transactional
    public Notification createNotification(Long recipientId, Long senderId, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found with ID: " + recipientId));

        User sender = null;
        if (senderId != null) {
            sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        }

        Notification notification = new Notification(recipient, sender, message);
        return notificationRepository.save(notification);
    }

    /**
     * Create and save a new system notification.
     * @param recipientId The ID of the user who will receive the notification.
     * @param message The content of the notification.
     * @return The saved Notification object.
     */
    @Transactional
    public Notification createSystemNotification(Long recipientId, String message) {
        return createNotification(recipientId, null, message);
    }

    /**
     * Get all notifications for a specific user, ordered by timestamp descending.
     * @param userId The ID of the recipient user.
     * @return A list of Notification objects.
     */
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Notification> notifications = notificationRepository.findByRecipientOrderByTimestampDesc(recipient);

        // Explicitly initialize lazy-loaded User objects for each notification
        for (Notification notification : notifications) {
            // Accessing the getters will initialize the lazy-loaded entities
            if (notification.getRecipient() != null) {
                notification.getRecipient().getId(); // Access ID to ensure it's loaded
            }
            if (notification.getSender() != null) {
                notification.getSender().getId(); // Access ID to ensure it's loaded
            }
        }
        return notifications;
    }

    /**
     * Get unread notifications for a specific user, ordered by timestamp descending.
     * @param userId The ID of the recipient user.
     * @return A list of unread Notification objects.
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalseOrderByTimestampDesc(recipient);

        // Explicitly initialize lazy-loaded User objects for each notification
        for (Notification notification : notifications) {
            // Accessing the getters will initialize the lazy-loaded entities
            if (notification.getRecipient() != null) {
                notification.getRecipient().getId(); // Access ID to ensure it's loaded
            }
            if (notification.getSender() != null) {
                notification.getSender().getId(); // Access ID to ensure it's loaded
            }
        }
        return notifications;
    }

    /**
     * Count unread notifications for a specific user.
     * @param userId The ID of the recipient user.
     * @return The count of unread notifications.
     */
    @Transactional(readOnly = true)
    public long countUnreadNotificationsForUser(Long userId) {
        User recipient = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return notificationRepository.countByRecipientAndIsReadFalse(recipient);
    }

    /**
     * Mark a specific notification as read.
     * @param notificationId The ID of the notification to mark as read.
     * @param userId The ID of the user who owns the notification (for security).
     * @return True if the notification was found and marked as read, false otherwise.
     */
    @Transactional
    public boolean markNotificationAsRead(Long notificationId, Long userId) {
        Optional<Notification> notificationOptional = notificationRepository.findById(notificationId);
        if (notificationOptional.isPresent()) {
            Notification notification = notificationOptional.get();
            // Ensure the notification belongs to the user trying to mark it as read
            if (notification.getRecipient().getId().equals(userId)) {
                notification.setRead(true);
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }

    /**
     * Mark all unread notifications for a specific user as read.
     * @param userId The ID of the user.
     * @return The number of notifications marked as read.
     */
    @Transactional
    public int markAllNotificationsAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotificationsForUser(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
        return unreadNotifications.size();
    }
}
