package com.mid.intern.mid_elearning.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.ForumService;
import com.mid.intern.mid_elearning.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ForumController {

    private final UserService userService;
    private final ForumService forumService;

    public ForumController(UserService userService, ForumService forumService) {
        this.userService = userService;
        this.forumService = forumService;
    }

    @PostMapping("/forum/send")
    public String sendMessage(Authentication authentication,
                              @RequestParam String message,
                              @RequestParam(required = false) Long redirectCourseId,
                              HttpServletRequest request,
                              @RequestHeader(value = "referer", required = false) String referer) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) return "redirect:/login";

        forumService.sendMessage(user, message);

        // Redirect back to course details if provided, otherwise fall back to referer or dashboard
        if (redirectCourseId != null) {
            return "redirect:/user/course-details/" + redirectCourseId;
        }
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/user/dashboard";
    }
}
