package com.mid.intern.mid_elearning.controller;

import com.mid.intern.mid_elearning.dto.StudentProgressDTO;
import com.mid.intern.mid_elearning.service.StudentProgressService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentProgressApiController {

    private final StudentProgressService studentProgressService;

    public AdminStudentProgressApiController(StudentProgressService studentProgressService) {
        this.studentProgressService = studentProgressService;
    }

    @GetMapping("/courses/{courseId}/student-progress")
    public List<StudentProgressDTO> getStudentProgress(@PathVariable Long courseId) {
        return studentProgressService.getStudentProgressForCourse(courseId);
    }
}
