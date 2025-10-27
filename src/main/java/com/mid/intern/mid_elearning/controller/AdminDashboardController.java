package com.mid.intern.mid_elearning.controller;

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

import com.mid.intern.mid_elearning.model.Announcement;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final SubjectService subjectService;
    private final UserService userService;
    private final AnnouncementService announcementService;

    public AdminDashboardController(SubjectService subjectService,
                                    UserService userService,
                                    AnnouncementService announcementService) {
        this.subjectService = subjectService;
        this.userService = userService;
        this.announcementService = announcementService;
    }

    // =============================================================
    // 🏠 DASHBOARD
    // =============================================================
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("participants", userService.getAllUsers()); // sudah approved
        model.addAttribute("waitingApprovals", userService.getPendingUsers()); // belum approved
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
    // 📘 COURSE MANAGEMENT
    // =============================================================

    // Accept both /add-course and /course/add for GET
    @GetMapping({"/add-course", "/course/add"})
    public String showAddCoursePage(Model model) {
        model.addAttribute("subject", new Subject());
        return "admin/add-course";
    }

    // Accept both /add-course and /course/add for POST
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

    @GetMapping("/course/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("subject", subjectOpt.get());
        return "admin/course-details";
    }

    @PostMapping("/course/{id}/update")
    public String updateCourse(@PathVariable("id") Long id, @ModelAttribute("subject") Subject subject) {
        Optional<Subject> existingOpt = subjectService.getSubjectById(id);
        if (existingOpt.isPresent()) {
            Subject existing = existingOpt.get();
            existing.setName(subject.getName());
            existing.setDescription(subject.getDescription());
            subjectService.saveSubject(existing);
        }
        return "redirect:/admin/course/" + id;
    }

    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id) {
        subjectService.deleteSubjectById(id);
        return "redirect:/admin/dashboard";
    }

    // =============================================================
    // 👥 PARTICIPANT MANAGEMENT
    // =============================================================

    // Accept both /add-participant and /participant/add for GET
    @GetMapping({"/add-participant", "/participant/add"})
    public String showAddParticipantForm(Model model) {
        model.addAttribute("participant", new User());
        return "admin/add-participant";
    }

    // Accept both /add-participant and /participant/add for POST
    @PostMapping({"/add-participant", "/participant/add"})
    public String addParticipant(@ModelAttribute("participant") User participant, Model model) {
        if (participant.getUsername() != null) participant.setUsername(participant.getUsername().trim());
        if (participant.getEmail() != null) participant.setEmail(participant.getEmail().trim());

        if (userService.getUserByEmail(participant.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            model.addAttribute("participant", participant);
            return "admin/add-participant";
        }

        if (userService.getUserByUsername(participant.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            model.addAttribute("participant", participant);
            return "admin/add-participant";
        }

        if (participant.getPassword() == null || participant.getPassword().isBlank()) {
            participant.setPassword("default123");
        }

        if (participant.getRole() == null || participant.getRole().isBlank()) {
            participant.setRole("USER");
        }

        participant.setApproved(true); // Admin menambahkan langsung aktif
        userService.saveUser(participant);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/participant/{id}")
    public String viewParticipantDetails(@PathVariable Long id, Model model) {
        Optional<User> participantOpt = userService.getUserById(id);
        if (participantOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

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

    // =============================================================
    // ✅ APPROVAL MANAGEMENT (FITUR BARU)
    // =============================================================
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
