package catalogs;

import org.example.models.Subtask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubtaskCatalog {
    private List<Subtask> subtasks = new ArrayList<Subtask>();

    public SubtaskCatalog(Connection conn){

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
}



