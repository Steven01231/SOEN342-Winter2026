package catalogs;

import org.example.models.RecurrencePattern;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecurrencePatternCatalog {

    private List<RecurrencePattern> recPats = new ArrayList<>();

    public RecurrencePatternCatalog(Connection conn){
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
}
