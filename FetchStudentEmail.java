package Student;

import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchStudentEmail {

    public static String getEmailById(int studentId) {
        String query = "SELECT email FROM students WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("❌ Database connection is closed.");
                return null;
            }

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            } else {
                System.out.println("❌ No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        } finally {
            closeResources(rs, stmt, conn);
        }
        return null;
    }

    private static void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
