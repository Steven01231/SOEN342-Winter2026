package org.example.utils;

import catalogs.CollaboratorCatalog;
import catalogs.ProjectCatalog;
import org.example.models.Collaborator;
import org.example.models.Project;
import org.example.models.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {
    private ProjectCatalog projectCatalog;
    private CollaboratorCatalog collaboratorCatalog;

    public CSVExporter(ProjectCatalog proCat, CollaboratorCatalog collabCat) {
        this.projectCatalog = proCat;
        this.collaboratorCatalog = collabCat;
    }

    public File export(List<Task> tasks, String filePath) throws IOException {
        File file = new File(filePath);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("TaskName,Description,Subtask,Status,Priority,DueDate,ProjectName,ProjectDescription,Collaborator,CollaboratorCategory");
            bw.newLine();

            for (Task task : tasks) {
                String[] row = buildRow(task);
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
        return file;
    }

    public String[] buildRow(Task t) {
        String projectName = "";
        String projectDesc = "";
        for (Project p : projectCatalog.getProjects()) {
            if (p.getId() == t.getProjectId()) {
                projectName = p.getName();
                projectDesc = p.getDescription();
                break;
            }
        }
        String collabName = "";
        String collabCat = "";

        for (Collaborator c : collaboratorCatalog.getCollaborators()) {
            if (c.getProjectId() == t.getProjectId()) {
                collabCat = c.getCategory();
                collabName = c.getName();
                break;
            }
        }
        return new String[] {
                t.getTitle() != null ? t.getTitle() : "",
                t.getDescription() != null ? t.getDescription() : "",
                "", //loop if subtask
                t.getStatus() != null ? t.getStatus().toString() : "OPEN",
                String.valueOf(t.getPriorityLevel()),
                t.getDueDate() != null ? t.getDueDate().toString() : "",
                projectName,
                projectDesc,
                collabName,
                collabCat
        };
    }
}