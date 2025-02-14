//package Admin;
//
//import utils.DatabaseConnection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class GenerateReports {
//
//    public static void generateReport() {
//        Statement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            stmt = DatabaseConnection.getConnection().createStatement();
//
//            // Query to get the total number of courses
//            String coursesQuery = "SELECT COUNT(*) AS total_courses FROM courses";
//            rs = stmt.executeQuery(coursesQuery);
//            rs.next();
//            int totalCourses = rs.getInt("total_courses");
//
//            // Query to get the total number of enrolled students
//            String studentsQuery = "SELECT COUNT(*) AS total_students FROM students";
//            rs = stmt.executeQuery(studentsQuery);
//            rs.next();
//            int totalStudents = rs.getInt("total_students");
//
//            // Query to get the total revenue (sum of course fees)
//            String revenueQuery = "SELECT SUM(fee) AS total_revenue FROM courses";
//            rs = stmt.executeQuery(revenueQuery);
//            rs.next();
//            double totalRevenue = rs.getDouble("total_revenue");
//
//            // Print the report
//            System.out.println("\nReports:");
//            System.out.println("Total Courses: " + totalCourses);
//            System.out.println("Total Enrolled Students: " + totalStudents);
//            System.out.println("Total Revenue: $" + totalRevenue);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            // Ensure resources are closed
//            try {
//                if (rs != null) rs.close();
//                if (stmt != null) stmt.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}


//
//package Admin;
//
//import utils.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class GenerateReports {
//
//    public static void generateAdminReport() {
//        try (Connection conn = DatabaseConnection.getConnection()) {
//            System.out.println("\n===== 📊 Admin Report 📊 =====");
//
//            // 1️⃣ Total Number of Students
//            printReport(conn, "Total Students: ", "SELECT COUNT(*) FROM students");
//
//            // 2️⃣ Total Courses Available
//            printReport(conn, "Total Courses: ", "SELECT COUNT(*) FROM courses");
//
//            // 3️⃣ Total Enrollments
//            printReport(conn, "Total Enrollments: ", "SELECT COUNT(*) FROM enrollments");
//
//            // 4️⃣ Average Course Rating
//            printReport(conn, "Average Course Rating: ", "SELECT AVG(rating) FROM feedback");
//
//            // 5️⃣ Course-wise Feedback Summary
//            generateCourseFeedbackSummary(conn);
//
//        } catch (SQLException e) {
//            System.out.println("❌ Database error: " + e.getMessage());
//        }
//    }
//
//    private static void printReport(Connection conn, String label, String query) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//            if (rs.next()) {
//                System.out.println(label + (rs.getString(1) == null ? "0" : rs.getString(1)));
//            }
//        }
//    }
//
//    private static void generateCourseFeedbackSummary(Connection conn) throws SQLException {
//        String query = "SELECT c.name AS course_name, COUNT(f.id) AS feedback_count, AVG(f.rating) AS avg_rating " +
//                "FROM courses c " +
//                "LEFT JOIN feedback f ON c.id = f.course_id " +
//                "GROUP BY c.name " +
//                "ORDER BY avg_rating DESC";
//
//        try (PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//
//            System.out.println("\n===== 📌 Course Feedback Summary 📌 =====");
//            System.out.printf("%-25s %-15s %-10s%n", "Course Name", "Feedback Count", "Avg Rating");
//            System.out.println("---------------------------------------------------");
//
//            boolean hasFeedback = false;
//            while (rs.next()) {
//                System.out.printf("%-25s %-15d %-10.2f%n",
//                        rs.getString("course_name"),
//                        rs.getInt("feedback_count"),
//                        rs.getDouble("avg_rating"));
//                hasFeedback = true;
//            }
//
//            if (!hasFeedback) {
//                System.out.println("❌ No feedback available.");
//            }
//        }
//    }
//}


