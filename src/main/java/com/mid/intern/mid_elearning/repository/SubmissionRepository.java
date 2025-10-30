package com.mid.intern.mid_elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;
import com.mid.intern.mid_elearning.model.User;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignment(Assignment assignment);
    List<Submission> findByUser(User user);
}
