package org.example.models;

import java.util.Date;

public class RecurrencePattern {
    private int id;
    private String patternType;
    private int customInterval;
    private Date startDate;
    private Date endDate;
    private String selectedDays; // e.g., "Mon,Wed,Fri"
    private int taskId;

    // Constructors
    public RecurrencePattern(int id, String patternType, int customInterval,
                             Date startDate, Date endDate, String selectedDays, int taskId) {
        this.id = id;
        this.patternType = patternType;
        this.customInterval = customInterval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedDays = selectedDays;
        this.taskId = taskId;
    }

    public RecurrencePattern(String patternType, int customInterval,
                             Date startDate, Date endDate, String selectedDays, int taskId) {
        this.patternType = patternType;
        this.customInterval = customInterval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedDays = selectedDays;
        this.taskId = taskId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getPatternType() {
        return patternType;
    }

    public int getCustomInterval() {
        return customInterval;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getSelectedDays() {
        return selectedDays;
    }

    public int getTaskId() {
        return taskId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPatternType(String patternType) {
        this.patternType = patternType;
    }

    public void setCustomInterval(int customInterval) {
        this.customInterval = customInterval;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setSelectedDays(String selectedDays) {
        this.selectedDays = selectedDays;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "RecurrencePattern{" +
                "id=" + id +
                ", patternType='" + patternType + '\'' +
                ", customInterval=" + customInterval +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", selectedDays='" + selectedDays + '\'' +
                ", taskId=" + taskId +
                '}';
    }
}
