package com.mid.intern.mid_elearning.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.SubmissionService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/mentor")
public class MentorDashboardController {

    private final SubjectService subjectService;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public MentorDashboardController(
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
        List<User> students = userService.getAllUsers().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("students", students);
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        return "mentor/dashboard";
    }

    // =============================================================
    // 📢 ANNOUNCEMENTS
    // =============================================================
    @PostMapping("/announcement/add")
    public String addAnnouncement(@RequestParam("content") String content) {
        if (content != null && !content.trim().isEmpty()) {
            Announcement a = new Announcement();
            a.setContent(content.trim());
            a.setUsername("Mentor");
            a.setRole("Mentor");
            announcementService.saveAnnouncement(a);
        }
        return "redirect:/mentor/dashboard";
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
    // 📘 COURSE MANAGEMENT
    // =============================================================
    @GetMapping({"/add-course", "/course/add"})
    public String showAddCoursePage(Model model) {
        model.addAttribute("subject", new Subject());
        return "mentor/add-course";
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
            return "mentor/add-course";
        }

        long count = subjectService.getAllSubjects().size() + 1;
        subject.setCode(String.format("C%03d", count));
        subjectService.saveSubject(subject);
        return "redirect:/mentor/dashboard";
    }

    @GetMapping("/course/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) return "redirect:/mentor/dashboard";

        Subject subject = subjectOpt.get();
        List<Assignment> assignments = assignmentService.getAssignmentsBySubjectId(id);

        model.addAttribute("subject", subject);
        model.addAttribute("assignments", assignments);
        return "mentor/course-details";
    }

    @PostMapping("/course/{id}/update")
    public String updateCourse(@PathVariable("id") Long id, @ModelAttribute("subject") Subject subject) {
        subjectService.getSubjectById(id).ifPresent(existing -> {
            existing.setName(subject.getName());
            existing.setDescription(subject.getDescription());
            subjectService.saveSubject(existing);
        });
        return "redirect:/mentor/course/" + id;
    }

    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id) {
        subjectService.deleteSubjectById(id);
        return "redirect:/mentor/dashboard";
    }

    // =============================================================
    // 👥 PARTICIPANTS
    // =============================================================
    @GetMapping("/participants")
    public String viewParticipants(Model model) {
        List<User> students = userService.getAllUsers().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());

        model.addAttribute("students", students);
        return "mentor/participants";
    }

    @GetMapping("/participants/{id}")
    public String viewParticipantDetails(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) return "redirect:/mentor/participants";

        User student = userOpt.get();
        model.addAttribute("student", student);
        model.addAttribute("submissions", submissionService.getSubmissionsByUser(student));
        return "mentor/participant-details";
    }
}
