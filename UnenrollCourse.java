package Student;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UnenrollCourse {
    public static void unenrollCourse(Scanner sc, int studentId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Fetch enrolled courses
            PreparedStatement listCoursesStmt = conn.prepareStatement(
                    "SELECT c.id, c.name FROM courses c " +
                            "JOIN enrollments e ON c.id = e.course_id WHERE e.student_id = ?");
            listCoursesStmt.setInt(1, studentId);
            ResultSet rsCourses = listCoursesStmt.executeQuery();

            System.out.println("\n📚 Your Enrolled Courses 📚");
            System.out.println("┌───────────────────────────┐");
            System.out.println("│ Course Name               │");
            System.out.println("├───────────────────────────┤");

            boolean hasCourses = false;
            while (rsCourses.next()) {
                hasCourses = true;
                String name = rsCourses.getString("name");
                System.out.printf("│ %-25s │%n", name);
            }

            if (!hasCourses) {
                System.out.println("│ No enrolled courses found. │");
                System.out.println("└───────────────────────────┘");
                return;
            }
            System.out.println("└───────────────────────────┘");

            // Ask the student to choose a course by name
            System.out.print("\nEnter Course Name to Unenroll: ");
            String courseName = sc.nextLine().trim();

            // Get course ID from course name
            PreparedStatement getCourseIdStmt = conn.prepareStatement(
                    "SELECT id FROM courses WHERE name = ?");
            getCourseIdStmt.setString(1, courseName);
            ResultSet rsCourseId = getCourseIdStmt.executeQuery();

            if (!rsCourseId.next()) {
                System.out.println("❌ Course not found. Please enter a valid course name.");
                return;
            }

            int courseId = rsCourseId.getInt("id");

            // Check if the student is enrolled in the selected course
            PreparedStatement checkEnrollmentStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?");
            checkEnrollmentStmt.setInt(1, studentId);
            checkEnrollmentStmt.setInt(2, courseId);
            ResultSet rsCheck = checkEnrollmentStmt.executeQuery();
            rsCheck.next();

            if (rsCheck.getInt(1) == 0) {
                System.out.println("❌ You are not enrolled in this course.");
                return;
            }

            // Unenroll the student
            PreparedStatement unenrollStmt = conn.prepareStatement(
                    "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?");
            unenrollStmt.setInt(1, studentId);
            unenrollStmt.setInt(2, courseId);
            unenrollStmt.executeUpdate();

            System.out.println("✅ Successfully unenrolled from " + courseName + "!");

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}