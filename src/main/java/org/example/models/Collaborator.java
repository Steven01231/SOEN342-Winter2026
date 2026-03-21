package org.example.models;


public class Collaborator {

    private int id;
    private String name;
    private String category;
    private int taskLimit;
    private int projectId;


    public Collaborator() {}


    public Collaborator(String name, String category, int taskLimit, int projectId) {
        this.name = name;
        this.category = category;
        this.taskLimit = taskLimit;
        this.projectId = projectId;
    }
    public Collaborator(int id, String name, String category, int taskLimit, int projectId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.taskLimit = taskLimit;
        this.projectId = projectId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getTaskLimit() {
        return taskLimit;
    }

    public void setTaskLimit(int taskLimit) {
        this.taskLimit = taskLimit;
    }


    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "Collaborator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", taskLimit=" + taskLimit +
                ", projectId=" + projectId +
                '}';
    }

}
