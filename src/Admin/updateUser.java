package Admin;

import Database.dbconnection;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Updates existing user records.
 */
public class updateUser {

    public static boolean validation(String id, String role,
                                     String name, String email, String phone) {
        if (id == null || id.isBlank() ||
                role == null || role.isBlank() ||
                name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                phone == null || phone.isBlank()) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
            return false;
        }
        return true;
    }

    public static boolean updateUserWithoutPassword(String id, String role,
                                                    String name, String email, String phone) {
        String sql = "UPDATE user SET user_name=?, user_role=?, user_email=?, user_phone=? WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, role);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateUserWithPassword(String id, char[] pass, String role,
                                                 String name, String email, String phone) {
        String sql = "UPDATE user SET user_name=?, user_password=?, user_role=?, user_email=?, user_phone=? WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, new String(pass));
            pst.setString(3, role);
            pst.setString(4, email);
            pst.setString(5, phone);
            pst.setString(6, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error updating user: " + e.getMessage());
            return false;
        }
    }
}