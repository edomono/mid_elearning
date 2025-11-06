package com.mid.intern.mid_elearning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mid.intern.mid_elearning.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);

    // ✅ ambil subject berdasarkan id student
    List<Subject> findByStudents_Id(Long studentId);
}
