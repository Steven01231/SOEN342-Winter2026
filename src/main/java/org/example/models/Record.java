package org.example.models;

import java.util.Date;

public class Record {
    private int id;
    private Date timestamp;
    private String description;
    private int taskId;

    // Constructors
    public Record(int id, Date timestamp, String description, int taskId) {
        this.id = id;
        this.timestamp = timestamp;
        this.description = description;
        this.taskId = taskId;
    }

    public Record(Date timestamp, String description, int taskId) {
        this.timestamp = timestamp;
        this.description = description;
        this.taskId = taskId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public int getTaskId() {
        return taskId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    // toString for debugging
    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                ", taskId=" + taskId +
                '}';
    }
}
