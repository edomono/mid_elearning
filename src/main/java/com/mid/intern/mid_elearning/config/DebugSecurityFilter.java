package com.mid.intern.mid_elearning.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DebugSecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("DEBUG FILTER - Request URI: " + request.getRequestURI());
            System.out.println("DEBUG FILTER - Authenticated user: " + authentication.getName());
            System.out.println("DEBUG FILTER - Authorities: " + authentication.getAuthorities());
            System.out.println("DEBUG FILTER - Is Authenticated: " + authentication.isAuthenticated());
        } else {
            System.out.println("DEBUG FILTER - Request URI: " + request.getRequestURI() + " - No authentication found.");
        }
        filterChain.doFilter(request, response);
    }
}
