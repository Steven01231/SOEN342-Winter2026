package org.example;

import catalogs.TaskCatalog;
import org.example.models.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class TestSearchEngine {
    public static void main(String[] args) {
        System.out.println("=== Testing Advanced Search Engine ===\n");

        String url = "jdbc:sqlite:Organizer.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            TaskCatalog taskCat = new TaskCatalog(conn);

            //search for keyword
            System.out.println("Test 1: Searching for keyword 'update'...");
            List<Task> keywordResults = taskCat.advancedSearch("update", "");
            printResults(keywordResults);

            //search for status
            System.out.println("\nTest 2: Searching for 'done' tasks...");
            List<Task> statusResults = taskCat.advancedSearch("", "done");
            printResults(statusResults);

            //empty search
            // Requirement: "If no criteria is specified, all open tasks are listed (sorted by due date, in ascending order)."
            System.out.println("\nTest 3: Empty Search (Should only show open tasks sorted by date)...");
            List<Task> emptySearchResults = taskCat.advancedSearch("", "");
            printResults(emptySearchResults);

        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    private static void printResults(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("   -> No matching tasks found.");
        } else {
            for (Task t : tasks) {
                System.out.println("   -> [" + t.getStatus() + "] " + t.getTitle() + " | Due: " + t.getDueDate());
            }
        }
    }
}