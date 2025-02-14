package Student;

import utils.DatabaseConnection;
import Student.EmailSender;
import java.sql.*;
import java.util.*;

public class EnrollCourse {

    public static void enrollInCourse(Scanner sc, int studentId) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String> selectedCourses = new ArrayList<>();
        double totalFee = 0;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Database connection is not valid.");
                return;
            }

            // Fetch all available courses
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM courses");
            boolean coursesAvailable = false;

            while (rs.next()) {
                if (!coursesAvailable) {
                    System.out.println("\nEnrollCourse :");
                    System.out.println("Courses Available :");
                    coursesAvailable = true;
                }
                System.out.println("┌──────────┬───────────────────────────┬──────────┬─────────────┬──────────────────────────────────────┐");
                System.out.println("│ Course ID│ Course Name               │ Fee (₹)  │ Duration    │ Description                          │");
                System.out.println("├──────────┼───────────────────────────┼──────────┼─────────────┼──────────────────────────────────────┤");

                Map<String, Integer> courseMap = new HashMap<>(); // Store course names & IDs
                while (rs.next()) {
                    int courseId = rs.getInt("id");
                    String courseName = rs.getString("name").trim().toLowerCase(); // Normalize input
                    courseMap.put(courseName, courseId);

                    // Fetch and clean the description
                    String description = rs.getString("description").replace("\n", " ").trim();
                    String shortDescription = description.length() > 37 ? description.substring(0, 33) + "..." : description;

                    System.out.printf("│ %-9d│ %-26s│ %-9.2f│ %-12d│ %-37s│\n",
                            courseId,
                            rs.getString("name"),
                            rs.getDouble("fee"),
                            rs.getInt("duration"),
                            shortDescription);
                }
                System.out.println("└──────────┴───────────────────────────┴──────────┴─────────────┴──────────────────────────────────────┘");
            }

            if (!coursesAvailable) {
                System.out.println("No Courses Available.");
                return;
            }

            // Ask user to search for a course
            boolean foundCourses = false;
            while (true) { // Loop to allow multiple searches
                System.out.print("\nEnter a keyword to search for related courses (or type 'back' to return): ");
                String searchKeyword = sc.nextLine().toLowerCase();

                if (searchKeyword.equalsIgnoreCase("back")) {
                    System.out.println("Returning to Student Dashboard...");
                    return; // Exit the search loop and return to the dashboard
                }

                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM courses WHERE LOWER(name) LIKE '%" + searchKeyword + "%'");

                foundCourses = false;
                while (rs.next()) {
                    if (!foundCourses) {
                        System.out.println("\nRelated Courses:");
                        foundCourses = true;
                    }
                    System.out.println("Course Name: " + rs.getString("name") + " - Fee: $" + rs.getDouble("fee") + " - Duration: " + rs.getInt("duration") + " weeks");
                }

                if (!foundCourses) {
                    System.out.println("❌ No related courses found for: " + searchKeyword);
                    String retryChoice;

                    // Input validation loop for retry choice
                    while (true) {
                        System.out.print("Do you want to search again? (yes/no): ");
                        retryChoice = sc.nextLine().trim().toLowerCase();

                        if (retryChoice.equals("yes") || retryChoice.equals("y")) {
                            break; // Exit the loop to allow a new search
                        } else if (retryChoice.equals("no") || retryChoice.equals("n")) {
                            System.out.println("Returning to Student Dashboard...");
                            break; // Exit the loop and return to the dashboard
                        } else {
                            System.out.println("❌ Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }

                    if (retryChoice.equals("no") || retryChoice.equals("n")) {
                        return; // Exit search loop if the user does not want to search again
                    }
                } else {
                    break; // Exit search loop if courses are found
                }
            }




// Modified part for course selection
            boolean selectingCourses = true;
            while (selectingCourses) {
                System.out.print("\nEnter the exact Course Name to enroll (or type 'done' to finish): ");
                String selectedCourse = sc.nextLine().trim();

                if (selectedCourse.equalsIgnoreCase("done")) {
                    selectingCourses = false;
                    continue;
                }

                // Validate selected course
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT * FROM courses WHERE LOWER(name) = '" + selectedCourse.toLowerCase() + "'");

                if (rs.next()) {
                    // Check if the student is already enrolled in this course
                    if (isAlreadyEnrolled(conn, studentId, selectedCourse)) {
                        System.out.println("❌ You are already enrolled in the course: " + selectedCourse);
                        continue;
                    }

                    // Check if the course is already selected by the student
                    boolean courseExists = false;
                    for (String course : selectedCourses) {
                        if (course.equalsIgnoreCase(selectedCourse)) {
                            courseExists = true;
                            break;
                        }
                    }

                    if (courseExists) {
                        System.out.println("❌ You have already selected this course: " + selectedCourse);
                        continue;
                    }

                    System.out.println("\nYou selected: " + rs.getString("name"));
                    System.out.println("Fee: $" + rs.getDouble("fee"));
                    System.out.println("Duration: " + rs.getInt("duration") + " weeks");

                    // Add course to selectedCourses list
                    selectedCourses.add(selectedCourse.toLowerCase());  // Make sure the course name is stored consistently

                    totalFee += rs.getDouble("fee");

                    // Ask if the user wants to select another course
                    System.out.print("\nDo you want to choose another course? (yes/no): ");
                    String choice = sc.nextLine().trim().toLowerCase();

                    // Keep asking until a valid "yes" or "no" answer is provided
                    while (!(choice.equals("yes") || choice.equals("y") || choice.equals("no") || choice.equals("n"))) {
                        System.out.print("Invalid input. Please type 'yes' or 'no': ");
                        choice = sc.nextLine().trim().toLowerCase();
                    }

                    if (choice.equals("no") || choice.equals("n")) {
                        selectingCourses = false;
                    }
                } else {
                    System.out.println("❌ Invalid course name. Please enter a valid course from the list.");
                }
            }





            // View and confirm selected courses
            if (!selectedCourses.isEmpty()) {
                System.out.println("\nYou have selected the following courses:");
                for (String course : selectedCourses) {
                    System.out.println(course);
                }
                System.out.println("Total Fee: $" + totalFee);

                // Confirm Enrollment
                System.out.print("\nDo you want to proceed with payment? (yes/no): ");
                String enrollChoice = sc.nextLine().trim().toLowerCase();

                // Keep asking until a valid "yes" or "no" answer is provided
                while (true) {
                    if (enrollChoice.equals("yes") || enrollChoice.equals("y")) {
                        proceedToPayment(sc, selectedCourses, totalFee, studentId);  // Pass selected courses and total fee
                        break;  // Exit the loop after proceeding to payment
                    } else if (enrollChoice.equals("no") || enrollChoice.equals("n")) {
                        System.out.println("Returning to Student Dashboard...");
                        return; // Exit the function and return to the dashboard
                    } else {
                        System.out.print("Invalid input. Please type 'yes' or 'no': ");
                        enrollChoice = sc.nextLine().trim().toLowerCase();  // Prompt again for valid input
                    }
                }
            } else {
                System.out.println("No courses selected.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private static boolean isAlreadyEnrolled(Connection conn, int studentId, String courseName) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND LOWER(course_name) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, courseName.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // If a record is found, student is already enrolled
        }
    }

    private static void proceedToPayment(Scanner sc, List<String> selectedCourses, double totalFee, int studentId) {
        System.out.println("\nYou have selected the following courses:");
        for (String course : selectedCourses) {
            System.out.println(course);
        }
        System.out.println("Total Fee: $" + totalFee);

        boolean paymentSuccessful = false;
        while (!paymentSuccessful) {
            System.out.println("\nChoose a payment method:");
            System.out.println("1. Credit/Debit Card");
            System.out.println("2. PayPal");
            System.out.println("3. Mobile Wallet (Google Pay, Apple Pay)");
            System.out.println("4. Cancel");

            System.out.print("\nEnter your choice (1/2/3/4): ");
            int paymentChoice = -1;

            // Input validation loop
            while (paymentChoice < 1 || paymentChoice > 4) {
                try {
                    paymentChoice = sc.nextInt();
                    sc.nextLine();  // Consume the newline
                    if (paymentChoice < 1 || paymentChoice > 4) {
                        System.out.println("❌ Invalid choice. Please enter a valid option.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("❌ Invalid input. Please enter a number between 1 and 4.");
                    sc.nextLine(); // Clear the buffer
                }
            }

            switch (paymentChoice) {
                case 1:
                    if (handleCreditCardPayment(sc, totalFee)) {
                        paymentSuccessful = true;
                        updateEnrollmentInDatabase(selectedCourses, "Credit/Debit Card", studentId, totalFee);
                    }
                    break;
                case 2:
                    if (handlePayPalPayment(sc, totalFee)) {
                        paymentSuccessful = true;
                        updateEnrollmentInDatabase(selectedCourses, "PayPal", studentId, totalFee);
                    }
                    break;
                case 3:
                    if (handleMobileWalletPayment(sc, totalFee)) {
                        paymentSuccessful = true;
                        updateEnrollmentInDatabase(selectedCourses, "Mobile Wallet", studentId, totalFee);
                    }
                    break;
                case 4:
                    System.out.println("❌ Payment cancelled. Returning to Student Dashboard.");
                    return;
                default:
                    // This case will never be reached due to the while condition.
                    break;
            }
        }
    }

    private static boolean handleCreditCardPayment(Scanner sc, double courseFee) {
        System.out.println("\nYou selected Credit/Debit Card payment.");

        // Card Number input validation (basic)
        String cardNumber = "";
        while (cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")) {
            System.out.print("Enter your Card Number (16 digits): ");
            cardNumber = sc.nextLine();
            if (cardNumber.length() != 16 || !cardNumber.matches("[0-9]+")) {
                System.out.println("❌ Invalid card number. Please enter a valid 16-digit card number.");
            }
        }

        // Expiry Date input validation (basic MM/YY format)
        String expiryDate = "";
        while (!expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            System.out.print("Enter Expiry Date (MM/YY): ");
            expiryDate = sc.nextLine();
            if (!expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
                System.out.println("❌ Invalid expiry date. Please enter a valid date in MM/YY format.");
            }
        }

        // CVV input validation (3 digits)
        String cvv = "";
        while (cvv.length() != 3 || !cvv.matches("[0-9]+")) {
            System.out.print("Enter CVV (3 digits): ");
            cvv = sc.nextLine();
            if (cvv.length() != 3 || !cvv.matches("[0-9]+")) {
                System.out.println("❌ Invalid CVV. Please enter a valid 3-digit CVV.");
            }
        }

        return true;
    }

    private static boolean handlePayPalPayment(Scanner sc, double courseFee) {
        System.out.println("\nYou selected PayPal payment.");

        // PayPal Email input validation (basic format)
        String email = "";
        while (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            System.out.print("Enter your PayPal Email: ");
            email = sc.nextLine();
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                System.out.println("❌ Invalid email format. Please enter a valid PayPal email.");
            }
        }

        // PayPal Password input validation (just a basic check for length)
        String password = "";
        while (password.length() < 3) {
            System.out.print("Enter your PayPal Password (at least 3 digit): ");
            password = sc.nextLine();
            if (password.length() < 3) {
                System.out.println("❌ Password is too short. Please enter at least 3 digit.");
            }
        }

        return true;
    }

    private static boolean handleMobileWalletPayment(Scanner sc, double courseFee) {
        System.out.println("\nYou selected Mobile Wallet payment.");

        // Mobile Wallet ID input validation (basic check)
        String walletId = "";
        while (walletId.isEmpty()) {
            System.out.print("Enter your Mobile Wallet ID: ");
            walletId = sc.nextLine();
            if (walletId.isEmpty()) {
                System.out.println("❌ Wallet ID cannot be empty.");
            }
        }

        // Wallet PIN input validation (basic check for 4-digit PIN)
        String walletPin = "";
        while (walletPin.length() != 4 || !walletPin.matches("[0-9]+")) {
            System.out.print("Enter your Wallet PIN (4 digits): ");
            walletPin = sc.nextLine();
            if (walletPin.length() != 4 || !walletPin.matches("[0-9]+")) {
                System.out.println("❌ Invalid PIN. Please enter a valid 4-digit PIN.");
            }
        }

        return true;
    }
    private static void updateEnrollmentInDatabase(List<String> selectedCourses, String paymentMethod, int studentId , double totalFee) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Database connection is not valid.");
                return;
            }

            // Insert new enrollment for each selected course with course fee
            for (String courseName : selectedCourses) {
                String sql = "INSERT INTO enrollments (student_id, course_id, course_name, payment_method, fees_paid) " +
                        "SELECT ?, id, ?, ?, ? FROM courses WHERE LOWER(name) = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, studentId);
                stmt.setString(2, courseName);
                stmt.setString(3, paymentMethod);
                stmt.setDouble(4, totalFee); // Assuming courseFee is the fee amount you want to insert
                stmt.setString(5, courseName.toLowerCase());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Successfully enrolled in the course: " + courseName);
                } else {
                    System.out.println("❌ Enrollment failed for course: " + courseName);
                }
                sendConfirmationEmail(studentId, selectedCourses, totalFee);
                // After successful enrollment, insert payment record into the payments table
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
    }

    private static void insertPaymentRecord(Connection conn, int studentId, String courseName, String paymentMethod, double totalFee) {
        PreparedStatement stmt = null;
        try {
            // Generate a unique transaction ID (for simplicity, we'll use a timestamp)
            String transactionId = "TXN" + System.currentTimeMillis();

            String sql = "INSERT INTO payments (student_id, course_id, payment_method, payment_status, transaction_id, payment_date) " +
                    "SELECT ?, id, ?, 'Completed', ?, CURRENT_TIMESTAMP FROM courses WHERE LOWER(name) = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setString(2, paymentMethod);
            stmt.setString(3, transactionId);
            stmt.setString(4, courseName.toLowerCase());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Payment successfully recorded for course: " + courseName);
            } else {
                System.out.println("❌ Failed to record payment for course: " + courseName);
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error while recording payment: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    private static void sendConfirmationEmail(int studentId, List<String> courses, double totalFee) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, email FROM students WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("name");
                String email = rs.getString("email");

                String subject = "Enrollment Confirmation";
                StringBuilder body = new StringBuilder("Dear " + studentName + ",\n\n");
                body.append("You have successfully enrolled in the following courses:\n");
                for (String course : courses) {
                    body.append("- ").append(course).append("\n");
                }
                body.append("\nTotal Fee Paid: $").append(totalFee).append("\n\n");
                body.append("Thank you for enrolling with us!\n\nBest regards,\nYour Institute");

                EmailSender.sendEmail(email, subject, body.toString());
                System.out.println("✅ Enrollment confirmation email sent to: " + email);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching student details: " + e.getMessage());
        }
    }
}