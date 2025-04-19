package com.lostfound.config;

// Provides database connection for the Lost and Found System.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database URL for MySQL connection (replace with your MySQL connection link).
    private static final String URL = "jdbc:mysql://localhost:3306/lostfounddb";
    // Database username (replace with your MySQL username).
    private static final String USER = "root";
    // Database password (replace with your MySQL password).
    private static final String PASSWORD = "";

    // Establishes and returns a connection to the MySQL database.
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}