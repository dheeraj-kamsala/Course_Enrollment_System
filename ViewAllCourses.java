package Admin;

import utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ViewAllCourses {

    // Main method to view all courses
    public static void viewAllCourses(Scanner sc) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = DatabaseConnection.getConnection().createStatement();
            rs = stmt.executeQuery("SELECT * FROM courses");

            // Check if any courses are available
            boolean coursesAvailable = false;

            while (rs.next()) {
                if (!coursesAvailable) {
                    System.out.println("Courses Available:");
                    coursesAvailable = true;
                }
                // Display course ID, name, fee, and duration
                System.out.println("Course ID: " + rs.getInt("id") + " - "
                        + rs.getString("name") + " - $"
                        + rs.getDouble("fee") + " - "
                        + rs.getInt("duration") + " weeks");
            }

            // If no courses were found, print the message
            if (!coursesAvailable) {
                System.out.println("No Courses Available");
            }

            // Ask user if they want to search for a course
            searchCourse(sc);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Ensure resources are closed
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to search for a course
    private static void searchCourse(Scanner sc) {
        boolean searchAgain = true;

        while (searchAgain) {
            // Consume the newline character before asking for input (to clear any leftover)
            sc.nextLine();  // This will clear any lingering newline from previous input

            // Prompt for course name and trim leading/trailing spaces
            System.out.print("\nEnter the course name to search: ");
            String courseName = sc.nextLine().trim(); // Capture and clean input

            // Debug: Print raw input to check if it's being captured properly
            System.out.println("Raw input: '" + courseName + "'");

            // Check if the course name is empty
            if (courseName.isEmpty()) {
                System.out.println("⚠️ Course name cannot be empty. Please enter a valid name.");
                continue; // Skip this loop iteration and prompt again
            }

            // Validate the course name format (custom rules)
            if (!isValidCourseName(courseName)) {
                System.out.println("⚠️ Invalid course name. Only alphabetic characters, Roman numerals, and symbols ++, &, ., #, (, ), _, - are allowed.");
                continue; // Skip this loop iteration and prompt again
            }

            // Query the database to search for the course
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                String query = "SELECT * FROM courses WHERE name LIKE ?";
                pstmt = DatabaseConnection.getConnection().prepareStatement(query);
                pstmt.setString(1, "%" + courseName + "%"); // Use partial match for course name
                rs = pstmt.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("\nCourse Found:");
                    System.out.println("Course ID: " + rs.getInt("id"));
                    System.out.println("Course Name: " + rs.getString("name"));
                    System.out.println("Fee: $" + rs.getDouble("fee"));
                    System.out.println("Duration: " + rs.getInt("duration") + " weeks");
                }

                if (!found) {
                    System.out.println("Course is not available.");
                }

                // Prompt to search again with validation
                String choice = "";
                while (true) {
                    System.out.println("Would you like to search again? (yes/no)");
                    choice = sc.nextLine().trim().toLowerCase();

                    // Accept only valid inputs (yes, no, y, n)
                    if (choice.equals("yes") || choice.equals("y")) {
                        searchAgain = true; // Continue searching
                        break;
                    } else if (choice.equals("no") || choice.equals("n")) {
                        System.out.println("Returning to the dashboard...");
                        searchAgain = false; // Exit to the dashboard
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (pstmt != null) pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Method to validate course name format
    private static boolean isValidCourseName(String courseName) {
        String regex = "^[a-zA-Z\\s]+(?:[\\+\\.&#()_-]+[a-zA-Z\\s]*)*$";
        return Pattern.compile(regex).matcher(courseName).matches();
    }
}
