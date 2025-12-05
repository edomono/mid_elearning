package com.mid.intern.mid_elearning.service;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    private final Path root = Paths.get("uploads/submissions");

    public SubmissionService() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload");
        }
    }

    public Submission saveSubmission(MultipartFile file, Assignment assignment, User user) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(fileName));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFileName(fileName);
        submission.setFilePath(this.root.resolve(fileName).toString());
        submission.setUploadTime(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public void gradeSubmission(Long submissionId, String grade, String comment) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        if (grade != null && !grade.trim().isEmpty()) {
            try {
                submission.setGrade(Double.parseDouble(grade.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Invalid grade format: " + grade);
            }
        }
        submission.setComment(comment);
        submissionRepository.save(submission);
    }

    public Long getAssignmentIdBySubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        return submission.getAssignment().getId();
    }

    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public void updateGradeAndComment(Long submissionId, String grade, String comment) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
        if (grade != null && !grade.isBlank()) {
            try {
                Double gradeValue = Double.parseDouble(grade.trim());
                submission.setGrade(gradeValue);
            } catch (NumberFormatException e) {
                System.err.println("❌ Invalid grade format for submission " + submissionId + ": " + grade);
            }
        }
        submission.setComment(comment);
        submissionRepository.save(submission);
    }

    public java.util.Optional<Submission> getSubmissionByAssignmentAndStudent(Assignment assignment, User user) {
        List<Submission> submissions = submissionRepository.findByAssignmentAndUser(assignment, user);
        if (submissions.isEmpty()) {
            return Optional.empty();
        }
        // Sort by upload time in descending order and return the latest
        submissions.sort((s1, s2) -> s2.getUploadTime().compareTo(s1.getUploadTime()));
        return Optional.of(submissions.get(0));
    }

    public void submitAssignment(User user, Assignment assignment, MultipartFile file, String note) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(fileName));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setUser(user);
        submission.setFileName(fileName);
        submission.setFilePath(this.root.resolve(fileName).toString());
        submission.setUploadTime(LocalDateTime.now());
        submission.setComment(note);
        submissionRepository.save(submission);
    }

    public void updateSubmission(Long submissionId, User user, MultipartFile file, String note) throws IOException {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (!submission.getUser().equals(user)) {
            throw new AccessDeniedException("User is not authorized to update this submission");
        }

        // Delete old file if it exists
        if (submission.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(submission.getFilePath()));
            } catch (IOException e) {
                // Log the error but don't prevent the update
                System.err.println("Could not delete old submission file: " + submission.getFilePath() + " - " + e.getMessage());
            }
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), this.root.resolve(fileName));

        submission.setFileName(fileName);
        submission.setFilePath(this.root.resolve(fileName).toString());
        submission.setUploadTime(LocalDateTime.now());
        submission.setComment(note);
        submissionRepository.save(submission);
    }

    public void deleteSubmission(Long submissionId, User user) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + submissionId));

        if (!submission.getUser().equals(user)) {
            throw new AccessDeniedException("User is not authorized to delete this submission.");
        }

        // Delete the file from the file system
        if (submission.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(submission.getFilePath()));
            } catch (IOException e) {
                // Log the error but don't prevent the deletion of the database record
                System.err.println("Could not delete submission file: " + submission.getFilePath() + " - " + e.getMessage());
            }
        }

        // Delete the submission record from the database
        submissionRepository.delete(submission);
    }

    public void deleteSubmissionById(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + submissionId));

        // Delete the file from the file system
        if (submission.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(submission.getFilePath()));
            } catch (IOException e) {
                // Log the error but don't prevent the deletion of the database record
                System.err.println("Could not delete submission file: " + submission.getFilePath() + " - " + e.getMessage());
            }
        }

        // Delete the submission record from the database
        submissionRepository.delete(submission);
    }
}