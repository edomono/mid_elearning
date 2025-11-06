package com.mid.intern.mid_elearning.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/student")
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
    @GetMapping("/assignment/{id}")
    public String viewAssignment(
            @PathVariable("id") Long assignmentId,
            Principal principal,
            Model model
    ) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) return "redirect:/student/dashboard";

        Assignment assignment = assignmentOpt.get();
        User student = userService.getUserByEmail(principal.getName()).orElse(null);

        Optional<Submission> submission = submissionService.getSubmissionByAssignmentAndStudent(assignment, student);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submission", submission.orElse(null));
        return "student/assignment-detail";
    }

    // =============================================================
    // 📤 SUBMIT ASSIGNMENT
    // =============================================================
    @PostMapping("/assignment/{id}/submit")
    public String submitAssignment(
            @PathVariable("id") Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            Principal principal
    ) {
        User student = userService.getUserByEmail(principal.getName()).orElse(null);
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) return "redirect:/student/dashboard";
        Assignment assignment = assignmentOpt.get();

        submissionService.submitAssignment(student, assignment, file, note);
        return "redirect:/student/assignment/" + assignmentId;
    }

    // =============================================================
    // 🔁 UPDATE SUBMISSION
    // =============================================================
    @PostMapping("/submission/{id}/update")
    public String updateSubmission(
            @PathVariable("id") Long submissionId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            Principal principal
    ) {
        User student = userService.getUserByEmail(principal.getName()).orElse(null);
        submissionService.updateSubmission(submissionId, student, file, note);

        Long assignmentId = submissionService.getAssignmentIdBySubmission(submissionId);
        return "redirect:/student/assignment/" + assignmentId;
    }
}
