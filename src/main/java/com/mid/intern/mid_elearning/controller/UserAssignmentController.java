package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserAssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final UserService userService;

    public UserAssignmentController(AssignmentService assignmentService, SubmissionService submissionService, UserService userService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.userService = userService;
    }

    @GetMapping("/assignment/{id}/details")
    @ResponseBody
    public ResponseEntity<Assignment> getAssignmentDetailsJson(@PathVariable Long id) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(id);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Assignment assignment = assignmentOpt.get();
        // Initialize submissions to avoid lazy loading issues if not already fetched
        assignment.getSubmissions().size();
        return ResponseEntity.ok(assignment);
    }


}
