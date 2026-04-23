package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {

    private String userId;
    private String name, email, phone, profilePicPath, role;

    private JTextField nameField, emailField;
    private JLabel phoneLabel;

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

        // ===== CENTER WRAPPER =====
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel(new BorderLayout(40, 20));
        card.setPreferredSize(new Dimension(700, 380));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // ===== LEFT SIDE (FORM) =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 1, 0, 15));
        formPanel.setBackground(CARD_COLOR);

        nameField = new JTextField(name);
        emailField = new JTextField(email);

        formPanel.add(createFieldPanel("Name", nameField));
        formPanel.add(createFieldPanel("User ID", new JLabel(userId)));
        formPanel.add(createFieldPanel("Email", emailField));
        formPanel.add(createFieldPanel("Role", new JLabel(role)));

        phoneLabel = new JLabel(phone);
        phoneLabel.setForeground(Color.GRAY);
        formPanel.add(createPhonePanel());

        JButton saveBtn = new JButton("Save Changes");
        styleButton(saveBtn);
        saveBtn.addActionListener(e -> updateUserDetails());

        JPanel saveWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saveWrapper.setBackground(CARD_COLOR);
        saveWrapper.add(saveBtn);

        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(CARD_COLOR);
        leftWrapper.add(formPanel, BorderLayout.CENTER);
        leftWrapper.add(saveWrapper, BorderLayout.SOUTH);

        // ===== RIGHT SIDE (PROFILE PIC) =====
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(CARD_COLOR);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        if (profilePicPath == null || profilePicPath.trim().isEmpty()
                || profilePicPath.equalsIgnoreCase("null")) {

            profilePicPath = "TechnicalOfficer/images_to/defaultDp.png";
        }

        ImageIcon profileIcon = new ImageIcon(profilePicPath);
        Image image = profileIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel profilePic = new JLabel(new ImageIcon(image));
        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton changePicBtn = new JButton("Change Picture");
        styleButton(changePicBtn);
        changePicBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePicBtn.addActionListener(e -> changeProfilePicture());

        rightPanel.add(profilePic);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(changePicBtn);

        card.add(leftWrapper, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createFieldPanel(String labelText, Component field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } else if (field instanceof JLabel) {
            ((JLabel) field).setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPhonePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        JLabel label = new JLabel("Phone");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton editPhoneBtn = new JButton("Edit");
        editPhoneBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editPhoneBtn.addActionListener(e -> updatePhoneDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(CARD_COLOR);
        top.add(label, BorderLayout.WEST);
        top.add(editPhoneBtn, BorderLayout.EAST);

        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(top, BorderLayout.NORTH);
        panel.add(phoneLabel, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // ==========================
    // DATABASE METHODS
    // ==========================

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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading profile: " + e.getMessage());
        }
    }

    private void updateUserDetails() {

        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Email cannot be empty!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE user SET user_name=?, user_email=? WHERE user_id=?")) {

            ps.setString(1, newName);
            ps.setString(2, newEmail);
            ps.setString(3, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Profile updated successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update profile.");
        }
    }

    private void updatePhoneDialog() {

        String newPhone = JOptionPane.showInputDialog(
                this, "Enter new phone number:", phone);

        if (newPhone != null && newPhone.matches("\\d{10}")) {

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE user SET user_phone=? WHERE user_id=?")) {

                ps.setString(1, newPhone);
                ps.setString(2, userId);
                ps.executeUpdate();

                phoneLabel.setText(newPhone);
                JOptionPane.showMessageDialog(this, "Phone updated!");

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

    private void changeProfilePicture() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                        "Image files", "jpg", "jpeg", "png"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            String imagePath =
                    fileChooser.getSelectedFile().getAbsolutePath();

            try (Connection con = dbconnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE user SET user_pro_pic=? WHERE user_id=?")) {

                ps.setString(1, imagePath);
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
}