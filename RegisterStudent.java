package Student;

import utils.DatabaseConnection;
import Student.EmailSenderStudent;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RegisterStudent {
    private static final int MAX_OTP_ATTEMPTS = 3;

    public static void registerStudent(Scanner sc) {
        boolean validInput = false;
        String email = "", name = "", password = "", phoneNumber = "";
        String otp = ""; // Stores the OTP for verification

        while (!validInput) {
            System.out.println("\n📋 Register New Student3 :-");

            // Prompt for username
            System.out.print("👤 Enter Username: ");
            name = sc.next();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            if (!name.matches("[a-zA-Z]+")) {
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
            EmailSenderStudent.sendVerificationEmail(email, otp);

            // Verify OTP with retry & resend options
            if (!verifyOTP(sc, email, otp)) {
                System.out.println("❌ Registration failed due to incorrect OTP.");
                return;
            }

            // Insert new admin into database
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement insertStmt = conn.prepareStatement(
                         "INSERT INTO students (name, email, password, phoneNumber) VALUES (?, ?, ?, ?)");
            ) {
                //  "INSERT INTO students (name, email, password, phoneNumber) VALUES (?, ?, ?, ?)");

                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.setString(4, phoneNumber);
                insertStmt.executeUpdate();
                System.out.println("✅ Student registered successfully.");

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
                System.out.print("🔄 Do you want to (1) Retry OTP or (2) Resend OTP? (Enter 1 or 2): ");
                int choice = sc.nextInt();

                if (choice == 2) {
                    otp = generateOTP();
                    EmailSenderStudent.sendVerificationEmail(email, otp);
                    System.out.println("📧 A new OTP has been sent to your email.");
                }
            }
        }
        return false; // OTP verification failed
    }

    private static boolean emailExists(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM students WHERE email = ?")) {
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
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM students WHERE phoneNumber = ?")) {
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