package com.mid.intern.mid_elearning.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.ForumService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
public class StudentDashboardController {

    private final UserService userService;
    private final SubjectService subjectService;
    private final ForumService forumService;
    private final AssignmentService assignmentService;
    private final com.mid.intern.mid_elearning.service.SubmissionService submissionService;
    private final com.mid.intern.mid_elearning.service.AnnouncementService announcementService;

    public StudentDashboardController(
            UserService userService,
            SubjectService subjectService,
            ForumService forumService,
            AssignmentService assignmentService,
            com.mid.intern.mid_elearning.service.AnnouncementService announcementService,
            com.mid.intern.mid_elearning.service.SubmissionService submissionService) {
        this.userService = userService;
        this.subjectService = subjectService;
        this.forumService = forumService;
        this.assignmentService = assignmentService;
        this.announcementService = announcementService;
        this.submissionService = submissionService;
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // ✅ Set user data untuk ditampilkan
        model.addAttribute("user", user);

        // ✅ Ambil semua subjects yang dibuat mentor/admin (bukan yang dimiliki student saja)
        model.addAttribute("subjects", subjectService.getAllSubjects());

        // ✅ Load all forum messages
        model.addAttribute("discussions", forumService.getAllDiscussions());
        
        // ✅ Load announcements so user can see mentor/admin announcements
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());

        return "user/dashboard";
    }

    @PostMapping("/user/forum/send")
    public String sendMessage(Authentication authentication, @RequestParam String message) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username).get();

        forumService.sendMessage(user, message);

        return "redirect:/user/dashboard";
    }
    
    @GetMapping("/user/course-details/{id}")
    public String courseDetails(@PathVariable("id") Long id, Authentication authentication, Model model) {
        try {
            // ✅ Ambil user logged in
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            model.addAttribute("user", user);

            // ✅ Ambil detail course dengan proper error handling
            Subject subject = subjectService.getSubjectById(id)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
            model.addAttribute("subject", subject);

            // ✅ Ambil semua assignment untuk course ini dan cek submission user
            List<Assignment> assignments = assignmentService.getAssignmentsBySubject(subject);
            List<com.mid.intern.mid_elearning.dto.AssignmentWithSubmissionDTO> assignmentsWithSubmission = assignments.stream()
                    .map(assignment -> {
                        java.util.Optional<com.mid.intern.mid_elearning.model.Submission> userSubmission =
                                submissionService.getSubmissionByAssignmentAndStudent(assignment, user);
                        return new com.mid.intern.mid_elearning.dto.AssignmentWithSubmissionDTO(assignment, userSubmission);
                    })
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("assignmentsWithSubmission", assignmentsWithSubmission);

            // ✅ Ambil announcements dan discussions untuk sidebar
            model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
            model.addAttribute("discussions", forumService.getAllDiscussions());

            return "user/course-details";
        } catch (Exception e) {
            model.addAttribute("error", "Course tidak ditemukan atau terjadi kesalahan: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }

    // =============================================================
    // 📄 ASSIGNMENT DETAILS (student view + submission)
    // =============================================================
    @GetMapping("/user/assignment-details/{id}")
    public String viewAssignment(@PathVariable("id") Long id, Authentication authentication, Model model) {
        try {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            Assignment assignment = assignmentService.getAssignmentById(id)
                    .orElseThrow(() -> new RuntimeException("Assignment not found"));

            // get existing submission if any
            java.util.Optional<com.mid.intern.mid_elearning.model.Submission> subOpt =
                    submissionService.getSubmissionByAssignmentAndStudent(assignment, user);

            model.addAttribute("user", user);
            model.addAttribute("assignment", assignment);
            model.addAttribute("submission", subOpt.orElse(null));
            model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
            model.addAttribute("discussions", forumService.getAllDiscussions());

            return "user/assignment-details";
        } catch (Exception e) {
            model.addAttribute("error", "Assignment tidak ditemukan atau terjadi kesalahan: " + e.getMessage());
            return "redirect:/user/dashboard";
        }
    }









}
