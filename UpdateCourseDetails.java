package Admin;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UpdateCourseDetails {

    public static void updateCourseDetails(Scanner sc, int adminId) { // Add adminId as a parameter
        while (true) {
            System.out.println("\nAvailable Courses:");
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement listCoursesStmt = conn.prepareStatement(
                        "SELECT id, name, description, fee, duration FROM courses ORDER BY id ASC");
                ResultSet rsCourses = listCoursesStmt.executeQuery();

                boolean coursesFound = false;
                while (rsCourses.next()) {
                    coursesFound = true;
                    System.out.println("Course ID: " + rsCourses.getInt("id") + " - " + rsCourses.getString("name"));
                    System.out.println("  Description: " + rsCourses.getString("description"));
                    System.out.println("  Fee: $" + rsCourses.getDouble("fee"));
                    System.out.println("  Duration: " + rsCourses.getInt("duration") + " weeks\n");
                }
                rsCourses.close();
                listCoursesStmt.close();

                if (!coursesFound) {
                    System.out.println("No courses available in the system.");
                    return;
                }

                System.out.print("Enter a keyword to search for related Courses: ");
                sc.nextLine();
                String courseKeyword = sc.nextLine();

                PreparedStatement searchStmt = conn.prepareStatement(
                        "SELECT id, name, fee, duration FROM courses WHERE name LIKE ?");
                searchStmt.setString(1, "%" + courseKeyword + "%");
                ResultSet rsSearch = searchStmt.executeQuery();

                List<String> matchedCourses = new ArrayList<>();
                while (rsSearch.next()) {
                    matchedCourses.add(rsSearch.getString("name"));
                    System.out.println("Course Name: " + rsSearch.getString("name") + " - Fee: $" + rsSearch.getDouble("fee") + " - Duration: " + rsSearch.getInt("duration") + " weeks");
                }
                rsSearch.close();
                searchStmt.close();

                if (matchedCourses.isEmpty()) {
                    System.out.println("⚠ No matching courses found. Try again.");
                    continue;
                }

                String exactCourseName;
                while (true) {
                    System.out.print("\nEnter the exact Course Name to Update details (or type 'done' to finish): ");
                    exactCourseName = sc.nextLine().trim();

                    if (exactCourseName.equalsIgnoreCase("done")) {
                        return;  // Exit the method
                    }

                    PreparedStatement checkStmt = conn.prepareStatement(
                            "SELECT * FROM courses WHERE LOWER(name) = ?");
                    checkStmt.setString(1, exactCourseName.toLowerCase());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // Valid course found, get the details
                        int courseId = rs.getInt("id");
                        String previousName = rs.getString("name");
                        String previousDescription = rs.getString("description");
                        double previousFee = rs.getDouble("fee");
                        int previousDuration = rs.getInt("duration");

                        System.out.println("\nCurrent Course Details:");
                        System.out.println("Name: " + previousName);
                        System.out.println("Description: " + previousDescription);
                        System.out.println("Fee: $" + previousFee);
                        System.out.println("Duration: " + previousDuration + " weeks\n");

                        // Close the result set and statement
                        rs.close();
                        checkStmt.close();

                        // Get new course name (previous name is also allowed)
                        String newName = getNewCourseName(sc, previousName, conn);

                        // Get new course description (previous description is also allowed)
                        String newDescription = getNewCourseDescription(sc, previousDescription);

                        // Get new course fee (previous fee is also allowed)
                        double newFee = getNewCourseFee(sc, previousFee);

                        // Get new course duration (previous duration is also allowed)
                        int newDuration = getNewCourseDuration(sc, previousDuration);

                        // Update course in the database
                        PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE courses SET name = ?, description = ?, fee = ?, duration = ? WHERE id = ?");
                        updateStmt.setString(1, newName);
                        updateStmt.setString(2, newDescription);
                        updateStmt.setDouble(3, newFee);
                        updateStmt.setInt(4, newDuration);
                        updateStmt.setInt(5, courseId);
                        updateStmt.executeUpdate();
                        updateStmt.close();

                        System.out.println("✅ Course details updated successfully.");
                        showDashboard(sc, adminId); // Pass adminId to showDashboard
                        break;
                    } else {
                        System.out.println("⚠ Course with name '" + exactCourseName + "' does not exist. Please enter a valid Course Name.");
                    }
                    rs.close();
                    checkStmt.close();
                }

            } catch (SQLException e) {
                System.out.println("❌ Database error: " + e.getMessage());
            }
        }
    }

    private static String getNewCourseName(Scanner sc, String previousName, Connection conn) {
        while (true) {
            System.out.print("Enter a New Course Name (Press Enter to keep '" + previousName + "'): ");
            String courseName = sc.nextLine().trim();

            if (courseName.isEmpty()) {
                return previousName;
            }

            courseName = courseName.substring(0, 1).toUpperCase() + courseName.substring(1).toLowerCase();

            if (!isValidCourseName(courseName)) {
                System.out.println("⚠ Invalid course name.");
                continue;
            }

            if (!courseName.equals(previousName) && isCourseExists(courseName, conn)) {
                System.out.println("⚠ Course with this name already exists. Enter a different name.");
                continue;
            }

            return courseName;
        }
    }

    private static String getNewCourseDescription(Scanner sc, String previousDescription) {
        StringBuilder description = new StringBuilder();
        System.out.println("Enter new Course Description (Press Enter to keep previous description): ");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                return previousDescription;
            }
            if (line.equalsIgnoreCase("DONE")) {
                break;
            }
            description.append(line).append("\n");
        }
        return description.toString().trim();
    }

    private static double getNewCourseFee(Scanner sc, double previousFee) {
        while (true) {
            System.out.print("Enter Course Fee (300-2000) or press Enter to keep $" + previousFee + ": ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                return previousFee;
            }
            try {
                double fee = Double.parseDouble(input);
                if (fee >= 300 && fee <= 2000) {
                    return fee;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("⚠ Invalid input. Enter a valid fee.");
        }
    }

    private static int getNewCourseDuration(Scanner sc, int previousDuration) {
        while (true) {
            System.out.print("Enter Course Duration (1-50 weeks) or press Enter to keep " + previousDuration + " weeks: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                return previousDuration;
            }
            try {
                int duration = Integer.parseInt(input);
                if (duration >= 1 && duration <= 50) {
                    return duration;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("⚠ Invalid input. Enter a valid duration.");
        }
    }

    private static void showDashboard(Scanner sc, int adminId) { // Add adminId as a parameter
        System.out.println("Redirecting to the Dashboard...");
        AdminDashboard.adminDashboard(sc, adminId); // Pass both Scanner and adminId
    }

    private static boolean isValidCourseName(String courseName) {
        String regex = "^[a-zA-Z\\s]+(?:[\\+\\.&#()_-]+[a-zA-Z\\s])$";
        return Pattern.compile(regex).matcher(courseName).matches();
    }

    private static boolean isCourseExists(String courseName, Connection conn) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM courses WHERE name = ?")) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
            return false;
        }
    }
}