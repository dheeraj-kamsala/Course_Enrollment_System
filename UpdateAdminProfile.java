package Admin;

import utils.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UpdateAdminProfile {

    public static void updateAdminProfile(Scanner sc, int adminId) {
        String query = "SELECT username, email, phone_number, password FROM admins WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(query)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Error: Could not connect to the database.");
                return;
            }

            checkStmt.setInt(1, adminId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("❌ Error: Admin ID not found. Update not allowed.");
                    return;
                }

                String currentUsername = rs.getString("username");
                String currentEmail = rs.getString("email");
                String currentPhoneNumber = rs.getString("phone_number");
                String currentPassword = rs.getString("password");

                // Show existing profile
                System.out.println("\n===== 📋 Existing Profile =====");
                System.out.println("👤 Name       : " + currentUsername);
                System.out.println("📧 Email      : " + currentEmail);
                System.out.println("📞 Phone      : " + currentPhoneNumber);

                System.out.println("\n🔹 Updating Profile...\n");

                sc.nextLine(); // Consume any leftover newline before taking string inputs

                // Get updated details from the admin
                String newUsername = getValidInput(sc,
                        "Enter New Name (only alphabets and spaces, leave blank to keep existing): ",
                        UpdateAdminProfile::isValidName, currentUsername);

                String newEmail = getValidEmail(sc, currentEmail, adminId, conn);
                String newPassword = getValidPassword(sc, currentPassword);
                String newPhoneNumber = getValidPhoneNumber(sc, currentPhoneNumber, adminId, conn);

                // Update Profile
                if (updateAdminProfile(conn, adminId, newUsername, newEmail, newPassword, newPhoneNumber)) {
                    System.out.println("✅ Profile updated successfully!");
                } else {
                    System.out.println("❌ Error: No changes made.");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }

    private static boolean updateAdminProfile(Connection conn, int adminId, String username, String email, String password, String phone_number) throws SQLException {
        String query = "UPDATE admins SET username = ?, email = ?, password = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password));
            stmt.setString(4, phone_number);
            stmt.setInt(5, adminId);
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
    private static String getValidEmail(Scanner sc, String currentEmail, int adminId, Connection conn) throws SQLException {
        while (true) {
            String email = getValidInput(sc, "Enter New Email (leave blank to keep existing): ", UpdateAdminProfile::isValidEmail, currentEmail);
            if (!email.equalsIgnoreCase(currentEmail) && emailExists(email, adminId, conn)) {
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
            if (password.isEmpty()) return currentPassword;

            System.out.print("Confirm New Password: ");
            String confirmPassword = sc.nextLine().trim();

            if (password.equals(confirmPassword)) {
                return password;
            }
            System.out.println("❌ Passwords do not match. Try again.");
        }
    }

    // 📞 Validate Phone Number & Ensure Uniqueness
    private static String getValidPhoneNumber(Scanner sc, String currentPhoneNumber, int adminId, Connection conn) throws SQLException {
        while (true) {
            String phone_number = getValidInput(sc, "Enter New Phone Number (10 digits, leave blank to keep existing): ",
                    UpdateAdminProfile::isValidPhoneNumber, currentPhoneNumber);
            if (!phone_number.equals(currentPhoneNumber) && phone_numberExists(phone_number, adminId, conn)) {
                System.out.println("❌ This phone number is already in use. Please enter a different number.");
            } else {
                return phone_number;
            }
        }
    }

    // ✅ Validators
    private static boolean isValidName(String username) {
        return username.matches("[a-zA-Z ]+");
    }

    private static boolean isValidEmail(String email) {
        return Pattern.matches("^[a-z0-9._%+-]+@gmail\\.com$", email);
    }

    private static boolean isValidPhoneNumber(String phone_number) {
        return phone_number.matches("\\d{10}");
    }

    // 🔍 Check if email exists (excluding current user)
    private static boolean emailExists(String email, int adminId, Connection conn) throws SQLException {
        String query = "SELECT 1 FROM admins WHERE email = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setInt(2, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // 🔍 Check if phone number exists (excluding current user)
    private static boolean phone_numberExists(String phone_number, int adminId, Connection conn) throws SQLException {
        String query = "SELECT 1 FROM admins WHERE phone_number = ? AND id != ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone_number);
            stmt.setInt(2, adminId);
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
