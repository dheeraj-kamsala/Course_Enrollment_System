package Admin;

import utils.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AddCourse {
    public static void addCourse(Scanner sc) {
        try {
            String courseName;

            // Flush input buffer before reading course name
            sc.nextLine(); // Ensure any leftover newline is removed

            while (true) {
                System.out.print("Enter Course Name: ");
                courseName = sc.nextLine().trim();

                // Check if course name is empty
                if (courseName.isEmpty()) {
                    System.out.println("⚠️ Course name cannot be empty. Please enter a valid name.");
                    continue;
                }
                courseName = courseName.substring(0, 1).toUpperCase() + courseName.substring(1).toLowerCase();
                // Validate course name format
                if (!isValidCourseName(courseName)) {
                    System.out.println("⚠️ Invalid course name. Only alphabetic characters, Roman numerals, and symbols ++, &, ., #, (, ), _, - are allowed.");
                    continue;
                }

                // Check if the course already exists
                if (isCourseExists(courseName)) {
                    System.out.println("⚠️ Course with this name already exists. Please enter a different name.");
                    continue;
                }

                break; // Valid input, exit loop
            }

            // Course Description Input
            System.out.println("Enter Course Description (Minimum 3-4 lines). Type 'DONE' to finish:");
            StringBuilder courseDescription = new StringBuilder();
            String line;
            while (true) {
                line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("DONE")) break;
                if (!line.isEmpty()) courseDescription.append(line).append("\n");
            }

            // Ensure at least 3 non-empty lines
            while (getLineCount(courseDescription.toString()) < 3) {
                System.out.println("⚠️ Course description must contain at least 3-4 non-empty lines.");
                courseDescription.setLength(0); // Reset description
                System.out.println("Please enter a valid Course Description (Type 'DONE' to finish): ");
                while (true) {
                    line = sc.nextLine().trim();
                    if (line.equalsIgnoreCase("DONE")) break;
                    if (!line.isEmpty()) courseDescription.append(line).append("\n");
                }
            }

            // Course Fee Input
            double courseFee;
            while (true) {
                System.out.print("Enter Course Fee (between 300 and 2000): ");
                if (sc.hasNextDouble()) {
                    courseFee = sc.nextDouble();
                    if (courseFee >= 300 && courseFee <= 2000) break;
                    System.out.println("⚠️ Invalid input. Please enter a fee between 300 and 2000.");
                } else {
                    System.out.println("⚠️ Invalid input. Please enter a valid number.");
                    sc.next(); // Consume invalid input
                }
            }
            sc.nextLine(); // Flush input buffer

            // Course Duration Input
            int courseDuration;
            while (true) {
                System.out.print("Enter Course Duration (in weeks, 1-50): ");
                if (sc.hasNextInt()) {
                    courseDuration = sc.nextInt();
                    if (courseDuration >= 1 && courseDuration <= 50) break;
                    System.out.println("⚠️ Invalid input. Please enter a number between 1 and 50.");
                } else {
                    System.out.println("⚠️ Invalid input. Please enter a valid number.");
                    sc.next(); // Consume invalid input
                }
            }
            sc.nextLine(); // Flush input buffer

            // Insert course into the database
            PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(
                    "INSERT INTO courses (name, description, fee, duration) VALUES (?, ?, ?, ?)");
            stmt.setString(1, courseName);
            stmt.setString(2, courseDescription.toString());
            stmt.setDouble(3, courseFee);
            stmt.setInt(4, courseDuration);
            stmt.executeUpdate();

            System.out.println("✅ New course added successfully.");

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    private static boolean isValidCourseName(String courseName) {
        String regex = "^[a-zA-Z\\s]+(?:[\\+\\.&#()_-]+[a-zA-Z\\s]*)*$";
        return Pattern.compile(regex).matcher(courseName).matches();
    }

    private static boolean isCourseExists(String courseName) throws SQLException {
        PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM courses WHERE name = ?");
        stmt.setString(1, courseName);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    private static int getLineCount(String text) {
        int count = 0;
        for (String line : text.split("\n")) {
            if (!line.trim().isEmpty()) count++;
        }
        return count;
    }
}
