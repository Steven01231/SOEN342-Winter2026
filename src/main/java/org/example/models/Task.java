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

    private String title;
    private String description;
    private Date creationDate;
    private int priorityLevel;
    private StatusType status;
    private Date dueDate;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void updateTask(){

    }
    public void viewTask(){}


    public void searchTask(){

    }
}
