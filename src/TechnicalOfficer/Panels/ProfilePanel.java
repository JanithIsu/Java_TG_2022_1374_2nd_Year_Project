package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;



public class ProfilePanel extends JPanel {

    private String userId;
    private dbconnection mdc;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;

    private JLabel pNameLbl, pUserIdLbl, pPasswordLbl, pPhoneLbl, pRoleLbl;
    private JTextField pNameField, pEmailField;
    private JLabel pUserIdLbl2, pPasswordLbl2;
    private JButton phoneEditBtn, saveBtn;
    private JLabel profilePicLabel, editIconLabel;

    private String name, password, email, phone, profilePicPath, role;

    public ProfilePanel(String userId) {
        this.userId = userId;
        setLayout(null);
        setBackground(Color.WHITE);

        getUserDetails();
        initUI();
    }

    private void initUI() {
        if (profilePicPath == null || profilePicPath.trim().isEmpty()) {
            profilePicPath = "src/TechnicalOfficer/images_to/defaultDP.png";
        }

        ImageIcon profileIcon = new ImageIcon(profilePicPath);
        Image image = profileIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        profileIcon = new ImageIcon(image);
        profilePicLabel = new JLabel(profileIcon);
        profilePicLabel.setBounds(450, 50, 150, 150);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(profilePicLabel);

        ImageIcon phoneEditIcon = new ImageIcon("src/TechnicalOfficer/items/editIcon.png");
        Image scaledPhoneIcon = phoneEditIcon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH);
        phoneEditBtn = new JButton(new ImageIcon(scaledPhoneIcon));
        phoneEditBtn.setBounds(600, 260, 15, 15);
        phoneEditBtn.setBackground(Color.WHITE);
        phoneEditBtn.setFocusable(false);
        phoneEditBtn.setBorderPainted(false);
        add(phoneEditBtn);

        // Updated fonts
        Font labelFont = new Font("Segoe UI", Font.BOLD, 20);
        Font labelFont2 = new Font("Segoe UI", Font.PLAIN, 15);

        pNameLbl = new JLabel("Name");
        pNameLbl.setFont(labelFont);
        pNameLbl.setBounds(50, 70, 100, 30);
        add(pNameLbl);

        pNameField = new JTextField(name);
        pNameField.setFont(labelFont2);
        pNameField.setBounds(50, 100, 250, 30);
        add(pNameField);

        pUserIdLbl = new JLabel("User ID");
        pUserIdLbl.setFont(labelFont);
        pUserIdLbl.setBounds(50, 150, 100, 30);
        add(pUserIdLbl);

        pUserIdLbl2 = new JLabel(userId);
        pUserIdLbl2.setFont(labelFont2);
        pUserIdLbl2.setBounds(50, 180, 250, 30);
        add(pUserIdLbl2);

        JLabel pEmailLbl = new JLabel("Email");
        pEmailLbl.setFont(labelFont);
        pEmailLbl.setBounds(50, 230, 100, 30);
        add(pEmailLbl);

        pEmailField = new JTextField(email);
        pEmailField.setFont(labelFont2);
        pEmailField.setBounds(50, 260, 250, 30);
        add(pEmailField);

        pPasswordLbl = new JLabel("Password");
        pPasswordLbl.setFont(labelFont);
        pPasswordLbl.setBounds(50, 310, 120, 30);
        add(pPasswordLbl);

        pPasswordLbl2 = new JLabel(password);
        pPasswordLbl2.setFont(labelFont2);
        pPasswordLbl2.setBounds(50, 340, 250, 30);
        add(pPasswordLbl2);

        pRoleLbl = new JLabel(role);
        pRoleLbl.setFont(labelFont);
        pRoleLbl.setBounds(450, 200, 200, 50);
        add(pRoleLbl);

        pPhoneLbl = new JLabel(phone);
        pPhoneLbl.setFont(labelFont2);
        pPhoneLbl.setForeground(Color.GRAY);
        pPhoneLbl.setBounds(475, 250, 200, 50);
        add(pPhoneLbl);

        saveBtn = new JButton("Save Changes");
        saveBtn.setBounds(50, 400, 150, 30);
        saveBtn.setFont(labelFont2);
        saveBtn.setBackground(new Color(100, 150, 255));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusable(false);
        add(saveBtn);

        phoneEditBtn.addActionListener(e -> updatePhoneDialog());
        saveBtn.addActionListener(e -> updateUserDetails());
    }

    private void getUserDetails() {
        mdc = new dbconnection();
        con = mdc.getConnection();

        try {
            String myQuery = "SELECT * FROM user WHERE user_id = ?";
            ps = con.prepareStatement(myQuery);
            ps.setString(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                password = rs.getString("user_password");
                name = rs.getString("user_name");
                email = rs.getString("user_email");
                phone = rs.getString("user_phone");
                role = rs.getString("user_role");
                profilePicPath = rs.getString("user_pro_pic");
            }
        } catch (SQLException e) {
            System.out.println("Error loading user profile: " + e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            updateProfilePicInDatabase(userId, selectedImagePath);
            JOptionPane.showMessageDialog(this, "Profile picture updated! Please reopen the app to see changes.");
        }
    }

    private void updateProfilePicInDatabase(String userId, String imagePath) {
        try {
            con = mdc.getConnection();
            String updateQuery = "UPDATE user SET user_pro_pic = ? WHERE user_id = ?";
            ps = con.prepareStatement(updateQuery);
            ps.setString(1, imagePath);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePhoneDialog() {
        String newPhone = JOptionPane.showInputDialog(this, "Update new phone number:", phone);
        if (newPhone != null && newPhone.matches("\\d{10}")) {
            updatePhoneInDatabase(userId, newPhone);
            pPhoneLbl.setText(newPhone);
            JOptionPane.showMessageDialog(this, "updated!");
        } else {
            JOptionPane.showMessageDialog(this, "phone number error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePhoneInDatabase(String userId, String newPhone) {
        try {
            con = mdc.getConnection();
            String query = "UPDATE user SET user_phone = ? WHERE user_id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, newPhone);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUserDetails() {
        String newName = pNameField.getText().trim();
        String newEmail = pEmailField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Email cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            con = mdc.getConnection();
            String query = "UPDATE user SET user_name = ?, user_email = ? WHERE user_id = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, newName);
            ps.setString(2, newEmail);
            ps.setString(3, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
