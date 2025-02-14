package Student;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ProvideCourseFeedback {
    public static void provideFeedback(Scanner sc, int studentId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Display all courses the student is enrolled in
            String listCoursesQuery = "SELECT c.id, c.name FROM courses c " +
                    "JOIN enrollments e ON c.id = e.course_id WHERE e.student_id = ?";
            PreparedStatement listCoursesStmt = conn.prepareStatement(listCoursesQuery);
            listCoursesStmt.setInt(1, studentId);
            ResultSet rsCourses = listCoursesStmt.executeQuery();

            System.out.println("\n===== Your Enrolled Courses =====");
            System.out.printf("%-10s %-30s%n", "Course ID", "Course Name");
            System.out.println("---------------------------------");

            boolean hasCourses = false;
            while (rsCourses.next()) {
                System.out.printf("%-10d %-30s%n", rsCourses.getInt("id"), rsCourses.getString("name"));
                hasCourses = true;
            }

            if (!hasCourses) {
                System.out.println("❌ You are not enrolled in any courses.");
                return;
            }

            // Ask the student to choose a course
            System.out.print("\nEnter Course ID to Provide Feedback: ");
            int courseId = sc.nextInt();
            sc.nextLine(); // Consume newline

            // Check if the student is actually enrolled in the course
            String checkEnrollmentQuery = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
            PreparedStatement checkEnrollmentStmt = conn.prepareStatement(checkEnrollmentQuery);
            checkEnrollmentStmt.setInt(1, studentId);
            checkEnrollmentStmt.setInt(2, courseId);
            ResultSet rsCheck = checkEnrollmentStmt.executeQuery();
            rsCheck.next();

            if (rsCheck.getInt(1) == 0) {  // FIXED CONDITION
                System.out.println("❌ You are not enrolled in this course.");
                return;
            }

            // Get feedback details
            System.out.print("Enter Rating (1-5): ");
            int rating = sc.nextInt();
            sc.nextLine(); // Consume newline

            if (rating < 1 || rating > 5) {
                System.out.println("❌ Invalid rating! Please enter a value between 1 and 5.");
                return;
            }

            System.out.print("Enter Comments: ");
            String comments = sc.nextLine();

            // Insert feedback into the database
            String insertFeedbackQuery = "INSERT INTO feedback (student_id,course_id, rating, comments) VALUES (?, ?, ?, ?)";
            PreparedStatement feedbackStmt = conn.prepareStatement(insertFeedbackQuery);
            feedbackStmt.setInt(1, studentId);
            feedbackStmt.setInt(2, courseId);
            feedbackStmt.setInt(3, rating);
            feedbackStmt.setString(4, comments);
            feedbackStmt.executeUpdate();

            System.out.println("✅ Feedback submitted successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}