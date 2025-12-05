package com.mid.intern.mid_elearning.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mid.intern.mid_elearning.dto.GradeChartDataDTO;
import com.mid.intern.mid_elearning.dto.StudentProgressDTO;
import com.mid.intern.mid_elearning.model.Assignment; // Import UserRepository
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.AssignmentRepository;
import com.mid.intern.mid_elearning.repository.SubjectRepository;
import com.mid.intern.mid_elearning.repository.SubmissionRepository;
import com.mid.intern.mid_elearning.repository.UserRepository;

@Service
public class StudentProgressService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    @Transactional(readOnly = true)
    public List<StudentProgressDTO> getStudentProgressForCourse(Long courseId) {
        // Step 1: Find the course. Subject is still needed to get course assignments.
        Optional<Subject> subjectOptional = subjectRepository.findById(courseId);
        if (subjectOptional.isEmpty()) {
            return Collections.emptyList(); // Return empty list if course not found
        }
        Subject course = subjectOptional.get();

        // Get all users with the 'USER' role, as all students are now considered to be in all subjects
        List<User> students = userRepository.findByRole("USER"); 

        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        List<Assignment> courseAssignments = assignmentRepository.findBySubjectId(courseId);
        List<StudentProgressDTO> studentProgressList = new ArrayList<>();

        for (User student : students) {
            int submittedAssignments = 0;
            int totalAssignments = courseAssignments.size();
            double totalGrades = 0;
            int gradedAssignmentsCount = 0;
            List<GradeChartDataDTO> gradeHistory = new ArrayList<>();

            for (Assignment assignment : courseAssignments) {
                Optional<Submission> submissionOptional = submissionRepository.findByAssignmentIdAndUserId(assignment.getId(), student.getId());

                if (submissionOptional.isPresent()) {
                    submittedAssignments++;
                    Submission submission = submissionOptional.get();
                    if (submission.getGrade() != null) {
                        totalGrades += submission.getGrade();
                        gradedAssignmentsCount++;
                        gradeHistory.add(new GradeChartDataDTO(assignment.getTitle(), submission.getGrade()));
                    }
                }
            }

            double submissionPercentage = (totalAssignments > 0) ? ((double) submittedAssignments / totalAssignments) * 100 : 100;
            double averageGrade = (gradedAssignmentsCount > 0) ? totalGrades / gradedAssignmentsCount : 0;

            // Problem flagging logic:
            // A student has a submission problem if their submission rate is below 50%.
            // A student has a grade problem if their average grade is below 75.
            boolean hasSubmissionProblem = submissionPercentage < 50;
            boolean hasGradeProblem = averageGrade < 75;

            // New logic for progress status
            String progressStatus;
            String statusColor;
            if (averageGrade > 75) {
                progressStatus = "Bagus";
                statusColor = "green";
            } else if (averageGrade >= 65) {
                progressStatus = "Butuh Bimbingan Lebih";
                statusColor = "yellow";
            } else {
                progressStatus = "Sudah Gawat";
                statusColor = "red";
            }

            studentProgressList.add(new StudentProgressDTO(
                    student.getId(),
                    student.getUsername(),
                    submittedAssignments,
                    totalAssignments,
                    submissionPercentage,
                    averageGrade,
                    hasSubmissionProblem,
                    hasGradeProblem,
                    gradeHistory,
                    progressStatus,
                    statusColor
            ));
        }

        return studentProgressList;
    }
}
