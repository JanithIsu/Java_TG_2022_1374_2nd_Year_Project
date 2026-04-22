package Admin;

import Database.dbconnection;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Deletes users from the system.
 */
public class deleteUser {

    public static boolean validation(String id) {
        if (id == null || id.isBlank()) {
            JOptionPane.showMessageDialog(null, "User ID is required!");
            return false;
        }
        return true;
    }

    public static boolean usernameExists(String id) {
        String sql = "SELECT 1 FROM user WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteUser(String id) {
        String sql = "DELETE FROM user WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting user: " + e.getMessage());
            return false;
        }
    }
}