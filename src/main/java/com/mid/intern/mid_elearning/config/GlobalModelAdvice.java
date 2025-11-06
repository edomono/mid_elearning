package com.mid.intern.mid_elearning.config;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.UserService;

@Component
@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;

    public GlobalModelAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {
        if (authentication == null) return;
        String username = authentication.getName();
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
        }
    }
}
