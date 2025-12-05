package com.mid.intern.mid_elearning.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Subject;
import com.mid.intern.mid_elearning.repository.SubjectRepository;
import com.mid.intern.mid_elearning.service.AssignmentService;

@Controller
@RequestMapping("/admin/course/{subjectId}/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final SubjectRepository subjectRepository;

    public AssignmentController(AssignmentService assignmentService, SubjectRepository subjectRepository) {
        this.assignmentService = assignmentService;
        this.subjectRepository = subjectRepository;
    }

    @PostMapping("/add")
    public String addAssignment(@PathVariable Long subjectId,
                                @ModelAttribute Assignment assignment,
                                @RequestParam("file") MultipartFile file) throws IOException {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow();
        assignment.setSubject(subject);
        assignmentService.addAssignment(assignment, file);
        return "redirect:/admin/course/" + subjectId;
    }


}
