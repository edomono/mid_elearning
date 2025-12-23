
package com.mid.intern.mid_elearning.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubmissionService;

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
        // After updating the assignment, redirect back to the course page (keep context for the admin)
        Long subjectId = assignmentService.getAssignmentSubjectId(assignmentId);
        if (subjectId == null) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/course/" + subjectId;
    }

    @PostMapping("/submission/{id}/grade")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> gradeSubmission(@PathVariable("id") Long submissionId,
                                @RequestParam("grade") String grade,
                                @RequestParam("comment") String comment) {
        Map<String, Object> response = new HashMap<>();
        try {
            submissionService.gradeSubmission(submissionId, grade, comment);
            response.put("success", true);
            response.put("message", "Grade saved successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error saving grade: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/submission/{id}/delete")
    public String deleteSubmission(@PathVariable("id") Long submissionId) {
        Long assignmentId = submissionService.getAssignmentIdBySubmission(submissionId);
        submissionService.deleteSubmissionById(submissionId);
        return "redirect:/admin/assignment/" + assignmentId;
    }

    @PostMapping("/assignment/{id}/delete")
    public String deleteAssignment(@PathVariable("id") Long assignmentId) {
        Long subjectId = assignmentService.getAssignmentSubjectId(assignmentId);
        if (subjectId == null) {
            System.err.println("⚠️ Subject ID not found for assignment " + assignmentId);
            return "redirect:/admin/dashboard";
        }
        assignmentService.deleteAssignment(assignmentId);
        System.out.println("🗑️ Assignment " + assignmentId + " berhasil dihapus");
        return "redirect:/admin/course/" + subjectId;
    }
}
