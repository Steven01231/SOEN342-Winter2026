package org.example;

import org.example.dto.TaskDTO;
import org.example.models.Task;
import org.example.utils.CSVParser;
import org.example.utils.CSVExporter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.Main.colCat;
import static org.example.Main.proCat;

public class TestUtilities {
    public static void main(String[] args) {
        try {
            // --- TEST 1: PARSER ---
            System.out.println("--- Testing CSV Parser ---");
            CSVParser parser = new CSVParser();
            File inputFile = new File("test_in.csv");

            List<TaskDTO> dtos = parser.parse(inputFile);

            for (TaskDTO dto : dtos) {
                System.out.println("Parsed Task: " + dto.taskName + " for Project: " + dto.projectName);
            }
            System.out.println("Total DTOs parsed: " + dtos.size());

            // --- TEST 2: EXPORTER ---
            System.out.println("\n--- Testing CSV Exporter ---");
            CSVExporter exporter = new CSVExporter(proCat, colCat);

            Task dummyTask = new Task();
            dummyTask.setTitle("Export Test");
            dummyTask.setDescription("Verify file writing works");

            List<Task> tasksToExport = new ArrayList<>();
            tasksToExport.add(dummyTask);

            exporter.export(tasksToExport, "test_out.csv");
            System.out.println("Success! Check your project folder for 'test_out.csv'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}