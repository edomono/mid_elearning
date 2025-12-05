package com.mid.intern.mid_elearning.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

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
