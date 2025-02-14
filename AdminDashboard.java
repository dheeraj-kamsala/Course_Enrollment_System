package Admin;

import java.util.Scanner;

public class AdminDashboard {
    public static void adminDashboard(Scanner sc, int adminId) { // Add adminId as a parameter
        int choice = 0;
        boolean validInput = false;

        while (true) {
            // Admin Dashboard menu
            System.out.println("\nAdmin Dashboard:");
            System.out.println("1. Add New Course");
            System.out.println("2. View All Courses");
            System.out.println("3. View All Enrolled Students");
            System.out.println("4. View Course Feedback");
            System.out.println("5. Update Course Details");
            System.out.println("6. Delete Course");
            System.out.println("7. View Students in a Specific Course");
            System.out.println("8. Generate Reports");
            System.out.println("9. Update Admin Profile");  // New option for updating profile
            System.out.println("10. Logout");

            // Input choice
            System.out.print("Enter your choice (1-10): ");

            // Validate input to ensure it is an integer between 1 and 10
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                if (choice >= 1 && choice <= 10) {
                    validInput = true;
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.next(); // Consume the invalid input
            }

            // Process the valid choice
            if (validInput) {
                switch (choice) {
                    case 1:
                        AddCourse.addCourse(sc);
                        break;
                    case 2:
                        ViewAllCourses.viewAllCourses(sc);
                        break;
                    case 3:
                        ViewAllEnrolledStudents.viewAllEnrolledStudents(sc);
                        break;
                    case 4:
                        ViewCourseFeedback.viewAllCourseFeedback();
                        break;
                    case 5:
                        UpdateCourseDetails.updateCourseDetails(sc, adminId); // Pass adminId
                        break;
                    case 6:
                        DeleteCourse.deleteCourse(sc, adminId); // Pass adminId
                        break;
                    case 7:
                        ViewStudentsInCourse.viewStudentsInCourse(sc);
                        break;
                    case 8:
                        // Generate Reports
                        GenerateReports.generateAdminReport();  // Call the generateReport method
                        break;
                    case 9:
                        // Update Admin Profile
                        UpdateAdminProfile.updateAdminProfile(sc, adminId); // Pass adminId
                        break;
                    case 10:
                        System.out.println("Logging out...");
                        return; // Exit the loop and return to the main menu
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }
}