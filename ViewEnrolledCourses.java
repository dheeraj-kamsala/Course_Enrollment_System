package Student;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ViewEnrolledCourses {

    public static void viewEnrolledCourses(Scanner sc, int studentId) {
        String query = "SELECT c.id, c.name, c.description, c.fee, c.duration FROM courses c " +
                "JOIN enrollments e ON c.id = e.course_id WHERE e.student_id = ? ORDER BY c.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Database connection is not available.");
                return;
            }

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("⚠ You are not enrolled in any courses.");
                    return;
                }

                System.out.println("\n📚 Your Enrolled Courses 📚");
                System.out.println("┌──────────┬───────────────────────────┬───────────┬───────────┬─────────────────────────────────┐");
                System.out.println("│ Course ID│ Course Name               │ Fee (₹)   │ Duration  │ Description                     │");
                System.out.println("├──────────┼───────────────────────────┼───────────┼───────────┼─────────────────────────────────┤");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double fee = rs.getDouble("fee");
                    int duration = rs.getInt("duration");
                    String description = rs.getString("description");

                    // Remove newlines and extra spaces from description
                    if (description != null) {
                        description = description.replaceAll("\\s+", " ").trim();
                    }

                    // Truncate description if it's too long
                    String shortDescription = description.length() > 31 ? description.substring(0, 27) + "..." : description;

                    // Print formatted output
                    System.out.printf("│ %-8d │ %-25s │ %7.1f   │ %-9d │ %-31s │%n",
                            id, name, fee, duration, shortDescription);
                }

                System.out.println("└──────────┴───────────────────────────┴───────────┴───────────┴─────────────────────────────────┘");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}













//package Student;
//import utils.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Scanner;
//
//public class ViewEnrolledCourses {
//
//    public static void viewEnrolledCourses(Scanner sc, int studentId) {
//        String query = "SELECT c.id, c.name, c.fee, c.duration FROM courses c " +
//                "JOIN enrollments e ON c.id = e.course_id " +
//                "WHERE e.student_id = ? ORDER BY c.id ASC";
//
//
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//
//            if (conn == null || conn.isClosed()) {
//                System.out.println("❌ Database connection is not available.");
//                return;
//            }
//
//            stmt.setInt(1, studentId);
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (!rs.isBeforeFirst()) {
//                    System.out.println("⚠ You are not enrolled in any courses.");
//                    return;
//                }
//
//                System.out.println("\n📚 Your Enrolled Courses 📚");
//                System.out.println("┌──────────┬───────────────────────────┬──────────┬─────────────┐");
//                System.out.println("│ Course ID│ Course Name               │ Fee (₹)  │ Duration    │");
//                System.out.println("├──────────┼───────────────────────────┼──────────┼─────────────┤");
//
//                while (rs.next()) {
//                    int id = rs.getInt("id");
//                    String name = rs.getString("name");
//                    double fee = rs.getDouble("fee");
//                    int duration = rs.getInt("duration");
//                    System.out.printf("│ %-8d │ %-25s │ %-8.2f │ %-11d │%n", id, name, fee, duration);
//                }
//
//                System.out.println("└──────────┴───────────────────────────┴──────────┴─────────────┘");
//            }
//
//        } catch (SQLException e) {
//            System.out.println("❌ Database error: " + e.getMessage());
//        }
//    }
//}