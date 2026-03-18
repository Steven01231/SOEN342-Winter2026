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
        String sqlInsert = "INSERT INTO tasks (title, description) VALUES (?, ?)";

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

    public void createTaskFromUserInput(Scanner scanner, TaskCatalog taskCat) {
        System.out.println("=== Create a New Task ===");

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        System.out.print("Priority Level (integer): ");
        int priority = 1; // default
        try {
            priority = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, using default priority 1.");
        }

        System.out.print("Status (default 'Pending'): ");
        String status = scanner.nextLine();
        if (status.isEmpty()) {
            status = "null";
        }

        System.out.print("Due in days from today (integer, optional, press Enter to skip): ");
        Date dueDate = null;
        String dueInput = scanner.nextLine();
        if (!dueInput.isEmpty()) {
            try {
                int days = Integer.parseInt(dueInput);
                dueDate = new Date(System.currentTimeMillis() + days * 24L * 60 * 60 * 1000);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, no due date set.");
            }
        }

        System.out.print("Project ID (integer): ");
        int projectId = 0;
        try {
            projectId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, using project ID 0.");
        }

        // Build Task object
        Task task = new Task(
                title,
                description,
                new Date(),    // creation date is now
                priority,
                status,
                dueDate,
                projectId
        );

        taskCat.addTask(task);


    }

}