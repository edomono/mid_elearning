package com.mid.intern.mid_elearning.controller;

import java.security.Principal;
import org.springframework.security.core.Authentication;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/user")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;
    private final UserService userService;

    public SubmissionController(
            SubmissionService submissionService,
            AssignmentService assignmentService,
            UserService userService
    ) {
        this.submissionService = submissionService;
        this.assignmentService = assignmentService;
        this.userService = userService;
    }

    // =============================================================
    // 📎 VIEW ASSIGNMENT + STATUS
    // =============================================================
    @GetMapping("/submission/{id}")
    public String viewAssignment(
            @PathVariable("id") Long assignmentId,
            Authentication authentication,
            Model model
    ) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) return "redirect:/user/dashboard";
        Assignment assignment = assignmentOpt.get();

        User student = userService.getUserByUsername(authentication.getName()).orElse(null);
        if (student == null) {
            throw new IllegalStateException("Authenticated user not found in database: " + authentication.getName());
        }

        Optional<Submission> submission = submissionService.getSubmissionByAssignmentAndStudent(assignment, student);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission.orElse(null));
        model.addAttribute("subject", assignment.getSubject()); // Add the subject to the model
        return "user/course-details";
    }

    // =============================================================
    // 📤 SUBMIT ASSIGNMENT
    // =============================================================
    @PostMapping("/assignment/{id}/submit")
    public String submitAssignment(
            @PathVariable("id") Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            Authentication authentication
    ) {
        User student = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database: " + authentication.getName()));

        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) return "redirect:/user/dashboard";
        Assignment assignment = assignmentOpt.get();

        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            return "redirect:/user/submission/" + assignmentId + "?error=The deadline for this assignment has passed.";
        }

        try {
            Optional<Submission> existing = submissionService.getSubmissionByAssignmentAndStudent(assignment, student);

            if (existing.isPresent()) {
                submissionService.updateSubmission(existing.get().getId(), student, file, note);
            } else {
                submissionService.submitAssignment(student, assignment, file, note);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "redirect:/user/submission/" + assignmentId + "?error=File upload failed";
        } catch (SecurityException e) {
            e.printStackTrace();
            return "redirect:/user/submission/" + assignmentId + "?error=" + e.getMessage();
        }
        return "redirect:/user/course-details/" + assignment.getSubject().getId();
    }

    // =============================================================
    // 🔁 UPDATE SUBMISSION
    // =============================================================
    @PostMapping("/submission/{id}/update")
    public ResponseEntity<String> updateSubmission(
            @PathVariable("id") Long submissionId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            Authentication authentication
    ) {
        User student = userService.getUserByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database: " + authentication.getName()));
        try {
            submissionService.updateSubmission(submissionId, student, file, note);
            return ResponseEntity.ok("Submission updated successfully.");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        } catch (SecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating submission: " + e.getMessage());
        }
    }

    // =============================================================
    // ❌ DELETE SUBMISSION
    // =============================================================
    @DeleteMapping("/submission/{id}")
    public ResponseEntity<?> deleteSubmission(
            @PathVariable("id") Long submissionId,
            Authentication authentication
    ) {
        User student = userService.getUserByUsername(authentication.getName()).orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
        try {
            submissionService.deleteSubmission(submissionId, student);
            return ResponseEntity.ok().body("Submission deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting submission: " + e.getMessage());
        }
    }
}
