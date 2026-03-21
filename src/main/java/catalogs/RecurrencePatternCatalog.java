package catalogs;

import org.example.models.RecurrencePattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecurrencePatternCatalog {

    private List<RecurrencePattern> recPats = new ArrayList<>();
    private Connection conn;

    public RecurrencePatternCatalog(Connection conn){
        this.conn = conn;
        String query = "SELECT * FROM recurrence_pattern";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                RecurrencePattern pattern = new RecurrencePattern(
                        rs.getInt("id"),
                        rs.getString("pattern_type"),
                        rs.getInt("custom_interval"),
                        new Date(rs.getLong("start_date")),
                        new Date(rs.getLong("end_date")),
                        rs.getString("selected_days"),
                        rs.getInt("task_id")
                );

                recPats.add(pattern);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RecurrencePattern> getRecurrencePatterns() {
        return recPats;
    }

    /** Persists a new RecurrencePattern to the database and adds it to the in-memory list. */
    public void saveRecurrencePattern(RecurrencePattern rp) {
        String sql = "INSERT OR REPLACE INTO recurrence_pattern " +
                     "(pattern_type, custom_interval, start_date, end_date, selected_days, task_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rp.getPatternType());
            ps.setInt(2, rp.getCustomInterval());
            ps.setLong(3, rp.getStartDate().getTime());
            ps.setLong(4, rp.getEndDate().getTime());
            ps.setString(5, rp.getSelectedDays());
            ps.setInt(6, rp.getTaskId());
            ps.executeUpdate();

            // retrieve auto-generated id and update the object
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    rp.setId(keys.getInt(1));
                }
            }
            recPats.add(rp);
            System.out.println("Recurrence pattern saved (id=" + rp.getId() + ").");
        } catch (SQLException e) {
            System.err.println("Error saving recurrence pattern: " + e.getMessage());
        }
    }
}
