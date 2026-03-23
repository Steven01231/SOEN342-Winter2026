package database;

import catalogs.ProjectCatalog;
import catalogs.RecordCatalog;
import catalogs.SubtaskCatalog;
import catalogs.TaskCatalog;
import org.example.models.PriorityLevel;
import org.example.models.StatusType;
import org.example.models.Subtask;
import org.example.models.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class TaskController{

    public void initializeDatabase(Connection conn) throws SQLException {

        Statement stmt = conn.createStatement();

        // Project
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS project (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "description TEXT" +
                        ");"
        );

        // Collaborator
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS collaborator (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "category TEXT, " +
                        "task_limit INTEGER, " +
                        "project_id INTEGER, " +
                        "FOREIGN KEY (project_id) REFERENCES project(id)" +
                        ");"
        );

        // Task
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS task (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "description TEXT, " +
                        "creation_date INTEGER, " +
                        "priority_level INTEGER, " +
                        "status TEXT, " +
                        "due_date INTEGER, " +
                        "project_id INTEGER, " +
                        "FOREIGN KEY (project_id) REFERENCES project(id)" +
                        ");"
        );

        // Subtask
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS subtask (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT, " +
                        "status TEXT, " +
                        "task_id INTEGER, " +
                        "collaborator_id INTEGER, " +
                        "FOREIGN KEY (task_id) REFERENCES task(id), " +
                        "FOREIGN KEY (collaborator_id) REFERENCES collaborator(id)" +
                        ");"
        );

        // Recurrence Pattern
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS recurrence_pattern (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "pattern_type TEXT, " +
                        "custom_interval INTEGER, " +
                        "start_date INTEGER, " +
                        "end_date INTEGER, " +
                        "selected_days TEXT, " +
                        "task_id INTEGER UNIQUE, " +   // 0..1 relationship with Task
                        "FOREIGN KEY (task_id) REFERENCES task(id)" +
                        ");"
        );

        // Tag
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS tag (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "keyword TEXT" +
                        ");"
        );

        // Task-Tag many-to-many relationship
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS task_tag (" +
                        "task_id INTEGER, " +
                        "tag_id INTEGER, " +
                        "PRIMARY KEY (task_id, tag_id), " +
                        "FOREIGN KEY (task_id) REFERENCES task(id), " +
                        "FOREIGN KEY (tag_id) REFERENCES tag(id)" +
                        ");"
        );

        // Record
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS record (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "timestamp INTEGER, " +
                        "description TEXT, " +
                        "task_id INTEGER, " +
                        "FOREIGN KEY (task_id) REFERENCES task(id)" +
                        ");"
        );

        stmt.close();
    }

    public void updateTaskDueDate(Connection conn, int taskId, java.util.Date newDueDate) {
        String query = "UPDATE task SET due_date = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, newDueDate.getTime());
            ps.setInt(2, taskId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Task " + taskId + " due date updated to " + newDueDate + ".");
            } else {
                System.out.println("No task found with ID " + taskId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating due date: " + e.getMessage());
        }
    }

    public void updateTaskStatus(Connection conn, int taskId, String newStatus, SubtaskCatalog subCat) {
        String query = "UPDATE task SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newStatus.trim().toLowerCase());
            ps.setInt(2, taskId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Task " + taskId + " status updated to '" + newStatus.trim().toLowerCase() + "'.");
                if (newStatus.equals("done")) {
                    String subtaskQuery = "UPDATE subtask SET status = 'done' WHERE task_id = ?";

                    try (PreparedStatement subPs = conn.prepareStatement(subtaskQuery)) {
                        subPs.setInt(1, taskId);
                        int updatedSubs = subPs.executeUpdate();

                        System.out.println(updatedSubs + " subtasks marked as done.");

                        // 🔥 Update in-memory list too
                        subCat.markAllSubtasksDone(taskId);

                    }
                }
            } else {
                System.out.println("No task found with ID " + taskId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    public void createTaskFromUserInput(Scanner scanner, TaskCatalog taskCat, ProjectCatalog proCat, RecordCatalog recCat) {
        System.out.println("=== Create a New Task ===");

        // --- Title ---
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Task not created.");
            return;
        }

        // --- Description ---
        System.out.print("Description: ");
        String description = scanner.nextLine();

        // --- Priority (1-5) -> PriorityLevel enum ---
        System.out.print("Priority Level (1-4, default 1): ");
        PriorityLevel priorityLevel = PriorityLevel.LOW;

        String priorityInput = scanner.nextLine().trim();
        if (!priorityInput.isEmpty()) {
            try {
                int p = Integer.parseInt(priorityInput);
                switch (p) {
                    case 1: priorityLevel = PriorityLevel.LOW; break;
                    case 2: priorityLevel = PriorityLevel.MEDIUM; break;
                    case 3: priorityLevel = PriorityLevel.HIGH; break;
                    case 4: priorityLevel = PriorityLevel.CRITICAL; break;
                    default:
                        System.out.println("Out of range, using LOW.");
                        priorityLevel = PriorityLevel.LOW;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using LOW.");
                priorityLevel = PriorityLevel.LOW;
            }
        }

        // --- Status (enum) ---
        System.out.print("Status (todo / in_progress / blocked / done, default 'todo'): ");
        String statusInput = scanner.nextLine().trim().toLowerCase();

        StatusType statusType;
        switch (statusInput) {
            case "todo":
                statusType = StatusType.TODO;
                break;
            case "in_progress":
                statusType = StatusType.IN_PROGRESS;
                break;
            case "blocked":
                statusType = StatusType.BLOCKED;
                break;
            case "done":
                statusType = StatusType.DONE;
                break;
            default:
                System.out.println("Unrecognised status, defaulting to 'TODO'.");
                statusType = StatusType.TODO;
        }

        // --- Due Date ---
        System.out.print("Due in days from today (integer, press Enter to skip): ");
        Date dueDate = null;
        String dueInput = scanner.nextLine().trim();

        if (!dueInput.isEmpty()) {
            try {
                int days = Integer.parseInt(dueInput);
                if (days < 0) {
                    System.out.println("Due date cannot be in the past. No due date set.");
                } else {
                    dueDate = new Date(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, no due date set.");
            }
        }

        // --- Project selection / creation ---
        java.util.List<org.example.models.Project> projects = proCat.getProjects();
        int projectId = 0;

        if (projects.isEmpty()) {
            System.out.println("No projects exist yet. Let's create one first.");
            projectId = createProjectInline(scanner, proCat);
        } else {
            System.out.println("Existing projects:");
            for (org.example.models.Project p : projects) {
                System.out.println("  ID: " + p.getId() + " | " + p.getName());
            }

            System.out.print("Enter Project ID (or 0 to create new, or press Enter to skip): ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                int chosen;
                try {
                    chosen = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    chosen = -2;
                }

                if (chosen == 0) {
                    projectId = createProjectInline(scanner, proCat);
                } else if (chosen > 0) {
                    boolean found = false;
                    for (org.example.models.Project p : projects) {
                        if (p.getId() == chosen) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        projectId = chosen;
                    } else {
                        System.out.println("Project ID not found. Defaulting to 'No Project'.");
                    }
                }
            }
        }

        if (projectId == -1) {
            System.out.println("Project creation failed. Task not created.");
            return;
        }

        // --- Create Task ---
        Task task = new Task(
                title,
                description,
                new Date(),
                priorityLevel,
                statusType,
                dueDate,
                projectId
        );

        taskCat.addTask(task);

        recCat.addRecord("Task created", task.getTaskId());

        System.out.println("Task created successfully!");
    }

    public void createSubtaskUI(Scanner scanner, SubtaskCatalog catalog, TaskCatalog taskCat) {

        System.out.println("=== Create Subtask ===");

        // Title
        System.out.print("Enter subtask title: ");
        String title = scanner.nextLine();

        // Status
        System.out.print("Enter status (e.g., todo, in progress, done): ");
        String status = scanner.nextLine();

        // Task ID
        taskCat.displayTasks();
        System.out.print("Enter task ID: ");
        int taskId = scanner.nextInt();

        // Ask if collaborator is needed
        System.out.print("Assign a collaborator? (y/n): ");
        char choice = scanner.next().toLowerCase().charAt(0);

        int collaboratorId = -1; // default value

        if (choice == 'y') {
            System.out.print("Enter collaborator ID: ");
            collaboratorId = scanner.nextInt();
        }

        scanner.nextLine(); // clear buffer

        // Create Subtask object
        Subtask subtask = new Subtask(0, title, status, taskId, collaboratorId);

        // Use catalog to add subtask
        catalog.addSubtask(subtask);

        System.out.println("Subtask created successfully!");
    }

    public int createProjectInline(Scanner scanner, catalogs.ProjectCatalog proCat) {
        System.out.print("New project name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Project name cannot be empty.");
            return -1;
        }
        // enforce uniqueness
        if (proCat.findIdByName(name) != -1) {
            System.out.println("A project named '" + name + "' already exists. Using it.");
            return proCat.findIdByName(name);
        }
        System.out.print("Project description: ");
        String desc = scanner.nextLine();
        int id = proCat.createProject(name, desc);
        if (id != -1) System.out.println("Project '" + name + "' created with ID " + id + ".");
        return id;
    }

}