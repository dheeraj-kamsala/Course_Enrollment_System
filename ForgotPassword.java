//package Student;
//
//import utils.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Random;
//import java.util.Scanner;
//
//    public class ForgotPassword{
//
//
//            public static void resetPassword(Scanner sc) {
//                int choice = 1; // Automatically go to "Forgot Password"
//                sc.nextLine();  // Consume newline
//
//                if (choice == 1) {
//                    requestPasswordReset(sc);
//                }
//            }
//
//            // Forgot Password Method
//            private static void requestPasswordReset(Scanner sc) {
//                System.out.println("\n===== Forgot Password =====");
//                System.out.print("Enter your registered Email: ");
//                String email = sc.next().trim();
//
//                if (!isEmailRegistered(email)) {
//                    System.out.println("❌ Email not found in our system.");
//                    return;
//                }
//
//                String verificationCode = generateVerificationCode();
//                if (sendVerificationCode(email, verificationCode)) {
//                    System.out.println("✅ A verification code has been sent to your email.");
//                    System.out.print("Enter the verification code to reset your password: ");
//                    String enteredCode = sc.next().trim();
//
//                    if (!enteredCode.equals(verificationCode)) {
//                        System.out.println("❌ Invalid verification code.");
//                        return;
//                    }
//
//                    System.out.print("Enter new password: ");
//                    String newPassword = sc.next().trim();
//
//                    System.out.print("Confirm password: ");
//                    String confirmPassword = sc.next().trim();
//
//                    if (!newPassword.equals(confirmPassword)) {
//                        System.out.println("❌ Passwords do not match. Try again.");
//                        return;
//                    }
//
//                    if (updatePassword(email, newPassword)) {
//                        System.out.println("✅ Password updated successfully!");
//                    } else {
//                        System.out.println("❌ Failed to reset password. Try again later.");
//                    }
//                } else {
//                    System.out.println("❌ Failed to send verification code. Try again later.");
//                }
//            }
//
//            private static boolean isEmailRegistered(String email) {
//                String query = "SELECT COUNT(*) FROM students WHERE email = ?";
//                try (Connection conn = DatabaseConnection.getConnection();
//                     PreparedStatement stmt = conn.prepareStatement(query)) {
//                    stmt.setString(1, email);
//                    ResultSet rs = stmt.executeQuery();
//                    return rs.next() && rs.getInt(1) > 0;
//                } catch (SQLException e) {
//                    System.out.println("❌ Database error: " + e.getMessage());
//                }
//                return false;
//            }
//
//            private static boolean updatePassword(String email, String newPassword) {
//                String query = "UPDATE students SET password = ? WHERE email = ?";
//                try (Connection conn = DatabaseConnection.getConnection();
//                     PreparedStatement stmt = conn.prepareStatement(query)) {
//                    stmt.setString(1, newPassword);  // Hash password in real applications
//                    stmt.setString(2, email);
//                    return stmt.executeUpdate() > 0;
//                } catch (SQLException e) {
//                    System.out.println("❌ Database error: " + e.getMessage());
//                }
//                return false;
//            }
//
//            private static String generateVerificationCode() {
//                String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//                StringBuilder code = new StringBuilder();
//                Random rnd = new Random();
//                for (int i = 0; i < 6; i++) { // Generate a 6-character code
//                    code.append(chars.charAt(rnd.nextInt(chars.length())));
//                }
//                return code.toString();
//            }
//
//            private static boolean sendVerificationCode(String email, String verificationCode) {
//                // In real-world applications, you'd send the verification code via email.
//                System.out.println("Mock email sent to " + email + " with verification code: " + verificationCode);
//                return true;
//            }
//        }


