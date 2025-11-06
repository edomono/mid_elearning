package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserAssignmentController {

    private final AssignmentService assignmentService;

    public UserAssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
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
