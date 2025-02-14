package Student;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class StudentLogin {

    public static void studentLogin(Scanner sc) {
        System.out.println("\nStudent Login.");
        System.out.print("Enter Email: ");
        String email = sc.next();

        System.out.print("Enter Password: ");
        String password = sc.next();

        // Authenticate the student and get their details
        Student student = authenticateStudent(email, password);

        if (student != null) {
            System.out.println("Login Successful! Welcome, 🎉" + student.getName() + ".😊");

            // Pass studentId to the dashboard
            StudentDashboard.studentDashboard(sc, student.getId()); // Open Student Dashboard
        } else {
            System.out.println("Invalid email or password. Please try again.");
        }
    }

    private static Student authenticateStudent(String email, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Student student = null;

        try {
            // Connect to the database
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                DatabaseConnection.connectToDatabase();  // Reconnect if connection is closed
                conn = DatabaseConnection.getConnection();
            }

            // SQL query to authenticate the student
            String query = "SELECT id, name FROM students WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                // Fetch student details
                int studentId = rs.getInt("id");
                String studentName = rs.getString("name");

                // Capitalize the first letter of the student's name
                if (studentName != null && !studentName.isEmpty()) {
                    studentName = studentName.substring(0, 1).toUpperCase() + studentName.substring(1).toLowerCase();
                }

                // Create a Student object with the fetched details
                student = new Student(studentId, studentName);
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

        return student;  // Return the student object if authentication is successful, else null
    }
}
