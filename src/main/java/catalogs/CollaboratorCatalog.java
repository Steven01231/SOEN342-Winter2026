package catalogs;

import org.example.models.Collaborator;
import org.example.models.Subtask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CollaboratorCatalog {
    private ArrayList<Collaborator> collaborators;
    private Connection conn;

    public CollaboratorCatalog(Connection conn) {
        this.conn = conn;
        collaborators = new ArrayList<>();

        String query = "SELECT * FROM collaborator";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Collaborator collaborator = new Collaborator(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getInt("task_limit"),
                        rs.getInt("project_id")
                );

                collaborators.add(collaborator);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Collaborator> getCollaborators() {
        return collaborators;
    }

    public Collaborator getCollaboratorById(int id) {
        for (Collaborator c : collaborators) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    /**
     * Counts the number of open (non-done) subtasks assigned to a collaborator.
     */
    public int countOpenTasks(int collaboratorId) {
        String query = "SELECT COUNT(*) FROM subtask WHERE collaborator_id = ? AND status != 'done'";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, collaboratorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Assigns a task to a collaborator by creating a subtask.
     * Throws IllegalStateException if the collaborator's open-task limit is reached.
     */
    /**
     * Returns the created Subtask so the caller can keep SubtaskCatalog's in-memory list in sync.
     */
    public Subtask assignTaskToCollaborator(int taskId, int collaboratorId, String subtaskTitle) {
        Collaborator c = getCollaboratorById(collaboratorId);
        if (c == null) {
            throw new IllegalArgumentException("Collaborator not found with ID: " + collaboratorId);
        }

        int openCount = countOpenTasks(collaboratorId);
        if (openCount >= c.getTaskLimit()) {
            throw new IllegalStateException(
                c.getCategory() + " collaborator (id=" + collaboratorId + ") has reached their open-task limit of "
                + c.getTaskLimit() + ". Currently has " + openCount + " open task(s). "
                + "They must complete at least one task before being assigned a new one."
            );
        }

        String insert = "INSERT INTO subtask (title, status, task_id, collaborator_id) VALUES (?, 'todo', ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, subtaskTitle);
            ps.setInt(2, taskId);
            ps.setInt(3, collaboratorId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                System.out.println("Subtask created and collaborator (id=" + collaboratorId + ") assigned to task (id=" + taskId + ") successfully.");
                return new Subtask(newId, subtaskTitle, "todo", taskId, collaboratorId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads the task_limit directly from DB — always up to date regardless of in-memory state.
     */
    public int getTaskLimitFromDB(int collaboratorId) {
        String query = "SELECT task_limit FROM collaborator WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, collaboratorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Updates the task_limit of an existing collaborator.
     * Allowed even when the collaborator already has assigned tasks — the collaborator
     * may become overloaded if the new limit is lower than their current open-task count.
     */
    public void updateTaskLimit(int collaboratorId, int newLimit) {
        Collaborator c = getCollaboratorById(collaboratorId);
        if (c == null) {
            throw new IllegalArgumentException("Collaborator not found with ID: " + collaboratorId);
        }
        String query = "UPDATE collaborator SET task_limit = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, newLimit);
            ps.setInt(2, collaboratorId);
            ps.executeUpdate();
            int openCount = countOpenTasks(collaboratorId);
            c.setTaskLimit(newLimit); // sync in-memory
            System.out.println("Collaborator " + collaboratorId + " limit updated to " + newLimit + ".");
            if (openCount > newLimit) {
                System.out.println("WARNING: Collaborator is now overloaded — "
                    + openCount + " open task(s) exceed the new limit of " + newLimit + ".");
            }
        } catch (SQLException e) {
            System.err.println("Error updating collaborator limit: " + e.getMessage());
        }
    }

    /**
     * Creates a new collaborator under the given project.
     * Category must be "Senior" (limit 2), "Intermediate" (limit 5), or "Junior" (limit 10).
     */
    public int createCollaborator(String category, int projectId) {
        int limit;
        switch (category.toLowerCase()) {
            case "senior":       limit = 2;  break;
            case "intermediate": limit = 5;  break;
            case "junior":       limit = 10; break;
            default:
                throw new IllegalArgumentException(
                    "Invalid category '" + category + "'. Must be Senior, Intermediate, or Junior."
                );
        }

        String insert = "INSERT INTO collaborator (category, task_limit, project_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category);
            ps.setInt(2, limit);
            ps.setInt(3, projectId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                collaborators.add(new Collaborator(newId, category, limit, projectId));
                return newId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
