package catalogs;

import org.example.models.Tag;
import org.example.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagCatalog {

    private List<Tag> tags = new ArrayList<>();
    private Connection conn;

    public TagCatalog(Connection conn) {
        this.conn = conn;
        String query = "SELECT * FROM tag";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tags.add(new Tag(rs.getInt("id"), rs.getString("keyword")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Creates a new tag with the given keyword. If a tag with the same keyword
     * already exists (case-insensitive), returns its existing id without inserting.
     */
    public int createTag(String keyword) {
        for (Tag t : tags) {
            if (t.getKeyword().equalsIgnoreCase(keyword)) {
                System.out.println("Tag '" + keyword + "' already exists with ID " + t.getId() + ".");
                return t.getId();
            }
        }

        String sql = "INSERT INTO tag (keyword) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, keyword);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    tags.add(new Tag(id, keyword));
                    System.out.println("Tag '" + keyword + "' created with ID " + id + ".");
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating tag: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Associates a tag with a task (many-to-many). Silently ignores duplicate links.
     */
    public void addTagToTask(int taskId, int tagId) {
        String sql = "INSERT OR IGNORE INTO task_tag (task_id, tag_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, tagId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Tag (id=" + tagId + ") linked to task (id=" + taskId + ").");
            } else {
                System.out.println("Tag was already linked to this task.");
            }
        } catch (SQLException e) {
            System.err.println("Error linking tag to task: " + e.getMessage());
        }
    }

    /** Removes the association between a tag and a task. */
    public void removeTagFromTask(int taskId, int tagId) {
        String sql = "DELETE FROM task_tag WHERE task_id = ? AND tag_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, tagId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Tag removed from task.");
            } else {
                System.out.println("No such tag-task link found.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing tag from task: " + e.getMessage());
        }
    }

    /** Returns all tags associated with a given task. */
    public List<Tag> getTagsForTask(int taskId) {
        List<Tag> result = new ArrayList<>();
        String sql = "SELECT t.id, t.keyword FROM tag t " +
                     "JOIN task_tag tt ON t.id = tt.tag_id " +
                     "WHERE tt.task_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Tag(rs.getInt("id"), rs.getString("keyword")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tags for task: " + e.getMessage());
        }
        return result;
    }

    /**
     * Returns all tasks that have a tag matching the given keyword (case-insensitive),
     * sorted by due date ascending per the spec's default sort order.
     */
    public List<Task> getTasksByTag(String keyword) {
        List<Task> result = new ArrayList<>();
        String sql = "SELECT task.* FROM task " +
                     "JOIN task_tag ON task.id = task_tag.task_id " +
                     "JOIN tag ON tag.id = task_tag.tag_id " +
                     "WHERE LOWER(tag.keyword) = LOWER(?) " +
                     "ORDER BY task.due_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"), rs.getString("description"));
                    task.setTaskId(rs.getInt("id"));
                    task.setCreationDate(new java.util.Date(rs.getLong("creation_date")));
                    task.setPriorityLevel(rs.getInt("priority_level"));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(new java.util.Date(rs.getLong("due_date")));
                    task.setProjectId(rs.getInt("project_id"));
                    result.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tasks by tag: " + e.getMessage());
        }
        return result;
    }
}