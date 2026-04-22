package Admin;

import Database.dbconnection;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Adds new users to the database.
 * Matches the logic you had in admin.java.
 */
public class addUser {

    public static boolean validation(String id, char[] pass, String role,
                                     String name, String email, String phone) {
        if (id == null || id.isBlank() ||
                pass == null || pass.length == 0 ||
                role == null || role.isBlank() ||
                name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                phone == null || phone.isBlank()) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
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
            JOptionPane.showMessageDialog(null, "Error checking username: " + e.getMessage());
            return false;
        }
    }

    public static boolean addUser(String id, String name, char[] pass,
                                  String role, String email, String phone) {
        String sql =
                "INSERT INTO user (user_id, user_name, user_password, user_role, user_email, user_phone)" +
                        " VALUES (?,?,?,?,?,?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.setString(2, name);
            pst.setString(3, new String(pass));
            pst.setString(4, role);
            pst.setString(5, email);
            pst.setString(6, phone);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + e.getMessage());
            return false;
        }
    }
}