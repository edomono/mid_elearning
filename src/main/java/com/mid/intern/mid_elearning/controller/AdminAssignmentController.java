package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminAssignmentController {

    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public AdminAssignmentController(AssignmentService assignmentService, SubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @GetMapping("/course/{subjectId}/assignments/{assignmentId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<Assignment> getAssignmentDetailsJson(
            @PathVariable Long subjectId,
            @PathVariable Long assignmentId) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Assignment assignment = assignmentOpt.get();
        if (!assignment.getSubject().getId().equals(subjectId)) {
            return ResponseEntity.notFound().build();
        }
        assignment.getSubmissions().size();
        return ResponseEntity.ok(assignment);
    }


    @GetMapping("/assignment/{id}/edit")
    public String showEditAssignmentForm(@PathVariable("id") Long assignmentId, Model model) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("assignment", assignmentOpt.get());
        return "admin/edit-assignment";
    }

    @PostMapping("/assignment/{id}/edit")
    public String editAssignment(@PathVariable("id") Long assignmentId,
                               @ModelAttribute("assignment") Assignment assignment,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {

        assignment.setDueDate(dueDate);
        assignmentService.updateAssignment(assignmentId, assignment, file);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/submission/{id}/grade")
    public String gradeSubmission(@PathVariable("id") Long submissionId,
                                @RequestParam("grade") String grade,
                                @RequestParam("comment") String comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
        } else {
            System.out.println("No authentication found in SecurityContextHolder.");
        }
        submissionService.gradeSubmission(submissionId, grade, comment);
        Long assignmentId = submissionService.getAssignmentIdBySubmission(submissionId);
        return "redirect:/admin/course/" + assignmentId;
    }

    @PostMapping("/submission/{id}/delete")
    public String deleteSubmission(@PathVariable("id") Long submissionId) {
        Long assignmentId = submissionService.getAssignmentIdBySubmission(submissionId);
        submissionService.deleteSubmissionById(submissionId);
        return "redirect:/admin/assignment/" + assignmentId;
    }
}
