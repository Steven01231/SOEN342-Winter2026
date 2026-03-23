package catalogs;

import org.example.models.Subtask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubtaskCatalog {
    private List<Subtask> subtasks = new ArrayList<Subtask>();
    private Connection conn;

    public SubtaskCatalog(Connection conn){
        this.conn = conn;

        String query = "SELECT * FROM subtask";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Subtask subtask = new Subtask(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("status"),
                        rs.getInt("task_id"),
                        rs.getInt("collaborator_id")
                );

                subtasks.add(subtask);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {

        String query = "INSERT INTO subtask (title, status, task_id, collaborator_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // title
            pstmt.setString(1, subtask.getTitle());

            // status
            if (subtask.getStatus() != null) {
                pstmt.setString(2, subtask.getStatus());
            } else {
                pstmt.setString(2, "todo"); // default status
            }

            // task_id
            pstmt.setInt(3, subtask.getTaskId());

            // collaborator_id
            pstmt.setInt(4, subtask.getCollaboratorId());

            pstmt.executeUpdate();

            // Get generated ID
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    subtask.setId(keys.getInt(1));
                }
            }

            // Add to in-memory list
            subtasks.add(subtask);

            System.out.println("Subtask inserted successfully with ID " + subtask.getId() + "!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the status of a collaborator's subtask only.
     * Completing a subtask does NOT affect the parent task's status.
     */
    public void updateSubtaskStatus(int subtaskId, String newStatus) {
        String query = "UPDATE subtask SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newStatus.trim().toLowerCase());
            ps.setInt(2, subtaskId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Subtask " + subtaskId + " status updated to '"
                    + newStatus.trim().toLowerCase() + "'. Parent task status is unchanged.");


                // sync in-memory list
                for (Subtask s : subtasks) {
                    if (s.getId() == subtaskId) {
                        s.setStatus(newStatus.trim().toLowerCase());
                        break;
                    }
                }
            } else {
                System.out.println("No subtask found with ID " + subtaskId + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating subtask: " + e.getMessage());
        }
    }

    public void displaySubtasksByTaskId(int taskId) {
        boolean found = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == taskId) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No subtasks found for Task ID: " + taskId);
            return;
        }

        System.out.printf("%-5s %-20s %-15s%n", "ID", "Title", "Status");
        System.out.println("------------------------------------------");

        for (Subtask subtask : subtasks) {
            if (subtask.getTaskId() == taskId) {
                System.out.printf("%-5d %-20s %-15s%n",
                        subtask.getId(),
                        subtask.getTitle(),
                        subtask.getStatus());
            }
        }
    }

    public void markAllSubtasksDone(int taskId) {
        for (Subtask sub : subtasks) {
            if (sub.getTaskId() == taskId) {
                sub.setStatus("done");
            }
        }
    }
}



