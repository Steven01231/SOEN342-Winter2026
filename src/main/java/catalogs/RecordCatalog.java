package catalogs;

import org.example.models.Project;
import org.example.models.Record;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RecordCatalog {
    private List<Record> records = new ArrayList<Record>();

    public RecordCatalog(Connection conn){
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
}
