package Admin;

import java.sql.*;
import javax.swing.JOptionPane;
import Database.dbconnection;
import java.sql.Date;

/**
 * Handles adding new notices.
 */
public class addNotice {

    public static boolean validation(String title, String content) {
        if (title == null || title.trim().isEmpty() ||
                content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Title and content are required!");
            return false;
        }
        return true;
    }

    public static boolean addNotice(String title, String content, Date date) {
        String sql = "INSERT INTO notice (notice_title, notice_content, notice_date) VALUES (?,?,?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, title);
            pst.setString(2, content);
            pst.setDate(3, date);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding notice: " + e.getMessage());
            return false;
        }
    }
}