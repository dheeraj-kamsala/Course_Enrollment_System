package Admin;

import utils.DatabaseConnection;
import Admin.EmailSenderAdmin;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RegisterAdmin {
    private static final int MAX_OTP_ATTEMPTS = 3;

    public static void registerAdmin(Scanner sc) {
        boolean validInput = false;
        String email = "", username = "", password = "", phoneNumber = "";
        String otp = ""; // Stores the OTP for verification

        while (!validInput) {
            System.out.println("\n📋 Register New Admin :-");

            // Prompt for username
            System.out.print("👤 Enter Username: ");
            username = sc.next();
            username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();

            if (!username.matches("[a-zA-Z]+")) {
                System.out.println("❌ Username must contain only letters.");
                continue;
            }

            // Email validation
            while (true) {
                System.out.print("📧 Enter Email (Gmail only): ");
                email = sc.next();

                if (!Pattern.matches("^[a-z0-9._%+-]+@gmail\\.com$", email)) {
                    System.out.println("❌ Invalid email format. Only Gmail addresses are allowed.");
                    continue;
                }

                if (emailExists(email)) {
                    System.out.println("❌ Email already exists. Please enter a different email.");
                    continue;
                }
                break;
            }

            // Prompt for password
            System.out.print("🔑 Enter Password: ");
            password = sc.next();

            // Phone number validation
            while (true) {
                System.out.print("📞 Enter Phone Number (10 digits): ");
                phoneNumber = sc.next();

                if (!phoneNumber.matches("\\d{10}")) {
                    System.out.println("❌ Phone number must be exactly 10 digits.");
                    continue;
                }

                if (phoneExists(phoneNumber)) {
                    System.out.println("❌ Phone number already exists. Please enter a different one.");
                    continue;
                }
                break;
            }

            // Generate OTP & Send Email Verification
            otp = generateOTP();
            EmailSenderAdmin.sendVerificationEmail(email, otp);

            // Verify OTP with retry & resend options
            if (!verifyOTP(sc, email, otp)) {
                System.out.println("❌ Registration failed due to incorrect OTP.");
                return;
            }

            // Insert new admin into database
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement insertStmt = conn.prepareStatement(
                         "INSERT INTO admins (username, email, password, phone_number) VALUES (?, ?, ?, ?)")) {

                insertStmt.setString(1, username);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.setString(4, phoneNumber);
                insertStmt.executeUpdate();
                System.out.println("✅ Admin registered successfully.");

                validInput = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean verifyOTP(Scanner sc, String email, String otp) {
        int attempts = 0;

        while (attempts < MAX_OTP_ATTEMPTS) {
            System.out.print("📩 Enter the OTP sent to your email: ");
            String enteredOtp = sc.next();

            if (enteredOtp.equals(otp)) {
                System.out.println("✅ OTP Verified Successfully.");
                return true;
            }

            attempts++;
            System.out.println("❌ Incorrect OTP. Attempts left: " + (MAX_OTP_ATTEMPTS - attempts));

            if (attempts < MAX_OTP_ATTEMPTS) {
                int choice;
                while (true) {
                    System.out.print("🔄 Do you want to (1) Retry OTP or (2) Resend OTP? (Enter 1 or 2): ");
                    if (sc.hasNextInt()) {
                        choice = sc.nextInt();
                        if (choice == 1 || choice == 2) break; // Valid input
                    } else {
                        sc.next(); // Clear invalid input
                    }
                    System.out.println("❌ Invalid input. Please enter only 1 (Retry) or 2 (Resend OTP).");
                }

                if (choice == 2) {
                    otp = generateOTP();
                    EmailSenderAdmin.sendVerificationEmail(email, otp);
                    System.out.println("📧 A new OTP has been sent to your email.");
                }
            }
        }
        return false; // OTP verification failed
    }

    private static boolean emailExists(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM admins WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static boolean phoneExists(String phoneNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM admins WHERE phone_number = ?")) {
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}