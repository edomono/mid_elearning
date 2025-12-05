package com.mid.intern.mid_elearning.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mid.intern.mid_elearning.dto.ParticipantRegistrationDto;
import com.mid.intern.mid_elearning.dto.StudentProgressDTO;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.ForumService;
import com.mid.intern.mid_elearning.service.StudentProgressService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    private final SubjectService subjectService;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;
    private final ForumService forumService;
    private final UserDetailsService userDetailsService;
    private final StudentProgressService studentProgressService;

    public AdminDashboardController(
            SubjectService subjectService,
            UserService userService,
            AnnouncementService announcementService,
            AssignmentService assignmentService,
            SubmissionService submissionService,
            ForumService forumService,
            UserDetailsService userDetailsService,
            StudentProgressService studentProgressService) {
        this.subjectService = subjectService;
        this.userService = userService;
        this.announcementService = announcementService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.forumService = forumService;
        this.userDetailsService = userDetailsService;
        this.studentProgressService = studentProgressService;
    }

    // =============================================================
    // 🏠 DASHBOARD
    // =============================================================
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        if (principal != null) {
            userService.getUserByUsername(principal.getName()).ifPresent(user -> model.addAttribute("user", user));
        }
    model.addAttribute("subjects", subjectService.getAllSubjects());
    model.addAttribute("participants", userService.getAllUsers());
    model.addAttribute("participant", new ParticipantRegistrationDto()); // Use DTO here
    model.addAttribute("waitingApprovals", userService.getPendingUsers());
    model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
    model.addAttribute("discussions", forumService.getAllDiscussions());
    return "admin/dashboard";
    }

    // =============================================================
    // 📘 COURSE MANAGEMENT (ADD ONLY)
    // =============================================================
    @GetMapping({"/add-course", "/course/add"})
    public String showAddCoursePage(Model model) {
        model.addAttribute("subject", new Subject());
        return "admin/add-course";
    }

    @PostMapping({"/add-course", "/course/add"})
    public String addCourse(@ModelAttribute("subject") Subject subject, Model model) {
        if (subject.getName() != null) subject.setName(subject.getName().trim());
        if (subject.getDescription() != null) subject.setDescription(subject.getDescription().trim());

        if (subjectService.existsByName(subject.getName())) {
            model.addAttribute("error", "Course name already exists!");
            model.addAttribute("subject", subject);
            return "admin/add-course";
        }

        long count = subjectService.getAllSubjects().size() + 1;
        subject.setCode(String.format("C%03d", count));
        subjectService.saveSubject(subject);
        return "redirect:/admin/dashboard";
    }

    // =============================================================
    // 🧩 ASSIGNMENT MANAGEMENT
    // =============================================================


    // =============================================================
    // 🧾 GRADE MANAGEMENT
    // =============================================================
    @PostMapping("/dashboard/submission/{id}/grade")
    public String gradeSubmission(
            @PathVariable("id") Long submissionId,
            @RequestParam("grade") String grade,
            @RequestParam(value = "comment", required = false) String comment) {

        submissionService.updateGradeAndComment(submissionId, grade, comment);
        Long assignmentId = submissionService.getAssignmentIdBySubmission(submissionId);
        return "redirect:/admin/assignment/" + assignmentId;
    }

    // =============================================================
    // 👥 PARTICIPANT MANAGEMENT
    // =============================================================


    @GetMapping("/participant/add")
    public String showAddParticipantPage(Model model) {
        model.addAttribute("participant", new ParticipantRegistrationDto());
        return "admin/add-participant";
    }

    @PostMapping("/participant/add")
    public String addParticipant(@Valid @ModelAttribute("participant") ParticipantRegistrationDto participantDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (result.hasErrors()) {
            return "admin/add-participant";
        }

        try {
            userService.registerParticipant(participantDto);
            redirectAttributes.addFlashAttribute("success", "Participant added successfully!");
            return "redirect:/admin/dashboard";
        } catch (IllegalStateException e) {
            result.rejectValue("email", "error.participantDto", e.getMessage());
            return "admin/add-participant";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding participant: " + e.getMessage());
            return "admin/add-participant";
        }
    }

    @GetMapping("/participant/{id:[0-9]+}")
    public String viewParticipantDetails(@PathVariable Long id, Model model) {
        Optional<User> participantOpt = userService.getUserById(id);
        if (participantOpt.isEmpty()) return "redirect:/admin/dashboard";

        model.addAttribute("participant", participantOpt.get());
        return "admin/participant-details";
    }

    @PostMapping("/participant/{id}/update")
    public String updateParticipant(@PathVariable Long id,
                                  @ModelAttribute("participant") User updatedUser,
                                  @RequestParam("role") String role) {
        Optional<User> existingOpt = userService.getUserById(id);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            existing.setUsername(updatedUser.getUsername());
            existing.setEmail(updatedUser.getEmail());
            existing.setRole(role); // Set role from the request parameter
            userService.saveUser(existing);

            // If the updated user is the currently authenticated user, update the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName().equals(existing.getUsername())) {
                // Re-authenticate the user with updated details
                UserDetails userDetails = userDetailsService.loadUserByUsername(existing.getUsername()); // Assuming userService implements UserDetailsService or has a similar method
                UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
                        userDetails, authentication.getCredentials(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/participant/{id}/delete")
    public String deleteParticipant(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/participant/{id}/approve")
    public String approveParticipant(@PathVariable Long id) {
        userService.approveUser(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/participant/{id}/reject")
    public String rejectParticipant(@PathVariable Long id) {
        userService.rejectUser(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/participants")
    public String showParticipants(Model model) {
        java.util.List<User> students = userService.getAllUsers();
        model.addAttribute("students", students);
        // Log each participant's id and role to help debug template rendering
        for (User s : students) {
            logger.info("Participant row: id={} username={} role={}", s.getId(), s.getUsername(), s.getRole());
        }
        return "admin/participants";
    }

    @GetMapping("/course/{courseId}/progress")
    public String showStudentProgress(@PathVariable("courseId") Long courseId, Model model) {
        Subject subject = subjectService.getSubjectById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
        List<StudentProgressDTO> studentProgress = studentProgressService.getStudentProgressForCourse(courseId);

        model.addAttribute("subject", subject);
        model.addAttribute("studentProgress", studentProgress);
        return "admin/student-progress";
    }

}