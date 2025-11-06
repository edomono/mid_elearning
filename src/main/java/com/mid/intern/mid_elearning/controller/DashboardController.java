package com.mid.intern.mid_elearning.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        String role = auth.getAuthorities().iterator().next().getAuthority();

        return switch (role) {
            case "ROLE_ADMIN" -> "admin/dashboard";
            case "ROLE_MENTOR" -> "mentor/dashboard";
            default -> "user/dashboard";
        };
    }
}
