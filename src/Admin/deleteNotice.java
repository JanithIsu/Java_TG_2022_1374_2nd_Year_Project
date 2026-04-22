package Admin;

import java.sql.*;
import javax.swing.JOptionPane;
import Database.dbconnection;

/**
 * Handles deleting notices.
 */
public class deleteNotice {

    public static boolean validation(String idText) {
        if (idText == null || idText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Notice ID is required!");
            return false;
        }
        try {
            Integer.parseInt(idText);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid notice ID.");
            return false;
        }
    }

    public static boolean noticeExists(int id) {
        String sql = "SELECT 1 FROM notice WHERE notice_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) { return rs.next(); }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error checking notice existence:\n" + e.getMessage());
            return false;
        }
    }

    public static boolean deleteNotice(int id) {
        String sql = "DELETE FROM notice WHERE notice_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error deleting notice: " + e.getMessage());
            return false;
        }
    }
}