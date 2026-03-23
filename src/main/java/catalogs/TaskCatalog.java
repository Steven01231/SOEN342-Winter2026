package catalogs;

import org.example.models.PriorityLevel;
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
                task.setPriorityLevel(PriorityLevel.fromValue(rs.getInt("priority_level")));
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

        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());

            // creation_date
            if (task.getCreationDate() != null) {
                pstmt.setLong(3, task.getCreationDate().getTime());
            } else {
                pstmt.setLong(3, System.currentTimeMillis()); // fallback
            }

            // priority_level
            pstmt.setInt(4, task.getPriorityLevel().ordinal());

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

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    task.setTaskId(keys.getInt(1));
                }
            }
            tasks.add(task);
            System.out.println("Task inserted successfully with ID " + task.getTaskId() + "!");


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

    /** Returns tasks whose due date falls within [from, to] inclusive, sorted by due date ASC. */
    public List<Task> searchByDateRange(java.util.Date from, java.util.Date to) {
        List<Task> results = new ArrayList<>();
        String sql = "SELECT * FROM task WHERE due_date >= ? AND due_date <= ? ORDER BY due_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, from.getTime());
            ps.setLong(2, to.getTime());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"), rs.getString("description"));
                    task.setTaskId(rs.getInt("id"));
                    task.setCreationDate(new java.util.Date(rs.getLong("creation_date")));
                    task.setPriorityLevel(PriorityLevel.fromValue(rs.getInt("priority_level")));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(new java.util.Date(rs.getLong("due_date")));
                    task.setProjectId(rs.getInt("project_id"));
                    results.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching by date range: " + e.getMessage());
        }
        return results;
    }

    /**
     * Returns tasks whose due date falls on the given day of week (0=Sun … 6=Sat),
     * sorted by due date ASC. Uses SQLite's strftime to extract the weekday.
     */
    public List<Task> searchByDayOfWeek(int dayOfWeek) {
        List<Task> results = new ArrayList<>();
        // strftime('%w', ...) returns 0=Sunday through 6=Saturday
        String sql = "SELECT * FROM task " +
                     "WHERE due_date IS NOT NULL AND due_date != 0 " +
                     "AND CAST(strftime('%w', datetime(due_date/1000, 'unixepoch')) AS INTEGER) = ? " +
                     "ORDER BY due_date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dayOfWeek);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"), rs.getString("description"));
                    task.setTaskId(rs.getInt("id"));
                    task.setCreationDate(new java.util.Date(rs.getLong("creation_date")));
                    task.setPriorityLevel(PriorityLevel.fromValue(rs.getInt("priority_level")));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(new java.util.Date(rs.getLong("due_date")));
                    task.setProjectId(rs.getInt("project_id"));
                    results.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching by day of week: " + e.getMessage());
        }
        return results;
    }

    public List<Task> advancedSearch(String keyword, String status) {
        List<Task> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM task WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        boolean hasCriteria = false;

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (title LIKE ? OR description LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
            hasCriteria = true;
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND status = ? ");
            params.add(status.trim().toLowerCase());
            hasCriteria = true;
        }

        if (!hasCriteria) {
            //only show "open" tasks if there is no criteria (todo, in_progress, blocked)
            sql.append("AND status IN ('todo', 'in_progress', 'blocked') ");
        }

        //sort by due date ascending
        sql.append("ORDER BY due_date ASC");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task(rs.getString("title"), rs.getString("description"));
                    task.setTaskId(rs.getInt("id"));
                    task.setCreationDate(new java.util.Date(rs.getLong("creation_date")));
                    task.setPriorityLevel(PriorityLevel.fromValue(rs.getInt("priority_level")));
                    task.setStatus(rs.getString("status"));
                    task.setDueDate(new java.util.Date(rs.getLong("due_date")));
                    task.setProjectId(rs.getInt("project_id"));
                    results.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching tasks: " + e.getMessage());
        }
        return results;
    }

    public void displayTasks() {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }

        System.out.printf("%-5s %-20s %-12s %-12s %-20s %-20s %-10s%n",
                "ID", "Title", "Priority", "Status", "Created", "Due", "Project");

        System.out.println("--------------------------------------------------------------------------------------------");

        for (Task t : tasks) {
            System.out.printf("%-5d %-20s %-12s %-12s %-20s %-20s %-10d%n",
                    t.getTaskId(),
                    t.getTitle(),
                    t.getPriorityLevel(),
                    t.getStatus(),
                    t.getCreationDate(),
                    t.getDueDate(),
                    t.getProjectId());
        }
    }
}
