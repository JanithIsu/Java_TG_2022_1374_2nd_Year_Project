package Admin;

import javax.swing.*;
import java.sql.*;
import Database.dbconnection;

/**
 * Handles deleting a course by ID, same style as your other Admin helpers.
 */
public class deleteCourse {

    /** Validation helper – checks field not empty */
    public static boolean validation(String id) {
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Course ID is required!");
            return false;
        }
        return true;
    }

    /** Checks if the given course ID exists in the database. */
    public static boolean courseExists(String id) {
        String sql = "SELECT 1 FROM course WHERE course_id = ?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error checking course existence:\n" + e.getMessage());
            return false;
        }
    }

    /** Deletes the course from the database. */
    public static boolean deleteUser(String id) {
        // Keeping method name deleteUser() so your existing button handlers continue to compile
        String sql = "DELETE FROM course WHERE course_id = ?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting course:\n" + e.getMessage());
            return false;
        }
    }
}