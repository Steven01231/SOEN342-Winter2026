package catalogs;

import org.example.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskCatalog {

    private List<Task> tasks = new ArrayList<Task>();
    private Connection conn;

    public TaskCatalog(Connection conn) {


        String query = "SELECT * FROM task";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                Task task = new Task(rs.getString("title"), rs.getString("description"));
                task.setTaskId(rs.getInt("id"));
                task.setCreationDate(new Date(rs.getLong("creation_date")));
                task.setPriorityLevel(rs.getInt("priority_level"));
                task.setStatus(rs.getString("status"));
                task.setDueDate(new Date(rs.getLong("due_date")));
                task.setProjectId(rs.getInt("project_id"));

                System.out.println(task);
                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.conn = conn;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {

        String query = "INSERT INTO task (title, description, creation_date, priority_level, status, due_date, project_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());

            // creation_date
            if (task.getCreationDate() != null) {
                pstmt.setLong(3, task.getCreationDate().getTime());
            } else {
                pstmt.setLong(3, System.currentTimeMillis()); // fallback
            }

            // priority_level
            pstmt.setInt(4, task.getPriorityLevel());

            // status
            if (task.getStatus() != null) {
                pstmt.setString(5, task.getStatus().name().toLowerCase());
            } else {
                pstmt.setString(5, "null"); // default
            }

            // due_date
            if (task.getDueDate() != null) {
                pstmt.setLong(6, task.getDueDate().getTime());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }

            // project_id
            pstmt.setInt(7, task.getProjectId());

            pstmt.executeUpdate();
            System.out.println("Task inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "TaskCatalog{" +
                "tasks=" + tasks +
                '}';
    }

    public List<Task> searchTasks(String keyword) {
        List<Task> searchResults = new ArrayList<>();

        String query = "SELECT * FROM task WHERE title LIKE ? OR description LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";

            pstmt.setString(1, searchPattern); // title
            pstmt.setString(2, searchPattern); // description

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"), rs.getString("description"));
                    task.setTaskId(rs.getInt("id"));
                    task.setCreationDate(new java.util.Date(rs.getLong("creation_date")));
                    task.setPriorityLevel(rs.getInt("priority_level"));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(new java.util.Date(rs.getLong("due_date")));
                    task.setProjectId(rs.getInt("project_id"));

                    searchResults.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching tasks: " + e.getMessage());
        }

        return searchResults;
    }
}
