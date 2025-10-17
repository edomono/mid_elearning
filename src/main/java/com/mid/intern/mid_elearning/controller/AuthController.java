package com.mid.intern.mid_elearning.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            if (auth.isAuthenticated()) {
                return "redirect:/";
            }
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Username atau password salah!");
            return "login";
        }
        model.addAttribute("error", "Terjadi kesalahan login");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.registerUser(user)) {
            model.addAttribute("success", "Registrasi berhasil! Silakan login.");
            return "login";
        } else {
            model.addAttribute("error", "Username atau email sudah digunakan!");
            return "register";
        }
    }
}
