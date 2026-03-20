package org.example.dto;

public class TaskDTO {
    public String taskName;
    public String description;
    public String subtask;
    public String status;
    public String priority;
    public String dueDate;
    public String projectName;
    public String projectDescription;
    public String collaborator;
    public String collaboratorCategory;

    // used to map csv row data to task dto
    public TaskDTO(String[] row) {
        if (row.length >= 10) {
            this.taskName = row[0];
            this.description = row[1];
            this.subtask = row[2];
            this.status = row[3];
            this.priority = row[4];
            this.dueDate = row[5];
            this.projectName = row[6];
            this.projectDescription = row[7];
            this.collaborator = row[8];
            this.collaboratorCategory = row[9];
        }
    }
}