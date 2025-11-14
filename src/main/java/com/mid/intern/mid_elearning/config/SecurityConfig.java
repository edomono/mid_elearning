package com.mid.intern.mid_elearning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final DebugSecurityFilter debugSecurityFilter;

    public SecurityConfig(DebugSecurityFilter debugSecurityFilter) {
        this.debugSecurityFilter = debugSecurityFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // authenticationProvider removed: rely on AuthenticationConfiguration and
    // the application's UserDetailsService + PasswordEncoder beans (Spring Boot will wire them).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String redirectURL = "";

            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectURL = "/admin/dashboard";
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"))) {
                redirectURL = "/mentor/dashboard";
            } else {
                redirectURL = "/user/dashboard";
            }

            response.sendRedirect(redirectURL);
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                    .authorizeHttpRequests(auth -> auth
                // ✅ hanya halaman publik yang boleh diakses tanpa login
                .requestMatchers("/", "/login", "/process-login", "/logout",
                                 "/css/**", "/js/**", "/images/**").permitAll()

                // ✅ akses role-based
                .requestMatchers(HttpMethod.POST, "/admin/assignment/{id}/edit").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/admin/submission/{id}/grade").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/mentor/assignment/{id}/edit").hasAuthority("ROLE_MENTOR")
                .requestMatchers(HttpMethod.POST, "/mentor/submission/{submissionId}/grade").hasAuthority("ROLE_MENTOR")
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/mentor/**").hasAuthority("ROLE_MENTOR")
                .requestMatchers("/user/**").hasAuthority("ROLE_USER")

                // semua lainnya butuh login
                .requestMatchers("/assignments/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/process-login")
                .successHandler(successHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .addFilterBefore(debugSecurityFilter, AuthorizationFilter.class)
            ;

        return http.build();
    }
}