package Admin;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenerateReports {

    public static void generateAdminReport() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("\n===== 📊 Admin Report 📊 =====");

            //  Total Students
            printReport(conn, "Total Students: ", "SELECT COUNT(*) FROM students");

            //  Total Courses Available
            printReport(conn, "Total Courses: ", "SELECT COUNT(*) FROM courses");

            //  Total Enrollments
            printReport(conn, "Total Enrollments: ", "SELECT COUNT(*) FROM enrollments");

            //  Total Revenue (Sum of Fees from Payments)
            printReport(conn, "Total Revenue: ₹", "SELECT SUM(c.fee) FROM payments p JOIN courses c ON p.course_id = c.id WHERE p.payment_status = 'Completed'");

            //  Most Popular Course (Highest Enrollments)
            getMostPopularCourse(conn);

            //  Highest Rated Course (Best Feedback)
            getHighestRatedCourse(conn);

            //  Payment Summary (Methods & Transactions)
            generatePaymentSummary(conn);

            //  Course-wise Feedback Summary
            generateCourseFeedbackSummary(conn);

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    private static void printReport(Connection conn, String label, String query) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println(label + (rs.getString(1) == null ? "0" : rs.getString(1)));
            }
        }
    }

    private static void getMostPopularCourse(Connection conn) throws SQLException {
        String query = "SELECT c.name, COUNT(e.course_id) AS enrollments " +
                "FROM enrollments e " +
                "JOIN courses c ON e.course_id = c.id " +
                "GROUP BY c.name " +
                "ORDER BY enrollments DESC " +
                "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Most Popular Course: " + rs.getString("name") + " (Enrolled: " + rs.getInt("enrollments") + " students)");
            } else {
                System.out.println("Most Popular Course: No enrollments yet.");
            }
        }
    }

    private static void getHighestRatedCourse(Connection conn) throws SQLException {
        String query = "SELECT c.name, AVG(f.rating) AS avg_rating " +
                "FROM feedback f " +
                "JOIN courses c ON f.course_id = c.id " +
                "GROUP BY c.name " +
                "ORDER BY avg_rating DESC " +
                "LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.printf("Highest Rated Course: %s (⭐ %.2f/5)\n", rs.getString("name"), rs.getDouble("avg_rating"));
            } else {
                System.out.println("Highest Rated Course: No feedback yet.");
            }
        }
    }

    private static void generatePaymentSummary(Connection conn) throws SQLException {
        String query = "SELECT payment_method, COUNT(*) AS total_transactions, SUM(c.fee) AS total_amount " +
                "FROM payments p " +
                "JOIN courses c ON p.course_id = c.id " +
                "WHERE p.payment_status = 'Completed' " +
                "GROUP BY p.payment_method " +
                "ORDER BY total_amount DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== 💳 Payment Summary 💳 =====");
            System.out.printf("%-15s %-20s %-10s%n", "Payment Method", "Total Transactions", "Total Amount (₹)");
            System.out.println("------------------------------------------------");

            boolean hasPayments = false;
            while (rs.next()) {
                System.out.printf("%-15s %-20d %-10.2f%n",
                        rs.getString("payment_method"),
                        rs.getInt("total_transactions"),
                        rs.getDouble("total_amount"));
                hasPayments = true;
            }

            if (!hasPayments) {
                System.out.println("❌ No payments made yet.");
            }
        }
    }

    private static void generateCourseFeedbackSummary(Connection conn) throws SQLException {
        String query = "SELECT c.name AS course_name, COUNT(f.id) AS feedback_count, AVG(f.rating) AS avg_rating " +
                "FROM courses c " +
                "LEFT JOIN feedback f ON c.id = f.course_id " +
                "GROUP BY c.name " +
                "ORDER BY avg_rating DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== 📌 Course Feedback Summary 📌 =====");
            System.out.printf("%-25s %-15s %-10s%n", "Course Name", "Feedback Count", "Avg Rating");
            System.out.println("---------------------------------------------------");

            boolean hasFeedback = false;
            while (rs.next()) {
                System.out.printf("%-25s %-15d %-10.2f%n",
                        rs.getString("course_name"),
                        rs.getInt("feedback_count"),
                        rs.getDouble("avg_rating"));
                hasFeedback = true;
            }

            if (!hasFeedback) {
                System.out.println("❌ No feedback available.");
            }
        }
    }
}
