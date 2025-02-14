//package Student;
//
//import utils.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.Scanner; // <-- Add this import statement
//
//public class ViewProfile {
//
//    public static void displayProfile(Scanner sc, String studentEmail) {
//        System.out.println("\n===== Student Profile =====");
//
//        String query = "SELECT name, email, phoneNumber FROM students WHERE email = ?";
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = DatabaseConnection.getConnection();
//            if (conn == null || conn.isClosed()) {
//                System.out.println("❌ Database connection is closed.");
//                return;
//            }
//
//            stmt = conn.prepareStatement(query);
//            stmt.setString(1, studentEmail);  // Use the email parameter
//            rs = stmt.executeQuery();
//
//            if (rs.next()) {
//                System.out.println("👤 Name: " + rs.getString("name"));
//                System.out.println("📧 Email: " + rs.getString("email"));
//                System.out.println("📞 Phone: " + rs.getString("phoneNumber"));
//            } else {
//                System.out.println("❌ No profile found for this email.");
//            }
//        } catch (SQLException e) {
//            System.out.println("❌ Database error: " + e.getMessage());
//        } finally {
//            closeResources(rs, stmt, conn);
//        }
//    }
//
//    private static void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
//        try {
//            if (rs != null) rs.close();
//            if (stmt != null) stmt.close();
//            if (conn != null && !conn.isClosed()) conn.close();
//        } catch (SQLException e) {
//            System.out.println("Error closing resources: " + e.getMessage());
//        }
//    }
//}



package Student;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ViewProfile {

    public static void displayProfile(Scanner sc, String studentEmail) {
        System.out.println("\n===== 🎓 Student Profile =====");

        String query = "SELECT name, email, phoneNumber FROM students WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Database connection is unavailable.");
                return;
            }

            stmt.setString(1, studentEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n✅ Profile Details:");
                    System.out.println("=*==*==*==*==*==*==*==*==*==*==*==*");

                    System.out.printf("👤 Name: %s\n📧 Email: %s\n📞 Phone: %s\n",
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phoneNumber") != null ? rs.getString("phoneNumber") : "N/A");
                } else {
                    System.out.println("❌ No profile found for this email: " + studentEmail);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}
