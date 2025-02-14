package Student;

import java.util.Scanner;

public class StudentDashboard {

    public static void studentDashboard(Scanner sc, int studentId) {
        int choice;

        // Fetch student email using the student ID
        String studentEmail = FetchStudentEmail.getEmailById(studentId);

        // Check if the email was fetched successfully
        if (studentEmail != null) {
//                System.out.println("Logged in as: " + studentEmail);
        } else {
            System.out.println("❌ Could not fetch email for student ID: " + studentId);
            return;
        }

        while (true) {
            System.out.println("\nStudent Dashboard:");
            System.out.println("1. View Available Courses");
            System.out.println("2. View Enrolled Courses");
            System.out.println("3. Enroll in a Course");
            System.out.println("4. Unenroll from a Course");
            System.out.println("5. Provide Course Feedback");
            System.out.println("6. Update Profile");
            System.out.println("7. View Profile");
            System.out.println("8. Logout");

            System.out.print("Enter your choice: ");

            if (sc.hasNextInt()) {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } else {
                System.out.println("❌ Invalid input. Please enter a number between 1 and 8.");
                sc.nextLine(); // Consume the invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    ViewAvailableCourses.viewAvailableCourses(sc, studentId);
                    break;
                case 2:
                    ViewEnrolledCourses.viewEnrolledCourses(sc, studentId);
                    break;
                case 3:
                    EnrollCourse.enrollInCourse(sc, studentId);  // Corrected method name
                    break;
                case 4:
                    UnenrollCourse.unenrollCourse(sc, studentId);
                    break;
                case 5:
                    ProvideCourseFeedback.provideFeedback(sc, studentId);
                    break;
                case 6:
                    UpdateProfile.updateProfile(sc, studentId);
                    break;
                case 7:
                    ViewProfile.displayProfile(sc, studentEmail);  // Pass email instead of ID
                    break;
                case 8:
                    System.out.println("Logging out...");
                    return; // Exits the method and logs out
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        }
    }
}
