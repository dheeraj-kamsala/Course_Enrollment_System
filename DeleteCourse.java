package Admin;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeleteCourse {

    public static void deleteCourse(Scanner sc, int adminId) {
        while (true) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                System.out.println("\n📌 Available Courses:");
                String allCoursesQuery = "SELECT id, name FROM courses ORDER BY id ASC";
                PreparedStatement allCoursesStmt = conn.prepareStatement(allCoursesQuery);
                ResultSet rsCourses = allCoursesStmt.executeQuery();

                List<Course> allCourses = new ArrayList<>();
                while (rsCourses.next()) {
                    int id = rsCourses.getInt("id");
                    String name = rsCourses.getString("name");
                    System.out.printf("📖 Course ID: %d - %s\n", id, name);
                    allCourses.add(new Course(id, name, 0, 0));
                }
                rsCourses.close();
                allCoursesStmt.close();

                if (allCourses.isEmpty()) {
                    System.out.println("⚠ No courses available in the system.");
                    return;
                }

                while (true) {
                    System.out.print("\nEnter a keyword to search for related Courses: ");
                    sc.nextLine();
                    String courseNameInput = sc.nextLine().trim();

                    String searchQuery = "SELECT id, name, fee, duration FROM courses " +
                            "WHERE LOWER(name) LIKE LOWER(?) ORDER BY name ASC";
                    PreparedStatement searchStmt = conn.prepareStatement(searchQuery);
                    searchStmt.setString(1, "%" + courseNameInput + "%");
                    ResultSet rs = searchStmt.executeQuery();

                    List<Course> courseList = new ArrayList<>();
                    System.out.println("\n🔎 Related Courses:");
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        double fee = rs.getDouble("fee");
                        int duration = rs.getInt("duration");
                        System.out.printf("📖 Course Name: %s - 💰 Fee: $%.2f - ⏳ Duration: %d weeks\n",
                                name, fee, duration);
                        courseList.add(new Course(id, name, fee, duration));
                    }
                    rs.close();
                    searchStmt.close();

                    if (courseList.isEmpty()) {
                        System.out.println("⚠ No related courses found. Try a different course name.");
                        continue;
                    }

                    Course selectedCourse = null;
                    while (selectedCourse == null) {
                        System.out.print("\nEnter the exact Course Name to Delete (or type 'done' to finish): ");
                        String exactName = sc.nextLine().trim();
                        if (exactName.equalsIgnoreCase("done")) {
                            System.out.println("🔙 Returning to Admin Dashboard...");
                            returnToAdminDashboard(sc, adminId);
                            return;
                        }

                        for (Course course : courseList) {
                            if (course.getName().equalsIgnoreCase(exactName)) {
                                selectedCourse = course;
                                break;
                            }
                        }

                        if (selectedCourse == null) {
                            System.out.println("❌ Course with name '" + exactName + "' not found among the search results.");
                            System.out.println("🔁 Please enter the correct name or type 'done' to exit.");
                        }
                    }

                    System.out.println("❗ Are you sure you want to delete the course: " + selectedCourse.getName() +
                            " and all its associated data? (Yes or No)");
                    String confirmation = sc.next();
                    sc.nextLine();

                    if (confirmation.equalsIgnoreCase("Yes") || confirmation.equalsIgnoreCase("Y")) {
                        try {
                            conn.setAutoCommit(false); // Start transaction

                            // Step 1: Delete dependent records from payments table
                            PreparedStatement deletePaymentsStmt = conn.prepareStatement(
                                    "DELETE FROM payments WHERE course_id = ?");
                            deletePaymentsStmt.setInt(1, selectedCourse.getId());
                            int paymentsDeleted = deletePaymentsStmt.executeUpdate();

                            // Step 2: Delete dependent records from feedback table
                            PreparedStatement deleteFeedbackStmt = conn.prepareStatement(
                                    "DELETE FROM feedback WHERE course_id = ?");
                            deleteFeedbackStmt.setInt(1, selectedCourse.getId());
                            int feedbackDeleted = deleteFeedbackStmt.executeUpdate();

                            // Step 3: Delete dependent records from enrollments table
                            PreparedStatement deleteEnrollmentsStmt = conn.prepareStatement(
                                    "DELETE FROM enrollments WHERE course_id = ?");
                            deleteEnrollmentsStmt.setInt(1, selectedCourse.getId());
                            int enrollmentsDeleted = deleteEnrollmentsStmt.executeUpdate();

                            // Step 4: Delete the course from the courses table
                            PreparedStatement deleteCourseStmt = conn.prepareStatement(
                                    "DELETE FROM courses WHERE id = ?");
                            deleteCourseStmt.setInt(1, selectedCourse.getId());
                            int coursesDeleted = deleteCourseStmt.executeUpdate();

                            // Commit transaction
                            conn.commit();

                            System.out.println("✅ Course '" + selectedCourse.getName() + "' deleted successfully.");
                            System.out.println("🗑 Removed " + paymentsDeleted + " payment(s).");
                            System.out.println("🗑 Removed " + feedbackDeleted + " feedback record(s).");
                            System.out.println("🗑 Removed " + enrollmentsDeleted + " enrollment(s).");

                            // Return to Admin Dashboard after successful deletion
                            System.out.println("🔙 Returning to Admin Dashboard...");
                            returnToAdminDashboard(sc, adminId);
                            return;

                        } catch (SQLException ex) {
                            conn.rollback(); // Rollback if any error occurs
                            System.out.println("❌ Error during deletion: " + ex.getMessage());
                        } finally {
                            conn.setAutoCommit(true); // Restore auto-commit mode
                        }

                    } else if (confirmation.equalsIgnoreCase("No") || confirmation.equalsIgnoreCase("N")) {
                        System.out.println("🔙 Returning to Admin Dashboard...");
                        returnToAdminDashboard(sc, adminId);
                        return;
                    } else {
                        System.out.println("⚠ Invalid input. Please enter Yes or No.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void returnToAdminDashboard(Scanner sc, int adminId) {
        AdminDashboard.adminDashboard(sc, adminId);
    }

    static class Course {
        private int id;
        private String name;
        private double fee;
        private int duration;

        public Course(int id, String name, double fee, int duration) {
            this.id = id;
            this.name = name;
            this.fee = fee;
            this.duration = duration;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getFee() {
            return fee;
        }

        public int getDuration() {
            return duration;
        }
    }
}
