package com.mid.intern.mid_elearning.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;


import org.springframework.http.HttpStatus;
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
import com.mid.intern.mid_elearning.service.UserService;

import com.mid.intern.mid_elearning.dto.ParticipantRegistrationDto;
import com.mid.intern.mid_elearning.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;


@Controller
@RequestMapping("/mentor")
public class MentorDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(MentorDashboardController.class);

    private final SubjectService subjectService;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final AssignmentService assignmentService;
    private final com.mid.intern.mid_elearning.service.ForumService forumService;
    private final UserRepository userRepository; // 💉 Inject UserRepository

    public MentorDashboardController(
            SubjectService subjectService,
            UserService userService,
            AnnouncementService announcementService,
            AssignmentService assignmentService,
            com.mid.intern.mid_elearning.service.ForumService forumService,
            UserRepository userRepository) { // 💉 Inject UserRepository
        this.subjectService = subjectService;
        this.userService = userService;
        this.announcementService = announcementService;
        this.assignmentService = assignmentService;
        this.forumService = forumService;
        this.userRepository = userRepository; // 💉 Assign UserRepository
    }
    
    // =============================================================
    // 👥 PARTICIPANT MANAGEMENT
    // =============================================================
    @PostMapping("/participant/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addParticipant(
            @Valid @ModelAttribute("participant") ParticipantRegistrationDto participantDto,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .findFirst()
                    .orElse("Invalid data provided.");
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", errorMessage));
        }

        if (userRepository.findByUsername(participantDto.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Username already exists!"));
        }

        if (userRepository.findByEmail(participantDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Email already exists!"));
        }

        try {
            // By default, a mentor can only add students (USER role)
            // or other mentors. Force role to USER if not specified or for security.
            if (participantDto.getRole() == null || participantDto.getRole().isEmpty()) {
                participantDto.setRole("USER");
            }
            
            userService.registerParticipant(participantDto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Participant added successfully!"));
        } catch (Exception e) {
            logger.error("Error adding participant from mentor dashboard: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "An unexpected error occurred: " + e.getMessage()));
        }
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
    model.addAttribute("discussions", forumService.getAllDiscussions());
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
    @ResponseBody
    public ResponseEntity<?> addCourse(@ModelAttribute Subject subject) {
        if (subject.getName() != null) subject.setName(subject.getName().trim());
        if (subject.getDescription() != null) subject.setDescription(subject.getDescription().trim());

        boolean exists = subjectService.getAllSubjects().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(subject.getName()));

        if (exists) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Course name already exists!"));
        }

        long count = subjectService.getAllSubjects().size() + 1;
        subject.setCode(String.format("C%03d", count));
        subjectService.saveSubject(subject);
        
        return ResponseEntity.ok(Map.of("status", "success", "message", "Course added successfully!"));
    }

    @GetMapping("/course/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) return "redirect:/mentor/dashboard";

        Subject subject = subjectOpt.get();
        List<Assignment> assignments = assignmentService.getAssignmentsBySubjectId(id);

        model.addAttribute("subject", subject);
        model.addAttribute("assignments", assignments);
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        model.addAttribute("discussions", forumService.getAllDiscussions());
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
}