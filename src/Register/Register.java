package Register; // Or whatever package you prefer

import Admin.encryption;
import Database.dbconnection; // Assumes your dbconnection class is here

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register {

    /**
     * Registers a new user in the database.
     * * @param userId      The user's ID (char(6))
     * @param password    The user's plain-text password (will be hashed)
     * @param userName    The user's name
     * @param userEmail   The user's email
     * @param userPhone   The user's phone number
     * @param userProPic  A path or URL to the profile picture
     * @param userRole    The user's role ('admin', 'lecturer', 'undergraduate', 'tech_officer')
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerUser(String userId, String password, String userName, 
                                String userEmail, String userPhone, String userProPic, String userRole) {
        
        String sql = "INSERT INTO user (user_id, user_password, user_name, user_email, user_phone, user_pro_pic, user_role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Hash the password just like in your login form
            String hashedPassword = encryption.hashpassword(password);

            // Set the parameters for the PreparedStatement
            pstmt.setString(1, userId);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, userName);
            pstmt.setString(4, userEmail);
            pstmt.setString(5, userPhone);
            pstmt.setString(6, userProPic);
            pstmt.setString(7, userRole);

            // Execute the query
            int rowsAffected = pstmt.executeUpdate();

            // If rowsAffected > 0, the insert was successful
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Error during hashing: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Example of how to use this class ---
    public static void main(String[] args) {
        Register reg = new Register();
        
        // Example registration:
        boolean success = reg.registerUser(
            "U00001", 
            "pass123", 
            "Test User", 
            "test@email.com", 
            "0771234567", 
            "path/to/pic.jpg", 
            "undergraduate"
        );

        if (success) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed.");
        }
    }
}