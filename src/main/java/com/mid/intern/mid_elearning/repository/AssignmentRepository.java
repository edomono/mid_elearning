package com.mid.intern.mid_elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findBySubject(Subject subject);
}
