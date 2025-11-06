package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/participants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminParticipantApiController {

    private final UserService userService;

    public AdminParticipantApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getParticipant(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}