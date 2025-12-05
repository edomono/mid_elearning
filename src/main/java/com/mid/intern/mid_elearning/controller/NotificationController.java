package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.Notification;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.UserRepository;
import com.mid.intern.mid_elearning.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository; // Inject UserRepository

    public NotificationController(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    private Long getAuthenticatedUserId(UserDetails userDetails) {
        System.out.println("DEBUG: Entering getAuthenticatedUserId method.");
        if (userDetails == null) {
            System.out.println("DEBUG: userDetails is null. Cannot extract username.");
            throw new UsernameNotFoundException("Authentication principal (UserDetails) is null.");
        }
        String username = userDetails.getUsername();
        System.out.println("DEBUG: Authenticated username: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in DB with username: " + username));
        System.out.println("DEBUG: User found with ID: " + user.getId() + " for username: " + username);
        return user.getId();
    }

    @PostMapping("/send/{recipientId}")
    public ResponseEntity<String> sendNotification(
            @PathVariable Long recipientId,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails senderDetails) {

        if (payload == null || payload.isEmpty()) {
            return ResponseEntity.badRequest().body("Notification payload cannot be empty.");
        }

        String message = payload.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Notification message cannot be empty.");
        }

        Long senderId = getAuthenticatedUserId(senderDetails);

        try {
            notificationService.createNotification(recipientId, senderId, message);
            return ResponseEntity.ok("Notification sent successfully to user " + recipientId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("DEBUG: Entering getUserNotifications method.");
        Long userId = getAuthenticatedUserId(userDetails);
        List<Notification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationsCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        long unreadCount = notificationService.countUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        boolean success = notificationService.markNotificationAsRead(notificationId, userId);
        if (success) {
            return ResponseEntity.ok("Notification " + notificationId + " marked as read.");
        } else {
            return ResponseEntity.badRequest().body("Failed to mark notification " + notificationId + " as read. It might not exist or not belong to the user.");
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<String> markAllNotificationsAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getAuthenticatedUserId(userDetails);
        int count = notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok(count + " notifications marked as read.");
    }
}


