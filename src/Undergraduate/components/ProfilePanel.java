package Undergraduate.components;

import Database.dbconnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {

    private String userId;
    private String name, email, phone, profilePicPath, role;

    private JLabel phoneValueLabel;

    // 🎨 Theme Colors
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public ProfilePanel(String userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        getUserDetails();
        initUI();
    }

    private void initUI() {

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== CENTER CONTENT =====
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND_COLOR);

        JPanel cardPanel = new JPanel(new BorderLayout(40, 20));
        cardPanel.setPreferredSize(new Dimension(650, 350));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ===== LEFT SIDE (User Info) =====
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 20));
        infoPanel.setBackground(CARD_COLOR);

        infoPanel.add(createInfoLabel("Name: " + name));
        infoPanel.add(createInfoLabel("User ID: " + userId));
        infoPanel.add(createInfoLabel("Email: " + email));
        infoPanel.add(createInfoLabel("Role: " + role));

        // ===== RIGHT SIDE (Profile Section) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_COLOR);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        if (profilePicPath == null || profilePicPath.trim().isEmpty()) {
            profilePicPath = "src/Undergraduate/Profile_images/defaultUser.jpeg";
        }

        CircularImagePanel profilePic =
                new CircularImagePanel(profilePicPath, 130);

        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton editPicButton = new JButton("Change Picture");
        styleButton(editPicButton);
        editPicButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editPicButton.addActionListener(e -> changeProfilePicture());

        phoneValueLabel = new JLabel("Phone: 0" + phone);
        phoneValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        phoneValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton editPhoneButton = new JButton("Update Phone");
        styleButton(editPhoneButton);
        editPhoneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editPhoneButton.addActionListener(e -> updatePhoneDialog());

        rightPanel.add(profilePic);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(editPicButton);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(phoneValueLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(editPhoneButton);

        cardPanel.add(infoPanel, BorderLayout.CENTER);
        cardPanel.add(rightPanel, BorderLayout.EAST);

        wrapper.add(cardPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return label;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    // ===============================
    // DATABASE METHODS
    // ===============================

    private void getUserDetails() {

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM user WHERE user_id = ?")) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                name = rs.getString("user_name");
                email = rs.getString("user_email");
                phone = rs.getString("user_phone");
                role = rs.getString("user_role");
                profilePicPath = rs.getString("user_pro_pic");
            }

            rs.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading profile: " + e.getMessage());
        }
    }

    private void changeProfilePicture() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Image files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            String selectedImagePath =
                    fileChooser.getSelectedFile().getAbsolutePath();

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE user SET user_pro_pic = ? WHERE user_id = ?")) {

                ps.setString(1, selectedImagePath);
                ps.setString(2, userId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Profile picture updated! Restart to see changes.");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating picture.");
            }
        }
    }

    private void updatePhoneDialog() {

        String newPhone = JOptionPane.showInputDialog(
                this, "Enter new phone number:", phone);

        if (newPhone != null && newPhone.matches("\\d{10}")) {

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE user SET user_phone = ? WHERE user_id = ?")) {

                ps.setString(1, newPhone);
                ps.setString(2, userId);
                ps.executeUpdate();

                phoneValueLabel.setText("Phone: 0" + newPhone);

                JOptionPane.showMessageDialog(this,
                        "Phone number updated!");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating phone.");
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid phone number!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}