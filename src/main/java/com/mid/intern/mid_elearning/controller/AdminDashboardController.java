package com.mid.intern.mid_elearning.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final SubjectService subjectService;
    private final UserService userService;

    public AdminDashboardController(SubjectService subjectService, UserService userService) {
        this.subjectService = subjectService;
        this.userService = userService;
    }

    // =============================================================
    // 🏠 DASHBOARD UTAMA
    // =============================================================
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("participants", userService.getAllUsers());
        return "admin/dashboard";
    }

    // =============================================================
    // 📘 COURSE MANAGEMENT
    // =============================================================

    // 🔹 Form tambah course
    @GetMapping({"/add-course", "/course/add"})
    public String showAddCoursePage(Model model) {
        model.addAttribute("subject", new Subject());
        return "admin/add-course";
    }

    // 🔹 Simpan course baru
    @PostMapping({"/add-course", "/course/add"})
    public String addCourse(@ModelAttribute("subject") Subject subject, Model model) {
        // Trim input
        if (subject.getName() != null) subject.setName(subject.getName().trim());
        if (subject.getDescription() != null) subject.setDescription(subject.getDescription().trim());

        // Cek duplikasi nama course
        boolean exists = subjectService.getAllSubjects().stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(subject.getName()));

        if (exists) {
            model.addAttribute("error", "Course name already exists!");
            model.addAttribute("subject", subject);
            return "admin/add-course";
        }

        // Generate kode otomatis
        long count = subjectService.getAllSubjects().size() + 1;
        subject.setCode(String.format("C%03d", count));

        // Simpan ke database
        subjectService.saveSubject(subject);
        return "redirect:/admin/dashboard";
    }

    // 🔹 Lihat detail course
    @GetMapping("/course/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("subject", subjectOpt.get());
        return "admin/course-details";
    }

    // 🔹 Update course
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

    // 🔹 Hapus course
    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id) {
        subjectService.deleteSubjectById(id);
        return "redirect:/admin/dashboard";
    }

    // =============================================================
    // 👥 PARTICIPANT MANAGEMENT
    // =============================================================

    // 🔹 Form tambah participant
    @GetMapping({"/add-participant", "/participant/add"})
    public String showAddParticipantForm(Model model) {
        model.addAttribute("participant", new User());
        return "admin/add-participant";
    }

    // 🔹 Simpan participant baru
    @PostMapping({"/add-participant", "/participant/add"})
    public String addParticipant(@ModelAttribute("participant") User participant, Model model) {
        if (participant.getUsername() != null) participant.setUsername(participant.getUsername().trim());
        if (participant.getEmail() != null) participant.setEmail(participant.getEmail().trim());

        // Cek duplikasi email
        if (userService.getUserByEmail(participant.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            model.addAttribute("participant", participant);
            return "admin/add-participant";
        }

        // Cek duplikasi username
        if (userService.getUserByUsername(participant.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            model.addAttribute("participant", participant);
            return "admin/add-participant";
        }

        // Password default bila kosong
        if (participant.getPassword() == null || participant.getPassword().isBlank()) {
            participant.setPassword("default123");
        }

        // Role default USER bila kosong
        if (participant.getRole() == null || participant.getRole().isBlank()) {
            participant.setRole("USER");
        }

        userService.saveUser(participant);
        return "redirect:/admin/dashboard";
    }

    // 🔹 Lihat detail participant
    @GetMapping("/participant/{id}")
    public String viewParticipantDetails(@PathVariable Long id, Model model) {
        Optional<User> participantOpt = userService.getUserById(id);
        if (participantOpt.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("participant", participantOpt.get());
        return "admin/participant-details";
    }

    // 🔹 Update participant
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

    // 🔹 Hapus participant (unified, hanya satu mapping)
    @PostMapping("/participant/{id}/delete")
    public String deleteParticipant(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }
}
