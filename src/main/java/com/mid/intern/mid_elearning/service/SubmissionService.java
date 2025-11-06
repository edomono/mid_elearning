package com.mid.intern.mid_elearning.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    // ✅ Find submission by assignment + student
    public Optional<Submission> getSubmissionByAssignmentAndStudent(Assignment assignment, User student) {
        return submissionRepository.findByAssignmentAndUser(assignment, student);
    }

    // ✅ Submit assignment (first time submit)
    public Submission submitAssignment(User user, Assignment assignment, MultipartFile file, String note) {
        try {
            String folder = "uploads/submissions/" + user.getId();
            Files.createDirectories(Paths.get(folder));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(folder, fileName);
            Files.write(filePath, file.getBytes());

            Submission s = new Submission();
            s.setUser(user);
            s.setAssignment(assignment);
            s.setFileName(fileName);
            s.setFilePath(filePath.toString());
            s.setUploadTime(LocalDateTime.now());
            s.setComment(note);
            s.setGrade(null);

            return submissionRepository.save(s);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // ✅ Update assignment submission (resubmit)
    public Submission updateSubmission(Long submissionId, User user, MultipartFile file, String note) {
        try {
            Submission submission = submissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));

            String folder = "uploads/submissions/" + user.getId();
            Files.createDirectories(Paths.get(folder));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(folder, fileName);
            Files.write(filePath, file.getBytes());

            submission.setFileName(fileName);
            submission.setFilePath(filePath.toString());
            submission.setUploadTime(LocalDateTime.now());
            submission.setComment(note);

            return submissionRepository.save(submission);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    // ✅ Get all by assignment
    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    // ✅ Get assignment ID from submission
    public Long getAssignmentIdBySubmission(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .map(s -> s.getAssignment().getId())
                .orElse(null);
    }

    // ✅ Update grade & comment (admin grading)
    public void updateGradeAndComment(Long submissionId, String grade, String comment) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setGrade(grade);
        submission.setComment(comment);
        submissionRepository.save(submission);
    }

    public List<Submission> getSubmissionsByUser(User user) {
        return submissionRepository.findByUser(user);
}

}
