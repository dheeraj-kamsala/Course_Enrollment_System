package Admin;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCourseFeedback {

    public static void viewAllCourseFeedback() {
        String query = "SELECT c.name, s.name AS student_name, f.rating, f.comments " +
                "FROM feedback f " +
                "JOIN courses c ON f.course_id = c.id " +
                "JOIN students s ON f.student_id  = s.id " +
                "ORDER BY c.name, f.rating DESC"; // Sort by course name & rating

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== All Course Feedback =====");
            System.out.printf("%-35s %-32s %-17s %-40s%n", "Course", "Student", "Rating","Comments");
            System.out.println("-----------------------------------------------------------------------------------------------------------");

            boolean hasFeedback = false;
            while (rs.next()) {
                System.out.printf("%-35s %-32s %-17d %-40s%n",
                        rs.getString("name"),
                        rs.getString("student_name"),
                        rs.getInt("rating"),
                        rs.getString("comments"));
                hasFeedback = true;
            }

            if (!hasFeedback) {
                System.out.println("❌ No feedback available for any courses.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}




//
//import utils.DatabaseConnection;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Scanner;

//public class ViewCourseFeedback {
//   public static void viewCourseFeedback(Scanner sc) {
//        try {
//            Statement stmt = DatabaseConnection.getConnection().createStatement();
//            ResultSet rs = stmt.executeQuery("SELECT * FROM feedback");
//            System.out.println("Course Feedback:");
//
//            // Check if there are any feedback records
//            boolean feedbackAvailable = false;
//            while (rs.next()) {
//                if (!feedbackAvailable) {
//                    feedbackAvailable = true;
//                }
//                System.out.println(rs.getString("course_name") + " - Rating: " + rs.getInt("rating") + "/5 - \"" + rs.getString("comments") + "\" - Provided by: " + rs.getString("student_name"));
//            }
//
//            if (!feedbackAvailable) {
//                System.out.println("No feedback available for courses.");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}


