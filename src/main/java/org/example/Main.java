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
    static CollaboratorCatalog colCat;

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
                colCat = new CollaboratorCatalog(conn);
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
            System.out.println("14. Update Task");
            System.out.println("15. Manage Collaborators");
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
                    System.out.println("\n--- View Task Activity History ---");
                    System.out.print("Enter the Task ID you want to view: ");
                    try {
                        int historyTaskId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Fetching history for Task " + historyTaskId + "...");
                        recCat.printTaskHistory(historyTaskId);

                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                    }
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
                case 14:
                    System.out.println("\n--- Update Task ---");
                    System.out.print("Enter Task ID to update: ");
                    try {
                        int updateTaskId = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("What would you like to update?");
                        System.out.println("  1. Update Status");
                        System.out.println("  2. Update Due Date");
                        System.out.println("  3. Assign Collaborator");
                        System.out.println("  4. Update Collaborator Subtask Status (progress)");
                        System.out.print("Select sub-option: ");
                        int updateChoice = scanner.nextInt();
                        scanner.nextLine();

                        switch (updateChoice) {
                            case 1:
                                System.out.print("New status (todo, in_progress, blocked, done): ");
                                String newStatus = scanner.nextLine();
                                tc.updateTaskStatus(conn, updateTaskId, newStatus);
                                break;
                            case 2:
                                System.out.print("New due date in days from today (integer): ");
                                try {
                                    int days = Integer.parseInt(scanner.nextLine().trim());
                                    java.util.Date newDue = new java.util.Date(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000);
                                    tc.updateTaskDueDate(conn, updateTaskId, newDue);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Due date not updated.");
                                }
                                break;
                            case 3:
                                System.out.println("Available collaborators:");
                                if (colCat.getCollaborators().isEmpty()) {
                                    System.out.println("  (none — use option 15 to create one first)");
                                } else {
                                    for (org.example.models.Collaborator c : colCat.getCollaborators()) {
                                        int open = colCat.countOpenTasks(c.getId());
                                        System.out.println("  ID: " + c.getId()
                                            + " | Category: " + c.getCategory()
                                            + " | Open tasks: " + open + "/" + c.getTaskLimit()
                                            + " | Project ID: " + c.getProjectId());
                                    }
                                }
                                System.out.print("Enter Collaborator ID: ");
                                try {
                                    int collabId = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Enter Subtask Title: ");
                                    String subtaskTitle = scanner.nextLine();
                                    org.example.models.Subtask created = colCat.assignTaskToCollaborator(updateTaskId, collabId, subtaskTitle);
                                    if (created != null) {
                                        subCat.getSubtasks().add(created); // keep in-memory list in sync
                                    }
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid input.");
                                    scanner.nextLine();
                                } catch (IllegalStateException | IllegalArgumentException e) {
                                    System.err.println("Assignment failed: " + e.getMessage());
                                }
                                break;
                            case 4:
                                // Completing a subtask reports progress only — parent task status unchanged
                                System.out.println("Subtasks for task " + updateTaskId + ":");
                                boolean found = false;
                                for (org.example.models.Subtask s : subCat.getSubtasks()) {
                                    if (s.getTaskId() == updateTaskId) {
                                        System.out.println("  Subtask ID: " + s.getId()
                                            + " | Title: " + s.getTitle()
                                            + " | Status: " + s.getStatus()
                                            + " | Collaborator ID: " + s.getCollaboratorId());
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    System.out.println("  No subtasks found for this task.");
                                    break;
                                }
                                System.out.print("Enter Subtask ID to update: ");
                                try {
                                    int subtaskId = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("New status (todo, in_progress, blocked, done): ");
                                    String subtaskStatus = scanner.nextLine();
                                    subCat.updateSubtaskStatus(subtaskId, subtaskStatus);
                                } catch (java.util.InputMismatchException e) {
                                    System.out.println("Invalid input.");
                                    scanner.nextLine();
                                }
                                break;
                            default:
                                System.out.println("Invalid sub-option.");
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                    break;
                case 15:
                    System.out.println("\n--- Manage Collaborators ---");
                    System.out.println("  1. Create a Collaborator");
                    System.out.println("  2. Update Collaborator Task Limit");
                    System.out.println("  3. List Collaborators");
                    System.out.print("Select sub-option: ");
                    try {
                        int collabChoice = scanner.nextInt();
                        scanner.nextLine();
                        switch (collabChoice) {
                            case 1:
                                System.out.print("Category (Senior / Intermediate / Junior): ");
                                String category = scanner.nextLine().trim();
                                System.out.print("Project ID: ");
                                int projectId = scanner.nextInt();
                                scanner.nextLine();
                                int newCollabId = colCat.createCollaborator(category, projectId);
                                if (newCollabId != -1) {
                                    System.out.println("Collaborator created with ID: " + newCollabId
                                        + " | Category: " + category);
                                }
                                break;
                            case 2:
                                // Limit can be changed even when collaborator has assigned tasks
                                System.out.println("Current collaborators:");
                                for (org.example.models.Collaborator c : colCat.getCollaborators()) {
                                    int open = colCat.countOpenTasks(c.getId());
                                    System.out.println("  ID: " + c.getId()
                                        + " | Category: " + c.getCategory()
                                        + " | Limit: " + c.getTaskLimit()
                                        + " | Open tasks: " + open
                                        + " | Project ID: " + c.getProjectId());
                                }
                                System.out.print("Enter Collaborator ID: ");
                                int limitCollabId = scanner.nextInt();
                                scanner.nextLine();
                                System.out.print("New task limit (integer): ");
                                int newLimit = scanner.nextInt();
                                scanner.nextLine();
                                colCat.updateTaskLimit(limitCollabId, newLimit);
                                break;
                            case 3:
                                if (colCat.getCollaborators().isEmpty()) {
                                    System.out.println("No collaborators found.");
                                } else {
                                    for (org.example.models.Collaborator c : colCat.getCollaborators()) {
                                        int open = colCat.countOpenTasks(c.getId());
                                        System.out.println("  ID: " + c.getId()
                                            + " | Category: " + c.getCategory()
                                            + " | Open tasks: " + open + "/" + c.getTaskLimit()
                                            + " | Project ID: " + c.getProjectId());
                                    }
                                }
                                break;
                            default:
                                System.out.println("Invalid sub-option.");
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input.");
                        scanner.nextLine();
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
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