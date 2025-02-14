package Admin;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;

public class AdminLogin {

    public static void adminLogin(Scanner sc) {
        System.out.println("\nAdmin Login");
        System.out.print("Enter Email: ");
        String email = sc.next();

        System.out.print("Enter Password: ");
        String password = sc.next();

        // Authenticate the admin and get their details
        Admin admin = authenticateAdmin(email, password);

        if (admin != null) {
            System.out.println("Login Successful! Welcome, 🎉" + admin.getUsername() + ".😊");

            // Pass adminId to the dashboard
            AdminDashboard.adminDashboard(sc, admin.getId()); // Open Admin Dashboard
        } else {
            System.out.println("Invalid email or password. Please try again.");
        }
    }

    private static Admin authenticateAdmin(String email, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Admin admin = null;

        try {
            // Connect to the database
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                DatabaseConnection.connectToDatabase();  // Reconnect if connection is closed
                conn = DatabaseConnection.getConnection();
            }

            // SQL query to authenticate the admin
            String query = "SELECT id, username FROM admins WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Fetch admin details
                int adminId = rs.getInt("id");
                String username = rs.getString("username");

                // Capitalize the first letter of the admin's username
                if (username != null && !username.isEmpty()) {
                    username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
                }

                // Create an Admin object with the fetched details
                admin = new Admin(adminId, username);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources safely
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }

        return admin;  // Return the admin object if authentication is successful, else null
    }
}