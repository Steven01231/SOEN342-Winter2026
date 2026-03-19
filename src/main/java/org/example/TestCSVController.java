package org.example;
import org.example.controllers.CSVController;
import catalogs.ProjectCatalog;
import catalogs.TaskCatalog;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestCSVController {
    public static void main(String[] args) {
        System.out.println("--- Testing CSV Controller Auto-Creation ---");

        String url = "jdbc:sqlite:Organizer.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            ProjectCatalog proCat = new ProjectCatalog(conn);
            TaskCatalog taskCat = new TaskCatalog(conn);

            CSVController controller = new CSVController(proCat, taskCat);

            File testFile = new File("test_in.csv");
            if (!testFile.exists()) {
                System.out.println("pls create 'test_in.csv' in your project root first");
                return;
            }

            System.out.println("Starting import process...");
            controller.importFromCSV(testFile);

            System.out.println("\nImport finished! Current Tasks in Database:");
            for (org.example.models.Task t : taskCat.getTasks()) {
                System.out.println(" - " + t.getTitle() + " (Project ID: " + t.getProjectId() + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
