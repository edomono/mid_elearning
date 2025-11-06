package com.mid.intern.mid_elearning.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
public class UserProfileController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/user/profile-details")
    public String profileDetails(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        model.addAttribute("user", user);
        return "user/profile-details";
    }

    @PostMapping("/user/profile/change-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        model.addAttribute("user", user);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "user/profile-details";
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            return "user/profile-details";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirmation do not match.");
            return "user/profile-details";
        }
        if (newPassword.length() < 6) {
            model.addAttribute("error", "New password must be at least 6 characters.");
            return "user/profile-details";
        }
        userService.updateUserPassword(user, newPassword);
        model.addAttribute("success", "Password changed successfully.");
        return "user/profile-details";
    }
}
