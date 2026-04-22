package Admin;

import java.sql.*;
import javax.swing.JOptionPane;
import Database.dbconnection;
import java.sql.Date;

/**
 * Handles notice updating logic.
 */
public class updateNotice {

    public static boolean validation(String id, String title, String content) {
        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Notice ID is required.");
            return false;
        }
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Title and content cannot be empty.");
            return false;
        }
        return true;
    }

    public static boolean updateNotice(int id, String title, String content, Date date) {
        String sql = "UPDATE notice SET notice_title=?, notice_content=?, notice_date=? WHERE notice_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, title);
            pst.setString(2, content);
            pst.setDate(3, date);
            pst.setInt(4, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating notice: " + e.getMessage());
            return false;
        }
    }
}