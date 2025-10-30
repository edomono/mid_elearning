package com.mid.intern.mid_elearning.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final SubjectService subjectService;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public AdminDashboardController(
            SubjectService subjectService,
            UserService userService,
            AnnouncementService announcementService,
            AssignmentService assignmentService,
            SubmissionService submissionService) {

        this.subjectService = subjectService;
        this.userService = userService;
        this.announcementService = announcementService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    // =============================================================
    // 🏠 DASHBOARD
    // =============================================================
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("participants", userService.getAllUsers());
        model.addAttribute("waitingApprovals", userService.getPendingUsers());
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        return "admin/dashboard";
    }

    // =============================================================
    // 📢 ANNOUNCEMENTS
    // =============================================================
    @PostMapping("/announcement/add")
    public String addAnnouncement(@RequestParam("content") String content) {
        if (content != null && !content.trim().isEmpty()) {
            Announcement a = new Announcement();
            a.setContent(content.trim());
            a.setUsername("Admin");
            a.setRole("Admin");
            announcementService.saveAnnouncement(a);
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

        boolean exists = subjectService.getAllSubjects().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(subject.getName()));

        if (exists) {
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
    @PostMapping("/course/{id}/assignment/add")
    public String addAssignment(
            @PathVariable("id") Long subjectId,
            @ModelAttribute("newAssignment") Assignment assignment,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        assignmentService.saveAssignment(subjectId, assignment, file);
        return "redirect:/admin/course/" + subjectId;
    }

    @GetMapping("/assignment/{id}")
    public String viewAssignmentDetails(@PathVariable("id") Long assignmentId, Model model) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) return "redirect:/admin/dashboard";

        Assignment assignment = assignmentOpt.get();
        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignment);

        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", submissions);
        return "admin/assignment-details";
    }

    @PostMapping("/assignment/{id}/delete")
    public String deleteAssignment(@PathVariable("id") Long id) {
        Long subjectId = assignmentService.getAssignmentSubjectId(id);
        assignmentService.deleteAssignment(id);
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
    @GetMapping({"/add-participant", "/participant/add"})
    public String showAddParticipantForm(Model model) {
        model.addAttribute("participant", new User());
        return "admin/add-participant";
    }

    @PostMapping({"/add-participant", "/participant/add"})
    public String addParticipant(@ModelAttribute("participant") User participant, Model model) {
        boolean emailExists = userService.getUserByEmail(participant.getEmail()).isPresent();
        boolean usernameExists = userService.getUserByUsername(participant.getUsername()).isPresent();

        if (emailExists || usernameExists) {
            model.addAttribute("error", "Email or username already exists!");
            model.addAttribute("participant", participant);
            return "admin/add-participant";
        }

        if (participant.getPassword() == null || participant.getPassword().isBlank()) {
            participant.setPassword("default123");
        }
        if (participant.getRole() == null || participant.getRole().isBlank()) {
            participant.setRole("USER");
        }
        participant.setApproved(true);
        userService.saveUser(participant);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/participant/{id}")
    public String viewParticipantDetails(@PathVariable Long id, Model model) {
        Optional<User> participantOpt = userService.getUserById(id);
        if (participantOpt.isEmpty()) return "redirect:/admin/dashboard";

        model.addAttribute("participant", participantOpt.get());
        return "admin/participant-details";
    }

    @PostMapping("/participant/{id}/update")
    public String updateParticipant(@PathVariable Long id, @ModelAttribute("participant") User updatedUser) {
        Optional<User> existingOpt = userService.getUserById(id);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            existing.setUsername(updatedUser.getUsername());
            existing.setEmail(updatedUser.getEmail());
            existing.setRole(updatedUser.getRole());
            userService.saveUser(existing);
        }
        return "redirect:/admin/participant/" + id;
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

    @PostMapping("/participant/{id}/reject")
    public String rejectParticipant(@PathVariable Long id) {
        userService.rejectUser(id);
        return "redirect:/admin/dashboard";
    }
}
