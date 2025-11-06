package com.mid.intern.mid_elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // ✅ Find all submissions by a student
    List<Submission> findByUser(User user);

    // ✅ Find all submissions for an assignment
    List<Submission> findByAssignment(Assignment assignment);

    // ✅ Find submission of a student for specific assignment
    Optional<Submission> findByAssignmentAndUser(Assignment assignment, User user);
}
