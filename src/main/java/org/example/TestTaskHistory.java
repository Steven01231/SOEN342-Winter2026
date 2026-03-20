package org.example;

import catalogs.RecordCatalog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TestTaskHistory {
    public static void main(String[] args) {
        System.out.println("=== Testing Task Activity History ===\n");

        String url = "jdbc:sqlite:Organizer.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            injectFakeHistory(conn, 1);

            RecordCatalog recCat = new RecordCatalog(conn);

            System.out.println("Fetching history for Task 1...");
            recCat.printTaskHistory(1);

            System.out.println("\nFetching history for Task 999...");
            recCat.printTaskHistory(999);

        } catch (Exception e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static void injectFakeHistory(Connection conn, int taskId) {
        String sql = "INSERT INTO record (timestamp, description, task_id) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Log 1: Created (2 days ago)
            pstmt.setLong(1, System.currentTimeMillis() - 172800000L);
            pstmt.setString(2, "Task was created via CSV Import");
            pstmt.setInt(3, taskId);
            pstmt.executeUpdate();

            // Log 2: Updated (1 day ago)
            pstmt.setLong(1, System.currentTimeMillis() - 86400000L);
            pstmt.setString(2, "Status changed from 'todo' to 'in_progress'");
            pstmt.setInt(3, taskId);
            pstmt.executeUpdate();

            // Log 3: Completed (Just now)
            pstmt.setLong(1, System.currentTimeMillis());
            pstmt.setString(2, "Task marked as 'done' by current user");
            pstmt.setInt(3, taskId);
            pstmt.executeUpdate();

            System.out.println("Injected 3 fake history logs for Task ID " + taskId + "\n");
        } catch (Exception e) {
            System.out.println("Could not inject fake history (Table might not exist yet).");
        }
    }
}
