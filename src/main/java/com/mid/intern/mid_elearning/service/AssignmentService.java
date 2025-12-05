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
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.repository.AssignmentRepository;
import com.mid.intern.mid_elearning.repository.SubjectRepository;
import com.mid.intern.mid_elearning.repository.SubmissionRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final SubmissionRepository submissionRepository;

    // 📂 Lokasi upload di luar JAR agar aman saat runtime
    private final Path rootUploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "assignments");

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            SubjectRepository subjectRepository,
            SubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.submissionRepository = submissionRepository;
    }

    // =============================================================
    // 📘 GET ASSIGNMENTS
    // =============================================================
    public List<Assignment> getAssignmentsBySubject(Subject subject) {
        return assignmentRepository.findBySubject(subject);
    }

    public List<Assignment> getAssignmentsBySubjectId(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .map(assignmentRepository::findBySubject)
                .orElse(List.of());
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    // =============================================================
    // 📄 GET SUBMISSIONS BY ASSIGNMENT
    // =============================================================
    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    // =============================================================
    // 💾 SAVE ASSIGNMENT (Mentor upload tugas)
    // =============================================================
    public void saveAssignment(Long subjectId, Assignment assignment, MultipartFile file) {
        try {
            Optional<Subject> subjectOpt = subjectRepository.findById(subjectId);
            if (subjectOpt.isEmpty()) {
                System.err.println("❌ Subject not found with ID: " + subjectId);
                return;
            }

            Subject subject = subjectOpt.get();

            // Buat folder upload jika belum ada
            if (!Files.exists(rootUploadDir)) {
                Files.createDirectories(rootUploadDir);
                System.out.println("📁 Folder uploads dibuat di: " + rootUploadDir.toAbsolutePath());
            }

            // Simpan file jika ada
            if (file != null && !file.isEmpty()) {
                String original = file.getOriginalFilename();
                if (original == null || original.isBlank()) original = "upload";
                String cleanFileName = original.replaceAll("\\s+", "_");
                String fileName = System.currentTimeMillis() + "_" + cleanFileName;
                Path filePath = rootUploadDir.resolve(fileName).normalize();

                file.transferTo(filePath.toFile());

                // Simpan path relatif agar bisa diakses via browser
                assignment.setFileName(fileName);
                assignment.setFilePath("uploads/assignments/" + fileName);
            }

            assignment.setSubject(subject);
            assignmentRepository.save(assignment);

            System.out.println("✅ Assignment berhasil disimpan: " + assignment.getTitle());

        } catch (IOException e) {
            System.err.println("❌ Gagal menyimpan file assignment: " + e.getMessage());
        }
    }

    public void updateAssignment(Long assignmentId, Assignment assignmentDetails, MultipartFile file) {
        try {
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            if (assignmentOpt.isEmpty()) {
                System.err.println("❌ Assignment not found with ID: " + assignmentId);
                return;
            }

            Assignment existingAssignment = assignmentOpt.get();
            existingAssignment.setTitle(assignmentDetails.getTitle());
            existingAssignment.setDescription(assignmentDetails.getDescription());
            existingAssignment.setDueDate(assignmentDetails.getDueDate());

            // Buat folder upload jika belum ada
            if (!Files.exists(rootUploadDir)) {
                Files.createDirectories(rootUploadDir);
                System.out.println("📁 Folder uploads dibuat di: " + rootUploadDir.toAbsolutePath());
            }

            // Simpan file jika ada
            if (file != null && !file.isEmpty()) {
                String original = file.getOriginalFilename();
                if (original == null || original.isBlank()) original = "upload";
                String cleanFileName = original.replaceAll("\\s+", "_");
                String fileName = System.currentTimeMillis() + "_" + cleanFileName;
                Path filePath = rootUploadDir.resolve(fileName).normalize();

                file.transferTo(filePath.toFile());

                // Hapus file lama jika ada
                if (existingAssignment.getFilePath() != null) {
                    try {
                        Path oldFilePath = Paths.get(System.getProperty("user.dir")).resolve(existingAssignment.getFilePath()).normalize().toAbsolutePath();
                        Files.deleteIfExists(oldFilePath);
                    } catch (IOException e) {
                        System.err.println("⚠️ Gagal menghapus file lama: " + existingAssignment.getFilePath());
                    }
                }

                // Simpan path relatif agar bisa diakses via browser
                existingAssignment.setFileName(fileName);
                existingAssignment.setFilePath("uploads/assignments/" + fileName);
            }

            assignmentRepository.save(existingAssignment);

            System.out.println("✅ Assignment berhasil diperbarui: " + existingAssignment.getTitle());

        } catch (IOException e) {
            System.err.println("❌ Gagal menyimpan file assignment: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("❌ Gagal memperbarui assignment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =============================================================
    // 📝 UPDATE GRADE & COMMENT (Mentor memberi nilai)
    // =============================================================
    public void updateGradeAndComment(Long submissionId, String grade, String comment) {
        submissionRepository.findById(submissionId).ifPresent(submission -> {
            if (grade != null && !grade.isBlank()) {
                try {
                    Double gradeValue = Double.parseDouble(grade.trim());
                    submission.setGrade(gradeValue);
                } catch (NumberFormatException e) {
                    System.err.println("❌ Invalid grade format for submission " + submissionId + ": " + grade);
                }
            }
            if (comment != null && !comment.isBlank()) submission.setComment(comment.trim());
            submissionRepository.save(submission);
            System.out.println("✏️ Submission " + submissionId + " dinilai dengan grade " + grade);
        });
    }

    // =============================================================
    // 🔍 GET SUBJECT ID BY ASSIGNMENT
    // =============================================================
    public Long getAssignmentSubjectId(Long assignmentId) {
    return assignmentRepository.findById(assignmentId)
        .map(a -> (a.getSubject() != null) ? a.getSubject().getId() : null)
        .orElse(null);
    }

    // =============================================================
    // 🔎 GET ASSIGNMENT ID BY SUBMISSION
    // Used when we have a submission id and need the parent assignment id
    // =============================================================
    public Long getAssignmentIdBySubmissionId(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .map(s -> (s.getAssignment() != null) ? s.getAssignment().getId() : null)
                .orElse(null);
    }

    // =============================================================
    // ❌ DELETE ASSIGNMENT (hapus file & submissions)
    // =============================================================
    public void deleteAssignment(Long assignmentId) {
        assignmentRepository.findById(assignmentId).ifPresent(a -> {
            // Hapus file fisik jika ada
            if (a.getFilePath() != null) {
                try {
                    // Pastikan filePath relatif diubah ke absolute path
                    Path filePath = Paths.get(System.getProperty("user.dir"))
                            .resolve(a.getFilePath())
                            .normalize()
                            .toAbsolutePath();

                    Files.deleteIfExists(filePath);
                    System.out.println("🗑️ File assignment dihapus: " + filePath);
                } catch (IOException e) {
                    System.err.println("⚠️ Gagal menghapus file: " + a.getFilePath());
                }
            }

            // Hapus submissions terkait
            List<Submission> submissions = submissionRepository.findByAssignment(a);
            if (!submissions.isEmpty()) {
                submissionRepository.deleteAll(submissions);
                System.out.println("🗑️ Semua submission untuk assignment dihapus.");
            }

            // Hapus assignment
            assignmentRepository.delete(a);
            System.out.println("✅ Assignment berhasil dihapus: " + a.getTitle());
        });
    }

    // =============================================================
    // ❌ DELETE ASSIGNMENTS BY SUBJECT ID
    // =============================================================
    public void deleteAssignmentsBySubjectId(Long subjectId) {
        List<Assignment> assignments = getAssignmentsBySubjectId(subjectId);
        for (Assignment assignment : assignments) {
            deleteAssignment(assignment.getId());
        }
    }

    // =============================================================
    // 🧱 ADD (optional direct creation)
    // =============================================================
    public Assignment addAssignment(Assignment assignment, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            if (!Files.exists(rootUploadDir)) Files.createDirectories(rootUploadDir);

            String original = file.getOriginalFilename();
            if (original == null || original.isBlank()) original = "upload";
            String cleanFileName = original.replaceAll("\\s+", "_");
            String fileName = System.currentTimeMillis() + "_" + cleanFileName;
            Path filePath = rootUploadDir.resolve(fileName).normalize();

            file.transferTo(filePath.toFile());

            assignment.setFileName(fileName);
            assignment.setFilePath("uploads/assignments/" + fileName);
        }

        return assignmentRepository.save(assignment);
    }
}
