package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.SubjectRepository;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final AssignmentService assignmentService;

    public SubjectService(SubjectRepository subjectRepository, AssignmentService assignmentService) {
        this.subjectRepository = subjectRepository;
        this.assignmentService = assignmentService;
    }

    // =============================================================
    // 📚 GET ALL SUBJECTS
    // =============================================================
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // =============================================================
    // 📚 GET SUBJECTS BY STUDENT
    // =============================================================
    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByStudent(User student) {
        return subjectRepository.findByStudents_Id(student.getId());
    }


    // =============================================================
    // 💾 SAVE SUBJECT
    // =============================================================
    @Transactional
    public Subject saveSubject(Subject subject) {
        if (subject.getName() != null) subject.setName(subject.getName().trim());
        if (subject.getDescription() != null) subject.setDescription(subject.getDescription().trim());
        return subjectRepository.save(subject);
    }

    // =============================================================
    // ✏️ UPDATE SUBJECT
    // =============================================================
    @Transactional
    public void updateSubject(Long id, Subject updatedSubject) {
        subjectRepository.findById(id).ifPresent(existing -> {
            if (updatedSubject.getName() != null && !updatedSubject.getName().isBlank()) {
                existing.setName(updatedSubject.getName().trim());
            }
            if (updatedSubject.getDescription() != null && !updatedSubject.getDescription().isBlank()) {
                existing.setDescription(updatedSubject.getDescription().trim());
            }
            subjectRepository.save(existing);
        });
    }

    // =============================================================
    // ❌ DELETE SUBJECT
    // =============================================================
    @Transactional
    public void deleteSubjectById(Long id) {
        if (subjectRepository.existsById(id)) {
            // Delete all assignments associated with this subject first
            assignmentService.deleteAssignmentsBySubjectId(id);
            subjectRepository.deleteById(id);
        }
    }

    // =============================================================
    // 🔍 GET SUBJECT BY ID
    // =============================================================
    @Transactional(readOnly = true)
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Subject getSubjectByIdOrThrow(Long id) {
        return subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + id));
    }


    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByStudentId(Long studentId) {
        return subjectRepository.findByStudents_Id(studentId);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return subjectRepository.existsByName(name);
    }
}
