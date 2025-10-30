package com.mid.intern.mid_elearning.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.service.AssignmentService;
import com.mid.intern.mid_elearning.service.SubjectService;

@Controller
@RequestMapping("/mentor")
public class MentorAssignmentController {

    private final AssignmentService assignmentService;
    private final SubjectService subjectService;

    public MentorAssignmentController(AssignmentService assignmentService, SubjectService subjectService) {
        this.assignmentService = assignmentService;
        this.subjectService = subjectService;
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
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", assignmentService.getSubmissionsByAssignment(assignment));

        return "mentor/assignment-details";
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

        // Simpan ke database + upload file (kalau ada)
        assignmentService.saveAssignment(subject.getId(), assignment, file);

        System.out.println("✅ Assignment berhasil ditambahkan: " + title);
        return "redirect:/mentor/course/" + courseId;
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
        return "redirect:/mentor/assignment/" + assignmentService.getAssignmentSubjectId(submissionId);
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
