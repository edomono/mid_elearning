package com.mid.intern.mid_elearning.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.SubmissionRepository;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final String uploadDir = "uploads/submissions/";

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    // =============================================================
    // 📚 GET SUBMISSIONS
    // =============================================================
    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    // 🔹 Tambahan baru: get submissions by user (untuk student/mentor dashboard)
    public List<Submission> getSubmissionsByUser(User user) {
        return submissionRepository.findByUser(user);
    }

    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    // =============================================================
    // 💾 SAVE SUBMISSION (UPLOAD)
    // =============================================================
    public Submission saveSubmission(Assignment assignment, User user, MultipartFile file) throws IOException {
        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setUser(user);

        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            submission.setFileName(fileName);
            submission.setFilePath(filePath.toString());
        }

        return submissionRepository.save(submission);
    }

    // =============================================================
    // 📝 UPDATE GRADE & COMMENT
    // =============================================================
    public void updateGradeAndComment(Long submissionId, String grade, String comment) {
        submissionRepository.findById(submissionId).ifPresent(submission -> {
            if (grade != null && !grade.isBlank()) submission.setGrade(grade);
            if (comment != null && !comment.isBlank()) submission.setComment(comment);
            submissionRepository.save(submission);
        });
    }

    // =============================================================
    // 🔍 GET ASSIGNMENT ID BY SUBMISSION
    // =============================================================
    public Long getAssignmentIdBySubmission(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .map(sub -> sub.getAssignment().getId())
                .orElse(null);
    }

    // =============================================================
    // ❌ DELETE SUBMISSION
    // =============================================================
    public void deleteSubmission(Long id) {
        Optional<Submission> opt = submissionRepository.findById(id);
        if (opt.isEmpty()) return;

        Submission s = opt.get();
        if (s.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(s.getFilePath()));
            } catch (IOException e) {
                System.err.println("⚠️ Gagal menghapus file: " + e.getMessage());
            }
        }
        submissionRepository.delete(s);
    }
}
