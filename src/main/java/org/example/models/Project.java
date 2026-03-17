package org.example.models;

public class Project {
    private int id;
    private String name;
    private String description;

    // Constructor
    public Project(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Optional constructor (for inserts)
    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // toString (very useful for debugging)
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
