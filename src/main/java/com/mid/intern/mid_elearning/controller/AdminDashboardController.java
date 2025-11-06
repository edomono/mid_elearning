package com.mid.intern.mid_elearning.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mid.intern.mid_elearning.dto.ParticipantRegistrationDto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.ForumService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public AdminDashboardController(
            SubjectService subjectService,
            UserService userService,
            AnnouncementService announcementService,
            AssignmentService assignmentService,
            SubmissionService submissionService,
            ForumService forumService) {
        this.subjectService = subjectService;
        this.userService = userService;
        this.announcementService = announcementService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
        this.forumService = forumService;
    }

    // =============================================================
    // 🏠 DASHBOARD
    // =============================================================
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
    model.addAttribute("subjects", subjectService.getAllSubjects());
    model.addAttribute("participants", userService.getAllUsers());
    model.addAttribute("participant", new ParticipantRegistrationDto()); // Use DTO here
    model.addAttribute("waitingApprovals", userService.getPendingUsers());
    model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
    model.addAttribute("discussions", forumService.getAllDiscussions());
    return "admin/dashboard";
    }

    // =============================================================
    // 📢 ANNOUNCEMENTS
    // =============================================================
    @PostMapping("/announcement/add")
    public String addAnnouncement(@RequestParam("content") String content, Principal principal) {
        if (content != null && !content.trim().isEmpty() && principal != null) {
            User user = userService.getUserByUsername(principal.getName()).orElse(null);
            if (user != null) {
                Announcement a = new Announcement();
                a.setContent(content.trim());
                a.setUsername(user.getUsername());
                a.setRole(user.getRole());
                announcementService.saveAnnouncement(a);
            }
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/announcement/{id}/edit")
    @ResponseBody
    public ResponseEntity<?> editAnnouncement(@PathVariable Long id, @RequestBody Announcement updated) {
        Optional<Announcement> opt = announcementService.getAnnouncementById(id);
        if (opt.isPresent()) {
            Announcement a = opt.get();
            if (updated.getContent() != null && !updated.getContent().trim().isEmpty()) {
                a.setContent(updated.getContent().trim());
                announcementService.saveAnnouncement(a);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/announcement/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        if (announcementService.existsById(id)) {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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


    @GetMapping("/assignment/{id}")
    public String viewAssignmentDetails(@PathVariable("id") Long assignmentId, Model model) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }
        Assignment assignment = assignmentOpt.get();

        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignment);
        Optional<Subject> subjectOpt;
        if (assignment.getSubject() != null) {
            subjectOpt = subjectService.getSubjectById(assignment.getSubject().getId());
        } else {
            subjectOpt = Optional.empty();
        }
        if (subjectOpt.isEmpty()) {
            logger.error("Subject not found for assignment ID: {}", assignmentId);
            return "redirect:/admin/dashboard";
        }
        Subject subject = subjectOpt.get();

                    model.addAttribute("assignment", Optional.of(assignment));        model.addAttribute("submissions", submissions);
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        model.addAttribute("discussions", forumService.getAllDiscussions());
        model.addAttribute("subject", subject);
        return "admin/assignment-details";
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
        return "redirect:/admin/assignment/" + assignmentId;
    }

    @PostMapping("/assignment/{id}/delete")
    public String deleteAssignment(@PathVariable("id") Long id) {
        Long subjectId = assignmentService.getAssignmentSubjectId(id);
        assignmentService.deleteAssignment(id);
        if (subjectId == null) {
            logger.error("Subject ID not found for assignment ID: {}", id);
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/course/" + subjectId;
    }

    // =============================================================
    // 🧾 GRADE MANAGEMENT
    // =============================================================
    @PostMapping("/submission/{id}/grade")
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
        model.addAttribute("students", userService.getAllUsers());
        return "admin/participants";
    }

}