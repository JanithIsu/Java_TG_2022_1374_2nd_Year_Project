package Admin;

import java.sql.*;
import Database.dbconnection;
import javax.swing.JOptionPane;

/**
 * Handles course‑insertion logic and input validation.
 * Mirrors the code that used to live inside your original admin.java.
 */
public class addCourse {

    public static boolean validation(String id, String name, String lec, String creditTxt, String type) {
        if (id == null || id.isBlank() ||
                name == null || name.isBlank() ||
                lec == null || lec.isBlank() ||
                creditTxt == null || creditTxt.isBlank() ||
                type == null || type.isBlank()) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
            return false;
        }

        try {
            Integer.parseInt(creditTxt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Credit must be a number!");
            return false;
        }
        return true;
    }

    public static boolean addCourses(String id, String name, String lec, int credit, String type) {
        String sql = "INSERT INTO course (course_id, course_name, lec_id, credit, course_type) VALUES (?,?,?,?,?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, lec);
            pst.setInt(4, credit);
            pst.setString(5, type);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to add course:\n" + e.getMessage());
            return false;
        }
    }
}