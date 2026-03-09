package database;

import java.sql.Connection;
import java.sql.PreparedStatement;import java.sql.SQLException;

public class TaskController{

    public void initializeDatabase(Connection conn) throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS inventory (id INTEGER PRIMARY KEY, item TEXT, qty INTEGER);";
        conn.createStatement().execute(sql);

        String sqlTask = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "creation_date INTEGER, " +
                "priority_level INTEGER, " +
                "status TEXT, " +
                "due_date INTEGER" +
                ");";

        conn.createStatement().execute(sqlTask);
    }

    public void insertTask(Connection conn, String title, String description) {
        String sqlInsert = "INSERT INTO tasks (title, description) VALUES (?, ?)";

        try (// Assuming you have a connection method
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);

            pstmt.executeUpdate();
            System.out.println("Task inserted successfully!");

        } catch (SQLException e) {
            System.out.println("Error inserting task: " + e.getMessage());
        }
    }

}