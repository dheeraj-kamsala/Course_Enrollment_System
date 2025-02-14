import Admin.*;
import Student.*;
import utils.DatabaseConnection;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Establish connection to the database
        DatabaseConnection.connectToDatabase();

        // Main menu loop
        while (true) {
            System.out.println("\nWelcome to the Course Enrollment System");
            System.out.println("1. Admin");
            System.out.println("2. Student");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1, 2, or 3): ");

            // Validate user input to ensure it's an integer
            int choice = 0;
            boolean validChoice = false;
            while (!validChoice) {
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    validChoice = true;
                } else {
                    System.out.println("Invalid input. Please enter a valid integer choice.");
                    sc.next(); // Consume the invalid input
                    break;
                }
            }

            // If the input was valid, process the choice
            if (validChoice) {
                switch (choice) {
                    case 1:
                        // Admin Menu
                        adminMenu(sc);
                        break;
                    case 2:
                        // Student Menu
                        studentMenu(sc);
                        break;
                    case 3:
                        System.out.println("Goodbye!");
                        DatabaseConnection.closeConnection();
                        sc.close();
                        return;  // Exit the program
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            }
        }
    }

    // Admin Menu
    // Admin Menu
    private static int adminMenu(Scanner sc) {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Admin Login");
            System.out.println("2. Register as Admin");
            System.out.println("3. Forgot Password");
            System.out.println("4. Go Back to Main Menu");
            System.out.print("Enter your choice (1, 2, 3,or 4): ");

            int choice = getValidChoice(sc, 1, 4);

            if (choice == -1) {
                return -1; // Return -1 to go back to the main menu
            }

            switch (choice) {
                case 1:
                    AdminLogin.adminLogin(sc);
                    break;
                case 2:
                    RegisterAdmin.registerAdmin(sc);
                    break;
                case 3:
                    Forgotpass.resetPassword(sc);
                    break;
                case 4:
                    System.out.println("Returning to Main Menu...");
                    return -1; // Go back to the main menu
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3,or 4.");
            }
        }
    }

    // Student Menu
    private static int studentMenu(Scanner sc) {
        while (true) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. Student Login");
            System.out.println("2. Register as Student");
            System.out.println("3. Forgot Password");
            System.out.println("4. Go Back to Main Menu");
            System.out.print("Enter your choice (1, 2, 3, or 4): ");

            int choice = getValidChoice(sc, 1, 4);

            if (choice == -1) {
                return -1; // Return -1 to go back to the main menu
            }

            switch (choice) {
                case 1:
                    StudentLogin.studentLogin(sc);
                    break;
                case 2:
                    RegisterStudent.registerStudent(sc);
                    break;
                case 3:
                    ForgotPassword.resetPassword(sc);
                    break;
                case 4:
                    System.out.println("Returning to Main Menu...");
                    return -1; // Go back to the main menu
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3 or 4.");
            }
        }
    }

    // Helper method to validate input

    private static int getValidChoice(Scanner sc, int min, int max) {
        while (true) {
            if (sc.hasNextInt()) {
                int choice = sc.nextInt();
                if (choice >= min && choice <= max) {
                    return choice; // Valid choice
                } else {
                    System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input. Only numbers are allowed. Please try again.");
                sc.next(); // Consume the invalid input
            }

        }
    }
}
