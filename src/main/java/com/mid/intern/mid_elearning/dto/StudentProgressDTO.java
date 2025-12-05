package com.mid.intern.mid_elearning.dto;

import java.util.List;

public class StudentProgressDTO {
    private Long userId;
    private String username;
    private int submittedAssignments;
    private int totalAssignments;
    private double submissionPercentage;
    private double averageGrade;
    private boolean hasSubmissionProblem;
    private boolean hasGradeProblem;
    private List<GradeChartDataDTO> gradeHistory;
    private String progressStatus;
    private String statusColor;

    // Constructors
    public StudentProgressDTO() {
    }

    public StudentProgressDTO(Long userId, String username, int submittedAssignments, int totalAssignments, double submissionPercentage, double averageGrade, boolean hasSubmissionProblem, boolean hasGradeProblem, List<GradeChartDataDTO> gradeHistory, String progressStatus, String statusColor) {
        this.userId = userId;
        this.username = username;
        this.submittedAssignments = submittedAssignments;
        this.totalAssignments = totalAssignments;
        this.submissionPercentage = submissionPercentage;
        this.averageGrade = averageGrade;
        this.hasSubmissionProblem = hasSubmissionProblem;
        this.hasGradeProblem = hasGradeProblem;
        this.gradeHistory = gradeHistory;
        this.progressStatus = progressStatus;
        this.statusColor = statusColor;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSubmittedAssignments() {
        return submittedAssignments;
    }

    public void setSubmittedAssignments(int submittedAssignments) {
        this.submittedAssignments = submittedAssignments;
    }

    public int getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(int totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public double getSubmissionPercentage() {
        return submissionPercentage;
    }

    public void setSubmissionPercentage(double submissionPercentage) {
        this.submissionPercentage = submissionPercentage;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public boolean isHasSubmissionProblem() {
        return hasSubmissionProblem;
    }

    public void setHasSubmissionProblem(boolean hasSubmissionProblem) {
        this.hasSubmissionProblem = hasSubmissionProblem;
    }

    public boolean isHasGradeProblem() {
        return hasGradeProblem;
    }

    public void setHasGradeProblem(boolean hasGradeProblem) {
        this.hasGradeProblem = hasGradeProblem;
    }

    public List<GradeChartDataDTO> getGradeHistory() {
        return gradeHistory;
    }

    public void setGradeHistory(List<GradeChartDataDTO> gradeHistory) {
        this.gradeHistory = gradeHistory;
    }

    public String getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }
}
