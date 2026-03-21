package database;

import catalogs.TaskCatalog;
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



    public void insertTask(Connection conn, String title, String description) {
        String sqlInsert = "INSERT INTO task (title, description) VALUES (?, ?)";

        try (// Assuming you have a connection method
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);

            pstmt.executeUpdate();
            System.out.println("Task inserted successfully!");

        } catch (SQLException e) {
            System.out.println("Error inserting task: " + e.getMessage());
        }
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

    public void updateTaskStatus(Connection conn, int taskId, String newStatus) {
        String query = "UPDATE task SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newStatus.trim().toLowerCase());
            ps.setInt(2, taskId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Task " + taskId + " status updated to '" + newStatus.trim().toLowerCase() + "'.");
            } else {
                System.out.println("No task found with ID " + taskId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating task: " + e.getMessage());
        }
    }

    public void createTaskFromUserInput(Scanner scanner, TaskCatalog taskCat, catalogs.ProjectCatalog proCat) {
        System.out.println("=== Create a New Task ===");

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Task not created.");
            return;
        }

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Priority Level (1-5, default 1): ");
        int priority = 1;
        try {
            int p = Integer.parseInt(scanner.nextLine().trim());
            if (p >= 1 && p <= 5) priority = p;
            else System.out.println("Out of range, using priority 1.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, using priority 1.");
        }

        System.out.print("Status (todo / in_progress / blocked / done, default 'todo'): ");
        String status = scanner.nextLine().trim().toLowerCase();
        if (!status.equals("todo") && !status.equals("in_progress")
                && !status.equals("blocked") && !status.equals("done")) {
            System.out.println("Unrecognised status, defaulting to 'todo'.");
            status = "todo";
        }

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

        // --- project selection / creation ---
        java.util.List<org.example.models.Project> projects = proCat.getProjects();
        int projectId;
        if (projects.isEmpty()) {
            System.out.println("No projects exist yet. Let's create one first.");
            projectId = createProjectInline(scanner, proCat);
        } else {
            System.out.println("Existing projects:");
            for (org.example.models.Project p : projects) {
                System.out.println("  ID: " + p.getId() + " | " + p.getName());
            }
            // 1. Change the prompt to tell the user they can skip
            System.out.print("Enter Project ID (or 0 to create new, or press Enter to skip): ");
            projectId = 0; // Default to 0 (No Project)
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                int chosen;
                try {
                    chosen = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    chosen = -2; //invalid input
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
                        System.out.println("Project ID " + chosen + " not found. Defaulting to 'No Project'.");
                    }
                }
            }

            if (projectId == -1) {
                System.out.println("Project creation failed. Task not created.");
                return;
            }

            Task task = new Task(title, description, new Date(), priority, status, dueDate, projectId);
            taskCat.addTask(task);
        }

        if (projectId == -1) {
            System.out.println("Project creation failed. Task not created.");
            return;
        }

    }

    private int createProjectInline(Scanner scanner, catalogs.ProjectCatalog proCat) {
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