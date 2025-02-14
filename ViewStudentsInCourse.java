package Admin;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewStudentsInCourse {
    public static void viewStudentsInCourse(Scanner sc) {
        while (true) {
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Display all available courses first in ID-wise ascending order
                System.out.println("\n📚 Available Courses:");
                ResultSet rs = stmt.executeQuery("SELECT id, name FROM courses ORDER BY id ASC");
                boolean foundCourses = false;
                while (rs.next()) {
                    foundCourses = true;
                    System.out.println("📌 Course ID: " + rs.getInt("id") + " - " + rs.getString("name"));
                }

                if (!foundCourses) {
                    System.out.println("❌ No courses available.");
                    continue;  // Restart if no courses are found
                }

                // Ask admin to enter a keyword to search for courses or 'back' to return
                while (true) {
                    System.out.print("\n🔍 Enter Course Keyword to Search (or type 'back' to return): ");
                    sc.nextLine();  // Consume the newline
                    String keyword = sc.nextLine().toLowerCase();  // Convert to lowercase for case-insensitive search

                    if (keyword.equals("back")) {
                        System.out.println("🔙 Returning to Admin Dashboard...");
                        return;  // Exit the current search and return to the dashboard
                    }

                    // Fetch courses that match the keyword (case-insensitive search)
                    String searchQuery = "SELECT id, name FROM courses WHERE LOWER(name) LIKE ? ORDER BY id ASC";  // Using LOWER() for case-insensitive search
                    try (PreparedStatement pstmt = conn.prepareStatement(searchQuery)) {
                        pstmt.setString(1, "%" + keyword + "%");  // Use % for partial matching
                        ResultSet searchRs = pstmt.executeQuery();

                        // Store the results in a list for later use
                        List<String> matchingCourses = new ArrayList<>();
                        boolean foundMatches = false;
                        System.out.println("\n📚 Courses matching keyword: '" + keyword + "':");
                        while (searchRs.next()) {
                            foundMatches = true;
                            String courseName = searchRs.getString("name");
                            System.out.println("📌 Course ID: " + searchRs.getInt("id") + " - " + courseName);
                            matchingCourses.add(courseName);  // Store matching course names
                        }

                        if (!foundMatches) {
                            System.out.println("❌ No related courses found for: " + keyword);
                        } else {
                            // Ask the admin to enter the exact or partial course name (from the matching results)
                            boolean validCourseName = false;
                            while (!validCourseName) {
                                System.out.print("\n🔍 Enter Exact or Partial Course Name from above list: ");
                                String courseNameInput = sc.nextLine().trim();  // Trim any leading/trailing spaces

                                // Convert both the input and course names from the list to lowercase for case-insensitive comparison
                                boolean matchFound = false;
                                for (String course : matchingCourses) {
                                    if (course.toLowerCase().contains(courseNameInput.toLowerCase())) {
                                        matchFound = true;
                                        break;
                                    }
                                }

                                if (matchFound) {
                                    validCourseName = true;  // Proceed to check course existence in DB
                                    // Proceed with displaying enrolled students
                                    displayEnrolledStudents(conn, courseNameInput);
                                } else {
                                    System.out.println("❌ Invalid course name entered. Please enter a valid name from the available courses.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("❌ Database Error: " + e.getMessage());
                    }

                    // Ask if the admin wants to search again or return
                    String choice;
                    while (true) {
                        System.out.print("\nDo you want to search again? (yes or no): ");
                        choice = sc.nextLine().toLowerCase();

                        if (choice.equals("no") || choice.equals("n")) {
                            System.out.println("🔙 Returning to Admin Dashboard...");
                            return;  // Exit the search and return to the dashboard
                        } else if (choice.equals("yes") || choice.equals("y")) {
                            // Restart the search process
                            break;  // Exit the loop to continue searching
                        } else {
                            System.out.println("❌ Invalid choice. Please enter 'yes' or 'no'.");
                        }
                    }
                }

            } catch (SQLException e) {
                System.out.println("❌ Database Error: " + e.getMessage());
            }
        }
    }

    // Method to display enrolled students for a selected course
    private static void displayEnrolledStudents(Connection conn, String courseName) {
        String query = "SELECT s.id, s.name, s.email FROM students s " +
                "JOIN enrollments e ON s.id = e.student_id " +
                "JOIN courses c ON e.course_id = c.id " +
                "WHERE LOWER(c.name) LIKE ?"; // Allow partial match for the course name in a case-insensitive manner

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + courseName.toLowerCase() + "%");  // Use % for partial match
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            System.out.println("\n👨‍🎓 Students Enrolled in Course: " + courseName);

            while (rs.next()) {
                count++;
                System.out.println("🆔 ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") +
                        " | Email: " + rs.getString("email"));
            }

            if (count == 0) {
                System.out.println("❌ No students are enrolled in this course.");
            } else {
                System.out.println("\n📊 Total Enrolled Students: " + count);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching enrolled students: " + e.getMessage());
        }
    }
}
