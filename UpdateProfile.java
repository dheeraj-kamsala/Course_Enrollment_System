package Student;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UpdateProfile {

    public static void updateProfile(int studentId) {
        String query = "SELECT name, email, phoneNumber FROM students WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Error: Could not connect to the database.");
                return;
            }

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n===== 🆔 Student Profile =====");
                    System.out.println("👤 Name       : " + rs.getString("name"));
                    System.out.println("📧 Email      : " + rs.getString("email"));
                    System.out.println("📞 Phone      : " + rs.getString("phoneNumber"));
                } else {
                    System.out.println("❌ Error: Student not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    // ✏️ Update Student Profile
    public static void updateProfile(Scanner sc, int studentId) {
        String query = "SELECT name, email, phoneNumber, password FROM students WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(query)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Error: Could not connect to the database.");
                return;
            }

            checkStmt.setInt(1, studentId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("❌ Error: Student ID not found. Update not allowed.");
                    return;
                }

                String currentName = rs.getString("name");
                String currentEmail = rs.getString("email");
                String currentPhoneNumber = rs.getString("phoneNumber");
                String currentPassword = rs.getString("password");

                // Show existing profile
                System.out.println("\n===== 📋 Existing Profile =====");
                System.out.println("👤 Name       : " + currentName);
                System.out.println("📧 Email      : " + currentEmail);
                System.out.println("📞 Phone      : " + currentPhoneNumber);

                System.out.println("\n🔹 Updating Profile...");

                // Get new details
                String name = getValidInput(sc, "Enter New Name (leave blank to keep existing): ", UpdateProfile::isValidName, currentName);
                String email = getValidEmail(sc, currentEmail, studentId, conn);
                String password = getValidPassword(sc, currentPassword);  // Pass current password to keep it if empty
                String phoneNumber = getValidPhoneNumber(sc, currentPhoneNumber, studentId, conn);

                // Update Profile
                if (updateStudentProfile(conn, studentId, name, email, password, phoneNumber)) {
                    System.out.println("✅ Profile updated successfully!");
                } else {
                    System.out.println("❌ Error: No changes made.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    private static boolean updateStudentProfile(Connection conn, int studentId, String name, String email, String password, String phoneNumber) throws SQLException {
        String query = "UPDATE students SET name = ?, email = ?, password = ?, phoneNumber = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password));
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, studentId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 🛡 Hashing Password (Placeholder for actual hashing method)
    private static String hashPassword(String password) {
        return password; // Replace with hashing logic (e.g., BCrypt)
    }

    // 📌 Generic input validation method (allows keeping existing data)
    private static String getValidInput(Scanner sc, String message, InputValidator validator, String existingValue) {
        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return existingValue;
            if (validator.validate(input)) return input;
            System.out.println("❌ Invalid input. Please try again.");
        }
    }

    // 📧 Validate Email & Ensure Uniqueness
    private static String getValidEmail(Scanner sc, String currentEmail, int studentId, Connection conn) throws SQLException {
        while (true) {
            String email = getValidInput(sc, "Enter New Email (leave blank to keep existing): ", UpdateProfile::isValidEmail, currentEmail);
            if (!email.equalsIgnoreCase(currentEmail) && emailExists(email, studentId, conn)) {
                System.out.println("❌ This email is already in use. Please enter a different email.");
            } else {
                return email;
            }
        }
    }

    // 🔑 Get & Confirm Password
    private static String getValidPassword(Scanner sc, String currentPassword) {
        while (true) {
            System.out.print("Enter New Password (leave blank to keep existing): ");
            String password = sc.nextLine().trim();

            if (password.isEmpty()) {
                return currentPassword; // Keep existing password if input is empty
            }

            System.out.print("Confirm New Password: ");
            String confirmPassword = sc.nextLine().trim();

            if (password.equals(confirmPassword)) {
                return password; // Return new password if confirmed
            }

            System.out.println("❌ Passwords do not match. Try again.");
        }
    }

    // 📞 Validate Phone Number & Ensure Uniqueness
    private static String getValidPhoneNumber(Scanner sc, String currentPhoneNumber, int studentId, Connection conn) throws SQLException {
        while (true) {
            String phoneNumber = getValidInput(sc, "Enter New Phone Number (leave blank to keep existing): ", UpdateProfile::isValidPhoneNumber, currentPhoneNumber);
            if (!phoneNumber.equals(currentPhoneNumber) && phoneNumberExists(phoneNumber, studentId, conn)) {
                System.out.println("❌ This phone number is already in use. Please enter a different number.");
            } else {
                return phoneNumber;
            }
        }
    }

    // ✅ Validators
    private static boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[a-z0-9._%+-]+@gmail\\.com$", email);
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    // 🔍 Check if email exists (excluding current user)
    private static boolean emailExists(String email, int studentId, Connection conn) throws SQLException {
        String query = "SELECT 1 FROM students WHERE email = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 🔍 Check if phone number exists (excluding current user)
    private static boolean phoneNumberExists(String phoneNumber, int studentId, Connection conn) throws SQLException {
        String query = "SELECT 1 FROM students WHERE phoneNumber = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phoneNumber);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Functional interface for input validation
    @FunctionalInterface
    interface InputValidator {
        boolean validate(String input);
    }
}
