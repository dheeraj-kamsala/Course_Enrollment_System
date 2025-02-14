package Admin;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ViewAllEnrolledStudents {
    public static void viewAllEnrolledStudents(Scanner sc) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            String query = "SELECT s.name, s.email, c.name AS course_name " +
                    "FROM students s " +
                    "JOIN enrollments e ON s.id = e.student_id " +  // Fixed: s.id instead of s.student_id
                    "JOIN courses c ON e.course_id = c.id";        // Fixed: c.id instead of c.course_id

            ResultSet rs = stmt.executeQuery(query);

            // Check if any students are enrolled
            boolean studentsAvailable = false;

            while (rs.next()) {
                if (!studentsAvailable) {
                    System.out.println("Enrolled Students:");
                    System.out.println("--------------------------------------------------------------");
                    System.out.printf("%-20s %-30s %-20s%n", "Student Name", "Email", "Course");
                    System.out.println("--------------------------------------------------------------");
                    studentsAvailable = true;
                }
                System.out.printf("%-20s %-30s %-20s%n",
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("course_name"));
            }

            // If no students were found, print the message
            if (!studentsAvailable) {
                System.out.println("No students are enrolled in any courses.");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching enrolled students: " + e.getMessage());
        }
    }
}
