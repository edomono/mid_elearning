package com.mid.intern.mid_elearning.service;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        String errorMsg;

        if (exception instanceof DisabledException) {
            errorMsg = "Akun Anda belum disetujui oleh admin.";
        } else if (exception instanceof BadCredentialsException) {
            errorMsg = "Username atau password salah.";
        } else {
            errorMsg = "Gagal login. Silakan coba lagi.";
        }

        response.sendRedirect("/login?error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
    }
}
