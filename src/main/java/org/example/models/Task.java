package org.example.models;

import java.util.Date;

public class Task {

    public enum StatusType
    {
        TODO,
        IN_PROGRESS,
        BLOCKED,
        DONE
    }

    private int taskId;
    private String title;
    private String description;
    private Date creationDate;
    private int priorityLevel;
    private StatusType status;
    private Date dueDate;
    private int projectId;

    public Task() {
        this.title = "default";
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, Date creationDate, int priorityLevel, String strStatus, Date dueDate, int projectId) {
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.priorityLevel = priorityLevel;
        switch(strStatus){
            case "todo":
                this.status = StatusType.TODO;
                break;
            case "in_progress":
                this.status = StatusType.IN_PROGRESS;
                break;
            case "blocked":
                this.status = StatusType.BLOCKED;
                break;
            case "done":
                this.status = StatusType.DONE;
                break;
            default:
                this.status = null;
        }
        this.dueDate = dueDate;
        this.projectId = projectId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(String strStatus) {
        switch(strStatus){
            case "todo":
                this.status = StatusType.TODO;
                break;
            case "in_progress":
                this.status = StatusType.IN_PROGRESS;
                break;
            case "blocked":
                this.status = StatusType.BLOCKED;
                break;
            case "done":
                this.status = StatusType.DONE;
                break;
            default:
                this.status = null;
        }
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", creationDate=" + creationDate +
                ", priorityLevel=" + priorityLevel +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", projectId=" + projectId +
                '}';
    }
}
