package com.mid.intern.mid_elearning.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.ForumService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.UserService;

@Controller
@RequestMapping("/admin/course")
public class AdminCourseController {

    private final SubjectService subjectService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final AnnouncementService announcementService;
    private final ForumService forumService;

    public AdminCourseController(SubjectService subjectService, AssignmentService assignmentService, UserService userService, AnnouncementService announcementService, ForumService forumService) {
        this.subjectService = subjectService;
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.announcementService = announcementService;
        this.forumService = forumService;
    }

    @GetMapping("/{id}")
    public String viewCourseDetails(@PathVariable("id") Long id, Model model) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(id);
        if (subjectOpt.isEmpty()) return "redirect:/admin/dashboard";

        Subject subject = subjectOpt.get();
        List<Assignment> assignments = assignmentService.getAssignmentsBySubjectId(id);

        model.addAttribute("subject", subject);
        model.addAttribute("assignments", assignments);
        model.addAttribute("newAssignment", new Assignment());
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        model.addAttribute("discussions", forumService.getAllDiscussions());

        return "admin/course-details";
    }

    @PostMapping("/{id}/update")
    public String updateCourse(@PathVariable("id") Long id, @ModelAttribute("subject") Subject subject) {
        Optional<Subject> existing = subjectService.getSubjectById(id);
        if (existing.isPresent()) {
            Subject s = existing.get();
            s.setName(subject.getName());
            s.setDescription(subject.getDescription());
            subjectService.saveSubject(s);
        }
        return "redirect:/admin/course/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable("id") Long id) {
        subjectService.deleteSubjectById(id);
        return "redirect:/admin/dashboard";
    }
}
