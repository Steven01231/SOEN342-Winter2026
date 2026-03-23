package org.example.controllers;

import catalogs.CollaboratorCatalog;
import org.example.dto.TaskDTO;
import org.example.models.Collaborator;
import org.example.models.PriorityLevel;
import org.example.models.Task;
import org.example.utils.CSVParser;
import org.example.utils.CSVExporter;
import catalogs.ProjectCatalog;
import catalogs.TaskCatalog;
import java.io.File;
import java.util.List;

public class CSVController {
    private final CSVParser parser;
    private final CSVExporter exporter;
    private final ProjectCatalog projectCatalog;
    private final TaskCatalog taskCatalog;
    private final CollaboratorCatalog collaboratorCatalog;

    public CSVController(ProjectCatalog proCat, TaskCatalog taskCat, CollaboratorCatalog collabCat) {
        this.parser = new CSVParser();
        this.projectCatalog = proCat;
        this.taskCatalog = taskCat;
        this.collaboratorCatalog = collabCat;
        this.exporter = new CSVExporter(proCat, collabCat);
    }

    public void importFromCSV(File file) throws Exception {
        List<TaskDTO> dtos = parser.parse(file);
        //assuming dates in the CSV look like "2026-03-25"
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");

        for (TaskDTO dto : dtos) {
            int projectId = handleProjectAutoCreation(dto.projectName, dto.projectDescription);

            Task newTask = new Task();
            newTask.setTitle(dto.taskName);
            newTask.setDescription(dto.description);
            newTask.setProjectId(projectId);

            try {
                if (dto.priority != null) {
                    newTask.setPriorityLevel(dto.priority);
                } else {
                    newTask.setPriorityLevel(PriorityLevel.fromValue(1));
                }
            } catch (NumberFormatException e) {
                newTask.setPriorityLevel(PriorityLevel.fromValue(1));
            }

            if (dto.status != null && !dto.status.trim().isEmpty()) {
                newTask.setStatus(dto.status.trim().toLowerCase());
            }

            if (dto.dueDate != null && !dto.dueDate.trim().isEmpty()) {
                try {
                    newTask.setDueDate(dateFormat.parse(dto.dueDate.trim()));
                } catch (java.text.ParseException e) {
                    System.err.println("Invalid date format for task: " + dto.getTaskName());
                }
            }

            newTask.setCreationDate(new java.util.Date());
            taskCatalog.addTask(newTask);

            if (dto.getCollaboratorName() != null && !dto.getCollaboratorName().trim().isEmpty()) {
                int collabId = handleCollaboratorAutoCreation(
                        dto.getCollaboratorName(),
                        dto.getCollaboratorCategory(),
                        projectId
                );

                if (collabId != -1) {
                    String subTitle = (dto.getSubtaskTitle() != null && !dto.getSubtaskTitle().isEmpty())
                            ? dto.getSubtaskTitle()
                            : "Main Responsibility";

                    collaboratorCatalog.assignTaskToCollaborator(newTask.getTaskId(), collabId, subTitle);
                }
            }
        }
    }

    private int handleProjectAutoCreation(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            return 0; // 0 represents 'No Project'
        }

        int existingId = projectCatalog.findIdByName(name);

        if (existingId != -1) {
            return existingId;
        } else {
            System.out.println("Auto-creating missing project: " + name);
            return projectCatalog.createProject(name, description);
        }
    }
    private int handleCollaboratorAutoCreation(String name, String category, int projectId) {
        // Look for existing collaborator with this name in this specific project
        for (Collaborator c : collaboratorCatalog.getCollaborators()) {
            if (c.getName().equalsIgnoreCase(name) && c.getProjectId() == projectId) {
                return c.getId();
            }
        }
        System.out.println("Auto-creating missing collaborator: " + name);
        return collaboratorCatalog.createCollaborator(name, category, projectId);
    }
}