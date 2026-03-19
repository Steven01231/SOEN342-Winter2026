package catalogs;

import org.example.models.Project;
import org.example.models.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RecordCatalog {
    private List<Record> records = new ArrayList<Record>();
    private Connection conn;

    public RecordCatalog(Connection conn){
        this.conn = conn;
        String query = "SELECT * FROM record";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Record record = new Record(
                        rs.getInt("id"),
                        new Date(rs.getLong("timestamp")),
                        rs.getString("description"),
                        rs.getInt("task_id")
                );

                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTaskHistory(int taskId) {
        String sql = "SELECT * FROM record WHERE task_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasHistory = false;

                while (rs.next()) {
                    hasHistory = true;
                    Date date = new Date(rs.getLong("timestamp"));
                    String action = rs.getString("description");

                    System.out.println("   🕒 [" + date.toString() + "] : " + action);
                }

                if (!hasHistory) {
                    System.out.println("   No history found for Task ID " + taskId);
                }
            }
        } catch (SQLException e) {
            System.out.println("History feature is currently unavailable (Table missing or error).");
        }
    }
}
