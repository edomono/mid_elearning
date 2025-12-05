package com.mid.intern.mid_elearning;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension; // Import UserRepository

import com.mid.intern.mid_elearning.dto.StudentProgressDTO;
import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.AssignmentRepository;
import com.mid.intern.mid_elearning.repository.SubjectRepository;
import com.mid.intern.mid_elearning.repository.SubmissionRepository;
import com.mid.intern.mid_elearning.repository.UserRepository;
import com.mid.intern.mid_elearning.service.StudentProgressService;

@ExtendWith(MockitoExtension.class)
public class StudentProgressServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository; // Mock UserRepository

    @InjectMocks
    private StudentProgressService studentProgressService;

    @Test
    public void testGetStudentProgressForCourse_shouldOnlyReturnGradedAssignmentsInChart() {
        // Given
        long courseId = 1L;
        long studentId = 1L; // Representing a 'USER' role user

        Subject subject = new Subject();
        subject.setId(courseId);
        // subject.setStudents(Set.of(student)); // Removed: No longer set students directly on Subject

        User student = new User();
        student.setId(studentId);
        student.setRole("USER"); // Role should be 'USER'

        Assignment assignment1 = new Assignment();
        assignment1.setId(1L);
        assignment1.setTitle("Assignment 1");
        assignment1.setSubject(subject);

        Assignment assignment2 = new Assignment();
        assignment2.setId(2L);
        assignment2.setTitle("Assignment 2");
        assignment2.setSubject(subject);

        Submission submission1 = new Submission();
        submission1.setAssignment(assignment1);
        submission1.setUser(student);
        submission1.setGrade(90.0);

        when(subjectRepository.findById(courseId)).thenReturn(Optional.of(subject));
        when(userRepository.findByRole("USER")).thenReturn(List.of(student)); // Mock findByRole("USER")
        when(assignmentRepository.findBySubjectId(courseId)).thenReturn(List.of(assignment1, assignment2));
        when(submissionRepository.findByAssignmentIdAndUserId(assignment1.getId(), studentId)).thenReturn(Optional.of(submission1));
        when(submissionRepository.findByAssignmentIdAndUserId(assignment2.getId(), studentId)).thenReturn(Optional.empty());

        // When
        List<StudentProgressDTO> studentProgress = studentProgressService.getStudentProgressForCourse(courseId);

        // Then
        assertEquals(1, studentProgress.size());
        StudentProgressDTO progressDTO = studentProgress.get(0);
        assertEquals(1, progressDTO.getGradeHistory().size());
        assertEquals("Assignment 1", progressDTO.getGradeHistory().get(0).getAssignmentTitle());
        assertEquals(90.0, progressDTO.getGradeHistory().get(0).getGrade());
    }
}
