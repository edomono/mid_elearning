package com.mid.intern.mid_elearning.repository;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
    List<Submission> findByAssignmentAndUser(Assignment assignment, User user);
    List<Submission> findByUser(User user);
    Optional<Submission> findByAssignmentIdAndUserId(Long assignmentId, Long userId);
}