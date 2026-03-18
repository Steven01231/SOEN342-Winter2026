package org.example.models;

public class Subtask {
    private int id;
    private String title;
    private String status;
    private int taskId;
    private int collaboratorId;

    // Constructors
    public Subtask(int id, String title, String status, int taskId, int collaboratorId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.taskId = taskId;
        this.collaboratorId = collaboratorId;
    }

    public Subtask(String title, String status, int taskId, int collaboratorId) {
        this.title = title;
        this.status = status;
        this.taskId = taskId;
        this.collaboratorId = collaboratorId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getCollaboratorId() {
        return collaboratorId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setCollaboratorId(int collaboratorId) {
        this.collaboratorId = collaboratorId;
    }

    // toString for debugging
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", taskId=" + taskId +
                ", collaboratorId=" + collaboratorId +
                '}';
    }
}