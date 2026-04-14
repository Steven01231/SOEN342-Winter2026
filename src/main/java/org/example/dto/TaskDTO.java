package org.example.dto;

import org.example.models.PriorityLevel;

public class TaskDTO {
    public String taskName;
    public String description;
    public String subtask;
    public String status;
    public PriorityLevel priority;
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
            try {
                this.priority = PriorityLevel.fromValue(Integer.parseInt(row[4].trim()));
            } catch (IllegalArgumentException ignored) {
                this.priority = PriorityLevel.LOW;
            }
            this.dueDate = row[5];
            this.projectName = row[6];
            this.projectDescription = row[7];
            this.collaborator = row[8];
            this.collaboratorCategory = row[9];
        }
    }

    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public String getSubtaskTitle() { return subtask; }
    public String getStatus() { return status; }
    public PriorityLevel getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public String getProjectName() { return projectName; }
    public String getProjectDescription() { return projectDescription; }
    public String getCollaboratorName() { return collaborator; }
    public String getCollaboratorCategory() { return collaboratorCategory; }
}