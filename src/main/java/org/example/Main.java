package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import database.TaskController;


public class Main {
    public static void main(String[] args) {

        String url = "jdbc:sqlite:Organizer.db";
        TaskController tc = new TaskController();

        // Will clean it more
        /*
         * Discussion: 1. Produce Object first before inserting to database
         * Put all db related code to Database Controller
         * same with classes
         */
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("成功! Connected to SQLite.");

                // You can now call methods to create tables or insert data here
                tc.initializeDatabase(conn);
                tc.insertTask(conn, "Hackathon", "Competition amongst Developers");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }

    }

}