package com.mid.intern.mid_elearning.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubjectService;
import com.mid.intern.mid_elearning.service.AnnouncementService;
import com.mid.intern.mid_elearning.service.ForumService;

@Controller
@RequestMapping("/mentor")
public class MentorAssignmentController {

    private final AssignmentService assignmentService;
    private final SubjectService subjectService;
    private final AnnouncementService announcementService;
    private final ForumService forumService;

    public MentorAssignmentController(AssignmentService assignmentService, SubjectService subjectService, AnnouncementService announcementService, ForumService forumService) {
        this.assignmentService = assignmentService;
        this.subjectService = subjectService;
        this.announcementService = announcementService;
        this.forumService = forumService;
    }

    // ==============================================================
    // 📘 DETAIL ASSIGNMENT (lihat submissions student)
    // ==============================================================
    @GetMapping("/assignment/{id}")
    public String getAssignmentDetail(@PathVariable Long id, Model model) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(id);
        if (assignmentOpt.isEmpty()) {
            System.err.println("⚠️ Assignment not found with ID: " + id);
            return "redirect:/mentor/dashboard";
        }

        Assignment assignment = assignmentOpt.get();
        Optional<Subject> subjectOpt = subjectService.getSubjectById(assignment.getSubject().getId());
        if (subjectOpt.isEmpty()) {
            System.err.println("⚠️ Subject not found for assignment ID: " + id);
            return "redirect:/mentor/dashboard";
        }
        Subject subject = subjectOpt.get();

        model.addAttribute("assignment", Optional.of(assignment));
        model.addAttribute("submissions", assignmentService.getSubmissionsByAssignment(assignment));
        model.addAttribute("announcements", announcementService.getAllAnnouncementsSorted());
        model.addAttribute("discussions", forumService.getAllDiscussions());
        model.addAttribute("subject", subject);

        return "mentor/assignment-details";
    }

    @GetMapping("/assignment/{id}/details")
    @ResponseBody
    public ResponseEntity<Assignment> getAssignmentDetailsJson(@PathVariable Long id) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(id);
        if (assignmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Assignment assignment = assignmentOpt.get();
        assignment.getSubmissions().size(); 
        return ResponseEntity.ok(assignment);
    }

    // ==============================================================
    // 💾 TAMBAH ASSIGNMENT (upload file)
    // ==============================================================
    @PostMapping("/course/{courseId}/assignments/add")
    public String addAssignment(
            @PathVariable("courseId") Long courseId,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
            Model model
    ) {
        Optional<Subject> subjectOpt = subjectService.getSubjectById(courseId);
        if (subjectOpt.isEmpty()) {
            System.err.println("❌ Subject not found with ID: " + courseId);
            return "redirect:/mentor/dashboard";
        }

        Subject subject = subjectOpt.get();

        // Buat entity assignment baru
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDueDate(dueDate);

        // Simpan ke database + upload file (kalau ada)
        assignmentService.saveAssignment(subject.getId(), assignment, file);

        System.out.println("✅ Assignment berhasil ditambahkan: " + title);
        return "redirect:/mentor/course/" + courseId;
    }

    @GetMapping("/assignment/{id}/edit")
    public String showEditAssignmentForm(@PathVariable("id") Long assignmentId, Model model) {
        Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
        if (assignmentOpt.isEmpty()) {
            return "redirect:/mentor/dashboard";
        }

        model.addAttribute("assignment", assignmentOpt.get());
        return "mentor/edit-assignment";
    }

    @PostMapping("/assignment/{id}/edit")
    public String editAssignment(@PathVariable("id") Long assignmentId,
                               @ModelAttribute("assignment") Assignment assignment,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               @RequestParam("dueDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {

        assignment.setDueDate(dueDate);
        assignmentService.updateAssignment(assignmentId, assignment, file);
        return "redirect:/mentor/assignment/" + assignmentId;
    }

    // ==============================================================
    // 📝 UPDATE GRADE (mentor memberi nilai student)
    // ==============================================================
    @PostMapping("/submission/{submissionId}/grade")
    public String gradeSubmission(
            @PathVariable("submissionId") Long submissionId,
            @RequestParam("grade") String grade,
            @RequestParam(value = "comment", required = false) String comment
    ) {
        assignmentService.updateGradeAndComment(submissionId, grade, comment);
        System.out.println("✅ Submission " + submissionId + " dinilai: " + grade);
        // after grading, redirect to the assignment details page for this submission's assignment
        Long assignmentId = assignmentService.getAssignmentIdBySubmissionId(submissionId);
        if (assignmentId == null) {
            // fallback to mentor dashboard if mapping failed
            System.err.println("⚠️ Unable to determine assignment for submission: " + submissionId);
            return "redirect:/mentor/dashboard";
        }
        return "redirect:/mentor/assignment/" + assignmentId;
    }

    // ==============================================================
    // ❌ DELETE ASSIGNMENT (hapus file & submissions)
    // ==============================================================
    @PostMapping("/assignment/{id}/delete")
    public String deleteAssignment(@PathVariable Long id) {
        Long subjectId = assignmentService.getAssignmentSubjectId(id);
        if (subjectId == null) {
            System.err.println("⚠️ Subject ID not found for assignment " + id);
            return "redirect:/mentor/dashboard";
        }

        assignmentService.deleteAssignment(id);
        System.out.println("🗑️ Assignment " + id + " berhasil dihapus");

        return "redirect:/mentor/course/" + subjectId;
    }
}
