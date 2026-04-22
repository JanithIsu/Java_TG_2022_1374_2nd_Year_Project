package Admin;

import javax.swing.*;
import java.sql.*;
import Database.dbconnection;

/**
 * Handles updating existing course data.
 */
public class updateCourse {

    public static boolean validation(String id, String name, String lec,
                                     String creditTxt, String type) {
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
            JOptionPane.showMessageDialog(null, "Credit must be a numeric value!");
            return false;
        }
        return true;
    }

    public static boolean updateCourses(String id, String name, String lec,
                                        int credit, String type) {
        String sql = "UPDATE course SET course_name=?, lec_id=?, credit=?, course_type=? " +
                "WHERE course_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            pst.setString(2, lec);
            pst.setInt(3, credit);
            pst.setString(4, type);
            pst.setString(5, id);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating course:\n" + e.getMessage());
            return false;
        }
    }
}