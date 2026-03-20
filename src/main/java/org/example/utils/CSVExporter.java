package org.example.utils;

import org.example.models.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

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
        return new String[] {
                t.getTitle() != null ? t.getTitle() : "",
                t.getDescription() != null ? t.getDescription() : "",
                "", // Subtasks might need a loop if they exist
                t.getStatus() != null ? t.getStatus().toString() : "OPEN",
                String.valueOf(t.getPriorityLevel()),
                t.getDueDate() != null ? t.getDueDate().toString() : "",
                "ProjectPlaceholder", //placeholder until task is fully linked to a project
                "DescPlaceholder",
                "CollabPlaceholder",
                "CatPlaceholder"
        };
    }
}