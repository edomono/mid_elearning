package com.mid.intern.mid_elearning.dto;

public class GradeChartDataDTO {
    private String assignmentTitle;
    private Double grade; // Use Double to allow for null grades (not yet graded)

    // Constructors
    public GradeChartDataDTO() {
    }

    public GradeChartDataDTO(String assignmentTitle, Double grade) {
        this.assignmentTitle = assignmentTitle;
        this.grade = grade;
    }

    // Getters and Setters
    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