//        public static void resetPassword(Scanner sc) {
////            System.out.println("\n===== Password Management =====");
////            System.out.println("1. Forgot Password");
////            System.out.println("2. Change Password");
////            System.out.print("Enter your choice: ");
//            int choice = 1;
//            sc.nextLine();  // Consume newline
//
//            switch (choice) {
//                case 1:
//                    requestPasswordReset(sc);
////                    break;
////                case 2:
////                    changePassword(sc);
//////                    break;
////                default:
////                    System.out.println("❌ Invalid choice. Please try again.");
//            }
//        }
//
//        // Forgot Password Method
//        private static void requestPasswordReset(Scanner sc) {
//            System.out.println("\n===== Forgot Password =====");
//            System.out.print("Enter your registered Email: ");
//            String email = sc.next().trim();
//
//            if (!isEmailRegistered(email)) {
//                System.out.println("❌ Email not found in our system.");
//                return;
//            }
//
//            String verificationCode = generateVerificationCode();
//            if (sendVerificationCode(email, verificationCode)) {
//                System.out.println("✅ A verification code has been sent to your email.");
//                System.out.print("Enter the verification code to reset your password: ");
//                String enteredCode = sc.next().trim();
//
//                if (!enteredCode.equals(verificationCode)) {
//                    System.out.println("❌ Invalid verification code.");
//                    return;
//                }
//
//                System.out.print("Enter new password: ");
//                String newPassword = sc.next().trim();
//
//                System.out.print("Confirm  password: ");
//                String confirmPassword = sc.next().trim();
//
//                if (!newPassword.equals(confirmPassword)) {
//                    System.out.println("❌ Passwords do not match. Try again.");
//                    return;
//                }
//
//                if (updatePassword(email, newPassword)) {
//                    System.out.println("✅ Password updated successfully!");
//                } else {
//                    System.out.println("❌ Failed to reset password. Try again later.");
//                }
//            } else {
//                System.out.println("❌ Failed to send verification code. Try again later.");
//            }
//        }
//
//        // Change Password Method
//        private static void changePassword(Scanner sc) {
//            System.out.println("\n===== Change Password =====");
//            System.out.print("Enter your registered Email: ");
//            String email = sc.next().trim();
//
//            System.out.print("Enter your current password: ");
//            String currentPassword = sc.next().trim();
//
//            if (!verifyOldPassword(email, currentPassword)) {
//                System.out.println("❌ Incorrect current password.");
//                return;
//            }
//
//            System.out.print("Enter new password: ");
//            String newPassword = sc.next().trim();
//
//            System.out.print("Confirm new password: ");
//            String confirmPassword = sc.next().trim();
//
//            if (!newPassword.equals(confirmPassword)) {
//                System.out.println("❌ Passwords do not match. Try again.");
//                return;
//            }
//
//            if (updatePassword(email, newPassword)) {
//                System.out.println("✅ Password updated successfully!");
//            } else {
//                System.out.println("❌ Failed to change password. Try again later.");
//            }
//        }
//
//        private static boolean isEmailRegistered(String email) {
//            String query = "SELECT COUNT(*) FROM students WHERE email = ?";
//            try (Connection conn = DatabaseConnection.getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, email);
//                ResultSet rs = stmt.executeQuery();
//                return rs.next() && rs.getInt(1) > 0;
//            } catch (SQLException e) {
//                System.out.println("❌ Database error: " + e.getMessage());
//            }
//            return false;
//        }
//
//        private static boolean verifyOldPassword(String email, String oldPassword) {
//            String query = "SELECT COUNT(*) FROM students WHERE email = ? AND password = ?";
//            try (Connection conn = DatabaseConnection.getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, email);
//                stmt.setString(2, oldPassword); // In real applications, compare the hashed password
//                ResultSet rs = stmt.executeQuery();
//                return rs.next() && rs.getInt(1) > 0;
//            } catch (SQLException e) {
//                System.out.println("❌ Database error: " + e.getMessage());
//            }
//            return false;
//        }
//
//        private static boolean updatePassword(String email, String newPassword) {
//            String query = "UPDATE students SET password = ? WHERE email = ?";
//            try (Connection conn = DatabaseConnection.getConnection();
//                 PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, newPassword);  // Hash password in real applications
//                stmt.setString(2, email);
//                return stmt.executeUpdate() > 0;
//            } catch (SQLException e) {
//                System.out.println("❌ Database error: " + e.getMessage());
//            }
//            return false;
//        }
//
//        private static String generateVerificationCode() {
//            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//            StringBuilder code = new StringBuilder();
//            Random rnd = new Random();
//            for (int i = 0; i < 6; i++) { // Generate a 6-character code
//                code.append(chars.charAt(rnd.nextInt(chars.length())));
//            }
//            return code.toString();
//        }
//
//        private static boolean sendVerificationCode(String email, String verificationCode) {
//            // In real-world applications, you'd send the verification code via email.
//            System.out.println("Mock email sent to " + email + " with verification code: " + verificationCode);
//            return true;
//        }
//    }
//
//
//



package Student;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import jakarta.mail.*;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import utils.DatabaseConnection;
//import javax.mail.*;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public class ForgotPassword {

    // Gmail Credentials (Replace with your actual credentials)
    public static final String GMAIL_USERNAME = "courstechnologies@gmail.com";
    public static final String GMAIL_PASSWORD = "ptoo pguy gdph vyjh";  // Use App Password

    public static void resetPassword(Scanner sc) {
        System.out.println("\n===== Forgot Password =====");
        System.out.print("Enter your registered Email: ");
        String email = sc.next().trim();

        if (!isEmailRegistered(email)) {
            System.out.println("❌ Email not found.");
            return;
        }

        String otpCode = generateOTP();
        boolean otpSent = sendOTPViaEmail(email, otpCode);

        if (!otpSent) {
            System.out.println("❌ OTP sending failed.");
            return;
        }

        System.out.println("✅ OTP has been sent to your email.");
        System.out.print("Enter the OTP: ");
        String enteredOTP = sc.next().trim();

        if (!enteredOTP.equals(otpCode)) {
            System.out.println("❌ Invalid OTP. Try again.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = sc.next().trim();

        if (updatePassword(email, newPassword)) {
            System.out.println("✅ Password updated successfully!");
        } else {
            System.out.println("❌ Failed to reset password.");
        }
    }

    private static boolean isEmailRegistered(String email) {
        String query = "SELECT COUNT(*) FROM students WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
        return false;
    }

    private static boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE students SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
        return false;
    }

    private static String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private static boolean sendOTPViaEmail(String email, String otp) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("courstechnologies@gmail.com", "ptoo pguy gdph vyjh");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom();
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP code is: " + otp);

            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error sending OTP via Email: " + e.getMessage());
            return false;
        }
    }
}
