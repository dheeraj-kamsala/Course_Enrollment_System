package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;

    public static void connectToDatabase() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/course_enroll", "root", "Chnu#123");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                connectToDatabase();  // Ensure the connection is open before returning it
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}