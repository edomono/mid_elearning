package com.mid.intern.mid_elearning.dto;

import com.mid.intern.mid_elearning.model.Assignment;
import com.mid.intern.mid_elearning.model.Submission;

import java.util.Optional;

public class AssignmentWithSubmissionDTO {
    private Assignment assignment;
    private Optional<Submission> submission;

    public AssignmentWithSubmissionDTO(Assignment assignment, Optional<Submission> submission) {
        this.assignment = assignment;
        this.submission = submission;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Optional<Submission> getSubmission() {
        return submission;
    }

    public void setSubmission(Optional<Submission> submission) {
        this.submission = submission;
    }
}
