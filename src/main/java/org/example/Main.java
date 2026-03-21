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
            System.out.println("13. Search Tasks");
            System.out.println("14. Update Task");
            System.out.println("15. Manage Collaborators");
            System.out.println("16. Manage Tags");
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
                    tc.createTaskFromUserInput(scanner, taskCat, proCat);
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
                    System.out.println("\n--- Set Recurrence Pattern for Task ---");
                    try {
                        System.out.print("Enter Task ID to make recurring: ");
                        int recTaskId = Integer.parseInt(scanner.nextLine().trim());

                        // validate the task exists
                        org.example.models.Task templateTask = null;
                        for (org.example.models.Task t : taskCat.getTasks()) {
                            if (t.getTaskId() == recTaskId) { templateTask = t; break; }
                        }
                        if (templateTask == null) {
                            System.out.println("No task found with ID " + recTaskId + ". Aborting.");
                            break;
                        }

                        System.out.println("Pattern type: DAILY | WEEKLY | MONTHLY | CUSTOM");
                        System.out.print("Enter pattern type: ");
                        String patternType = scanner.nextLine().trim().toUpperCase();
                        if (!patternType.equals("DAILY") && !patternType.equals("WEEKLY")
                                && !patternType.equals("MONTHLY") && !patternType.equals("CUSTOM")) {
                            System.out.println("Invalid pattern type. Must be DAILY, WEEKLY, MONTHLY, or CUSTOM.");
                            break;
                        }

                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        java.util.Calendar todayMidnight = java.util.Calendar.getInstance();
                        todayMidnight.set(java.util.Calendar.HOUR_OF_DAY, 0);
                        todayMidnight.set(java.util.Calendar.MINUTE, 0);
                        todayMidnight.set(java.util.Calendar.SECOND, 0);
                        todayMidnight.set(java.util.Calendar.MILLISECOND, 0);
                        java.util.Date today = todayMidnight.getTime();

                        System.out.print("Start date (yyyy-MM-dd): ");
                        java.util.Date recStart = sdf.parse(scanner.nextLine().trim());

                        System.out.print("End date   (yyyy-MM-dd): ");
                        java.util.Date recEnd = sdf.parse(scanner.nextLine().trim());

                        if (recEnd.before(recStart)) {
                            System.out.println("End date must be on or after start date. Aborting.");
                            break;
                        }
                        if (recEnd.before(today)) {
                            System.out.println("End date is entirely in the past — no future occurrences possible. Aborting.");
                            break;
                        }

                        String selectedDays = null;
                        int customInterval  = 0;

                        if (patternType.equals("WEEKLY")) {
                            System.out.print("Selected days (comma-separated, e.g. Mon,Wed,Fri): ");
                            selectedDays = scanner.nextLine().trim();
                            if (selectedDays.isEmpty()) {
                                System.out.println("No days selected for WEEKLY pattern. Aborting.");
                                break;
                            }
                        } else if (patternType.equals("CUSTOM")) {
                            System.out.print("Interval in days: ");
                            customInterval = Integer.parseInt(scanner.nextLine().trim());
                            if (customInterval <= 0) {
                                System.out.println("Interval must be a positive integer. Aborting.");
                                break;
                            }
                        }

                        org.example.models.RecurrencePattern rp = new org.example.models.RecurrencePattern(
                                patternType, customInterval, recStart, recEnd, selectedDays, recTaskId);

                        // generate occurrences and filter out past dates
                        java.util.List<java.util.Date> allOccurrences = rp.generateOccurrences();
                        java.util.List<java.util.Date> occurrences = new java.util.ArrayList<>();
                        for (java.util.Date d : allOccurrences) {
                            if (!d.before(today)) occurrences.add(d);
                        }

                        int skipped = allOccurrences.size() - occurrences.size();
                        if (skipped > 0) {
                            System.out.println("Note: " + skipped + " past occurrence(s) skipped.");
                        }

                        if (occurrences.isEmpty()) {
                            System.out.println("No valid future occurrences generated for this pattern. "
                                    + "Check that your selected days/dates produce at least one occurrence on or after today.");
                            break;
                        }

                        recPatCat.saveRecurrencePattern(rp);

                        java.text.SimpleDateFormat display = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        System.out.println("\nGenerated " + occurrences.size() + " occurrence(s):");
                        for (int i = 0; i < occurrences.size(); i++) {
                            java.util.Date occDate = occurrences.get(i);
                            System.out.println("  [" + (i + 1) + "] " + templateTask.getTitle()
                                    + " | Due: " + display.format(occDate));

                            org.example.models.Task occ = new org.example.models.Task(
                                    templateTask.getTitle(), "Occurrence " + (i + 1),
                                    new java.util.Date(), templateTask.getPriorityLevel(),
                                    "todo", occDate, templateTask.getProjectId());
                            taskCat.addTask(occ);
                        }
                        System.out.println("All occurrences saved as individual tasks.");
                    } catch (java.text.ParseException e) {
                        System.out.println("Invalid date format. Use yyyy-MM-dd.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number input.");
                    }
                    break;
                case 10:
                    System.out.println("\n--- Recurrence Patterns ---");
                    if (recPatCat.getRecurrencePatterns().isEmpty()) {
                        System.out.println("No recurrence patterns defined yet.");
                    } else {
                        for (org.example.models.RecurrencePattern rp : recPatCat.getRecurrencePatterns()) {
                            System.out.println("  ID: " + rp.getId()
                                    + " | Task ID: " + rp.getTaskId()
                                    + " | Type: " + rp.getPatternType()
                                    + " | Start: " + rp.getStartDate()
                                    + " | End: " + rp.getEndDate()
                                    + (rp.getSelectedDays() != null ? " | Days: " + rp.getSelectedDays() : "")
                                    + (rp.getCustomInterval() > 0 ? " | Interval: " + rp.getCustomInterval() + "d" : ""));
                        }
                    }
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
                    System.out.println("\n--- Search Tasks ---");
                    System.out.println("  1. By keyword (title / description)");
                    System.out.println("  2. By tag");
                    System.out.println("  3. By status");
                    System.out.println("  4. By date range (period)");
                    System.out.println("  5. By day of week");
                    System.out.println("  6. All open tasks (no filter)");
                    System.out.print("Select sub-option: ");
                    try {
                        int searchChoice = Integer.parseInt(scanner.nextLine().trim());
                        List<org.example.models.Task> searchResults = new java.util.ArrayList<>();

                        switch (searchChoice) {
                            case 1:
                                System.out.print("Keyword (title/description): ");
                                String searchKeyword = scanner.nextLine().trim();
                                if (searchKeyword.isEmpty()) {
                                    System.out.println("Keyword cannot be empty.");
                                    break;
                                }
                                searchResults = taskCat.advancedSearch(searchKeyword, null);
                                break;
                            case 2:
                                System.out.print("Tag keyword: ");
                                String tagKeyword = scanner.nextLine().trim();
                                if (tagKeyword.isEmpty()) {
                                    System.out.println("Tag keyword cannot be empty.");
                                    break;
                                }
                                searchResults = tagCat.getTasksByTag(tagKeyword);
                                break;
                            case 3:
                                System.out.print("Status (todo / in_progress / blocked / done): ");
                                String searchStatus = scanner.nextLine().trim();
                                if (searchStatus.isEmpty()) {
                                    System.out.println("Status cannot be empty.");
                                    break;
                                }
                                searchResults = taskCat.advancedSearch(null, searchStatus);
                                break;
                            case 4:
                                java.text.SimpleDateFormat rangeSdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    System.out.print("From date (yyyy-MM-dd): ");
                                    java.util.Date rangeFrom = rangeSdf.parse(scanner.nextLine().trim());
                                    System.out.print("To date   (yyyy-MM-dd): ");
                                    java.util.Date rangeTo = rangeSdf.parse(scanner.nextLine().trim());
                                    // set rangeTo to end of day so the upper bound is inclusive
                                    java.util.Calendar cal = java.util.Calendar.getInstance();
                                    cal.setTime(rangeTo);
                                    cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                                    cal.set(java.util.Calendar.MINUTE, 59);
                                    cal.set(java.util.Calendar.SECOND, 59);
                                    rangeTo = cal.getTime();
                                    if (rangeFrom.after(rangeTo)) {
                                        System.out.println("From date must be before or equal to To date.");
                                        break;
                                    }
                                    searchResults = taskCat.searchByDateRange(rangeFrom, rangeTo);
                                } catch (java.text.ParseException e) {
                                    System.out.println("Invalid date format. Use yyyy-MM-dd.");
                                }
                                break;
                            case 5:
                                System.out.print("Day of week (Mon / Tue / Wed / Thu / Fri / Sat / Sun): ");
                                String dayInput = scanner.nextLine().trim().toLowerCase();
                                int dayOfWeek = -1;
                                switch (dayInput) {
                                    case "sun": case "sunday":    dayOfWeek = 0; break;
                                    case "mon": case "monday":    dayOfWeek = 1; break;
                                    case "tue": case "tuesday":   dayOfWeek = 2; break;
                                    case "wed": case "wednesday": dayOfWeek = 3; break;
                                    case "thu": case "thursday":  dayOfWeek = 4; break;
                                    case "fri": case "friday":    dayOfWeek = 5; break;
                                    case "sat": case "saturday":  dayOfWeek = 6; break;
                                    default: System.out.println("Unrecognised day. Use e.g. Mon, Tue, Wed.");
                                }
                                if (dayOfWeek != -1) {
                                    searchResults = taskCat.searchByDayOfWeek(dayOfWeek);
                                }
                                break;
                            case 6:
                                searchResults = taskCat.advancedSearch(null, null);
                                break;
                            default:
                                System.out.println("Invalid sub-option.");
                        }

                        if (!searchResults.isEmpty()) {
                            java.text.SimpleDateFormat dispFmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
                            System.out.println("\nFound " + searchResults.size() + " task(s):");
                            for (org.example.models.Task t : searchResults) {
                                String due = t.getDueDate() != null ? dispFmt.format(t.getDueDate()) : "no due date";
                                System.out.println("  ID: " + t.getTaskId()
                                        + " | [" + t.getStatus() + "] " + t.getTitle()
                                        + " | Due: " + due);
                            }
                            System.out.print("\nExport results to CSV? (y/n): ");
                            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                                System.out.print("File name (e.g. results.csv): ");
                                String outPath = scanner.nextLine().trim();
                                try {
                                    new org.example.utils.CSVExporter().export(searchResults, outPath);
                                    System.out.println("Exported to " + outPath);
                                } catch (Exception e) {
                                    System.err.println("Export failed: " + e.getMessage());
                                }
                            }
                        } else if (searchResults != null) {
                            System.out.println("No tasks found matching your criteria.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
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
                                    // warn if reopening causes collaborator overload
                                    if (!subtaskStatus.trim().equalsIgnoreCase("done")) {
                                        for (org.example.models.Subtask s : subCat.getSubtasks()) {
                                            if (s.getId() == subtaskId) {
                                                org.example.models.Collaborator c = colCat.getCollaboratorById(s.getCollaboratorId());
                                                if (c != null) {
                                                    int openCount = colCat.countOpenTasks(c.getId());
                                                    int liveLimit = colCat.getTaskLimitFromDB(c.getId());
                                                    if (openCount > liveLimit) {
                                                        System.out.println("WARNING: Collaborator (id=" + c.getId()
                                                            + ", category=" + c.getCategory()
                                                            + ") is now overloaded — "
                                                            + openCount + " open task(s) exceed their limit of "
                                                            + liveLimit + ".");
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
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
                case 16:
                    System.out.println("\n--- Manage Tags ---");
                    System.out.println("  1. Create a Tag");
                    System.out.println("  2. Add Tag to Task");
                    System.out.println("  3. Remove Tag from Task");
                    System.out.println("  4. List all Tags");
                    System.out.println("  5. View Tags on a Task");
                    System.out.println("  6. Search Tasks by Tag");
                    System.out.print("Select sub-option: ");
                    try {
                        int tagChoice = Integer.parseInt(scanner.nextLine().trim());
                        switch (tagChoice) {
                            case 1:
                                System.out.print("Tag keyword: ");
                                String newKeyword = scanner.nextLine().trim();
                                if (newKeyword.isEmpty()) {
                                    System.out.println("Keyword cannot be empty.");
                                } else {
                                    tagCat.createTag(newKeyword);
                                }
                                break;
                            case 2:
                                if (tagCat.getTags().isEmpty()) {
                                    System.out.println("No tags exist yet. Create one first (sub-option 1).");
                                    break;
                                }
                                System.out.println("Available tags:");
                                for (org.example.models.Tag tg : tagCat.getTags()) {
                                    System.out.println("  ID: " + tg.getId() + " | " + tg.getKeyword());
                                }
                                System.out.print("Enter Task ID: ");
                                int tagTaskId = Integer.parseInt(scanner.nextLine().trim());
                                System.out.print("Enter Tag ID: ");
                                int addTagId = Integer.parseInt(scanner.nextLine().trim());
                                tagCat.addTagToTask(tagTaskId, addTagId);
                                break;
                            case 3:
                                System.out.print("Enter Task ID: ");
                                int removeTaskId = Integer.parseInt(scanner.nextLine().trim());
                                List<org.example.models.Tag> taskTags = tagCat.getTagsForTask(removeTaskId);
                                if (taskTags.isEmpty()) {
                                    System.out.println("No tags linked to task " + removeTaskId + ".");
                                    break;
                                }
                                System.out.println("Tags on task " + removeTaskId + ":");
                                for (org.example.models.Tag tg : taskTags) {
                                    System.out.println("  ID: " + tg.getId() + " | " + tg.getKeyword());
                                }
                                System.out.print("Enter Tag ID to remove: ");
                                int removeTagId = Integer.parseInt(scanner.nextLine().trim());
                                tagCat.removeTagFromTask(removeTaskId, removeTagId);
                                break;
                            case 4:
                                if (tagCat.getTags().isEmpty()) {
                                    System.out.println("No tags defined yet.");
                                } else {
                                    System.out.println("All tags:");
                                    for (org.example.models.Tag tg : tagCat.getTags()) {
                                        System.out.println("  ID: " + tg.getId() + " | " + tg.getKeyword());
                                    }
                                }
                                break;
                            case 5:
                                System.out.print("Enter Task ID: ");
                                int viewTaskId = Integer.parseInt(scanner.nextLine().trim());
                                List<org.example.models.Tag> viewTags = tagCat.getTagsForTask(viewTaskId);
                                if (viewTags.isEmpty()) {
                                    System.out.println("No tags linked to task " + viewTaskId + ".");
                                } else {
                                    System.out.println("Tags on task " + viewTaskId + ":");
                                    for (org.example.models.Tag tg : viewTags) {
                                        System.out.println("  ID: " + tg.getId() + " | " + tg.getKeyword());
                                    }
                                }
                                break;
                            case 6:
                                System.out.print("Enter tag keyword to search: ");
                                String searchTag = scanner.nextLine().trim();
                                if (searchTag.isEmpty()) {
                                    System.out.println("Keyword cannot be empty.");
                                    break;
                                }
                                List<org.example.models.Task> tagResults = tagCat.getTasksByTag(searchTag);
                                if (tagResults.isEmpty()) {
                                    System.out.println("No tasks found with tag '" + searchTag + "'.");
                                } else {
                                    System.out.println("Tasks tagged '" + searchTag + "' (" + tagResults.size() + " found):");
                                    for (org.example.models.Task t : tagResults) {
                                        System.out.println("  ID: " + t.getTaskId()
                                                + " | [" + t.getStatus() + "] " + t.getTitle()
                                                + " | Due: " + t.getDueDate());
                                    }
                                }
                                break;
                            default:
                                System.out.println("Invalid sub-option.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
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