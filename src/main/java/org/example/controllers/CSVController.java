package org.example.controllers;

import org.example.dto.TaskDTO;
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

    public CSVController(ProjectCatalog proCat, TaskCatalog taskCat) {
        this.parser = new CSVParser();
        this.exporter = new CSVExporter();
        this.projectCatalog = proCat;
        this.taskCatalog = taskCat;
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
                if (dto.priority != null && !dto.priority.isEmpty()) {
                    newTask.setPriorityLevel(Integer.parseInt(dto.priority.trim()));
                } else {
                    newTask.setPriorityLevel(1);
                }
            } catch (NumberFormatException e) {
                newTask.setPriorityLevel(1);
            }

            if (dto.status != null && !dto.status.trim().isEmpty()) {
                newTask.setStatus(dto.status.trim().toLowerCase());
            }

            if (dto.dueDate != null && !dto.dueDate.trim().isEmpty()) {
                try {
                    newTask.setDueDate(dateFormat.parse(dto.dueDate.trim()));
                } catch (java.text.ParseException e) {
                    System.err.println("Invalid date format for task: " + dto.taskName + ". Leaving due date empty.");
                }
            }

            newTask.setCreationDate(new java.util.Date());

            // 5. Hand it off to Steven's database method!
            taskCatalog.addTask(newTask);

            // Add to his local list so it stays in sync
            taskCatalog.getTasks().add(newTask);
        }
    }

    private int handleProjectAutoCreation(String name, String description) {
        if (name == null || name.trim().isEmpty()) return -1;

        int existingId = projectCatalog.findIdByName(name);

        if (existingId != -1) {
            return existingId;
        } else {
            System.out.println("Auto-creating missing project: " + name);
            return projectCatalog.createProject(name, description);
        }
    }
}