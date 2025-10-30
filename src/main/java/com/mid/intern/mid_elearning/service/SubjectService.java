package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.repository.SubjectRepository;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    // =============================================================
    // 📚 GET ALL SUBJECTS
    // =============================================================
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
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
}
