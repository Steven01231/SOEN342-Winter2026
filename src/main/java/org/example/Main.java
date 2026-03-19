package org.example;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import catalogs.*;
import database.TaskController;
import org.example.controllers.CSVController;
import org.example.models.Task;
import org.example.utils.CSVExporter;


public class Main {

    static ProjectCatalog proCat;
    static RecordCatalog recCat;
    static RecurrencePatternCatalog recPatCat;
    static SubtaskCatalog subCat;
    static TagCatalog tagCat;
    static TaskCatalog taskCat;

    public static void main(String[] args) {

        String url = "jdbc:sqlite:Organizer.db";
        TaskController tc = new TaskController();
        Connection conn = null; // declare outside try

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        CSVController csvController = new CSVController(proCat, taskCat);

        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("成功! Connected to SQLite.");
                tc.initializeDatabase(conn);

                proCat = new ProjectCatalog(conn);
                recCat = new RecordCatalog(conn);
                recPatCat = new RecurrencePatternCatalog(conn);
                subCat = new SubtaskCatalog(conn);
                tagCat = new TagCatalog(conn);
                taskCat = new TaskCatalog(conn);
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }

        while (!exit) {
            System.out.println("\n===== Task Organizer Menu =====");
            System.out.println("1. Create a new Project");
            System.out.println("2. List all Projects");
            System.out.println("3. Create a new Task");
            System.out.println("4. List all Tasks");
            System.out.println("5. Create a new Subtask");
            System.out.println("6. List all Subtasks");
            System.out.println("7. Add a Record to a Task");
            System.out.println("8. List Records");
            System.out.println("9. Set Recurrence Pattern for Task");
            System.out.println("10. List Recurrence Patterns");
            System.out.println("11. Import Tasks from CSV");
            System.out.println("12. Export All Tasks to CSV");
            System.out.println("13. Search Tasks (Keyword)");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("Creating a new Project...");
                    // call method to create project
                    break;
                case 2:
                    System.out.println("Listing all Projects...");
                    // call method to list projects
                    break;
                case 3:
                    System.out.println("Creating a new Task...");
                    tc.createTaskFromUserInput(scanner,taskCat);
                    break;
                case 4:
                    System.out.println("Listing all Tasks...");
                    // call method to list tasks
                    break;
                case 5:
                    System.out.println("Creating a new Subtask...");
                    // call method to create subtask
                    break;
                case 6:
                    System.out.println("Listing all Subtasks...");
                    // call method to list subtasks
                    break;
                case 7:
                    System.out.println("Adding a Record to a Task...");
                    // call method to add record
                    break;
                case 8:
                    System.out.println("Listing all Records...");
                    // call method to list records
                    break;
                case 9:
                    System.out.println("Setting Recurrence Pattern for Task...");
                    // call method to set recurrence
                    break;
                case 10:
                    System.out.println("Listing all Recurrence Patterns...");
                    // call method to list recurrence patterns
                    break;
                case 11:
                    System.out.println("--- Import Tasks ---");
                    System.out.print("Enter CSV file path (e.g., test_in.csv): ");
                    String importPath = scanner.nextLine();
                    try {
                        csvController.importFromCSV(new File(importPath));
                        System.out.println("Import successful! Use Option 4 to see them.");
                    } catch (Exception e) {
                        System.err.println("Import failed: " + e.getMessage());
                    }
                    break;
                case 12:
                    System.out.println("--- Export Tasks ---");
                    System.out.print("Enter destination file name (e.g., my_export.csv): ");
                    String exportPath = scanner.nextLine();
                    try {
                        CSVExporter exporter = new org.example.utils.CSVExporter();
                        exporter.export(taskCat.getTasks(), exportPath);
                        System.out.println("Export successful! Check your project folder.");
                    } catch (Exception e) {
                        System.err.println("Export failed: " + e.getMessage());
                    }
                    break;
                case 13:
                    System.out.println("\n--- Advanced Task Search ---");
                    System.out.println("(Press ENTER to skip any filter)");

                    System.out.print("Keyword (title/desc): ");
                    String searchKeyword = scanner.nextLine();

                    System.out.print("Status filter (todo, in_progress, blocked, done): ");
                    String searchStatus = scanner.nextLine();

                    //dynamic search
                    List<org.example.models.Task> searchResults = taskCat.advancedSearch(searchKeyword, searchStatus);

                    if (searchResults.isEmpty()) {
                        System.out.println("No tasks found matching your criteria.");
                    } else {
                        System.out.println("\nFound " + searchResults.size() + " matching tasks:");
                        for (org.example.models.Task t : searchResults) {
                            System.out.println(" - [" + t.getStatus() + "] " + t.getTitle() + " | Due: " + t.getDueDate());
                        }

                        System.out.print("\nDo you want to export these results to CSV? (y/n): ");
                        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                            System.out.print("Enter export file name (e.g., search_results.csv): ");
                            String outPath = scanner.nextLine();
                            try {
                                org.example.utils.CSVExporter exp = new org.example.utils.CSVExporter();
                                exp.export(searchResults, outPath);
                                System.out.println("Search results exported successfully to " + outPath);
                            } catch (Exception e) {
                                System.err.println("Export failed: " + e.getMessage());
                            }
                        }
                    }
                    break;
                case 0:
                    System.out.println("Exiting... Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
                    break;
            }



        }
        scanner.close();
    }
}