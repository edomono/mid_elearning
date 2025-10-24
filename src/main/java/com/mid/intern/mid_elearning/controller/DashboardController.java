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

        if (role.equals("ROLE_ADMIN")) {
            return "admin/dashboard";
        } else if (role.equals("ROLE_MENTOR")) {
            return "mentor/dashboard";
        } else {
            return "user/dashboard";
        }
    }
}
