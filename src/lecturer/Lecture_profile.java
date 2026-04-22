package lecturer;

import Database.dbconnection;
import Login.Login;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class Lecture_profile extends JFrame {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userPass;
    private String userProPic;

    private JTextField lecId, lecName, lecEmail, lecPhone;
    private JPasswordField lecPass;
    private JLabel lecPhoto;
    private JButton btnUpdate, btnChangePic;

    public Lecture_profile(String userId) {
        this.userId = userId;
        initComponents();
        setResizable(false);
        loadUserDetails();
    }

    // ---------- Initialization ----------
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setTitle("Lecturer Profile");
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("LECTURER PROFILE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // Main content
        JPanel content = new JPanel(null);
        content.setBackground(new Color(140, 140, 140));

        // --- fixed coordinates so photo & button fit inside window ---
        lecPhoto = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/user.png")));
        lecPhoto.setBounds(550, 80, 150, 150); // moved left and slightly down
        lecPhoto.setBorder(BorderFactory.createEtchedBorder());
        content.add(lecPhoto);

        btnChangePic = new JButton("Change Picture");
        btnChangePic.setBounds(535, 250, 170, 35); // directly below image
        btnChangePic.addActionListener(e -> changeProfilePicture());
        content.add(btnChangePic);
        // --- end fixed photo area ---

        JLabel lblId = label("Lecturer ID", 80, 60);
        JLabel lblName = label("Name", 80, 120);
        JLabel lblEmail = label("Email", 80, 180);
        JLabel lblPhone = label("Phone", 80, 240);
        JLabel lblPass = label("Password", 80, 300);
        content.add(lblId);
        content.add(lblName);
        content.add(lblEmail);
        content.add(lblPhone);
        content.add(lblPass);

        lecId = textField(250, 60);
        lecName = textField(250, 120);
        lecEmail = textField(250, 180);
        lecPhone = textField(250, 240);
        lecPass = new JPasswordField();
        lecPass.setBounds(250, 300, 250, 28);
        lecPass.setEditable(false);

        lecId.setEditable(false);
        content.add(lecId);
        content.add(lecName);
        content.add(lecEmail);
        content.add(lecPhone);
        content.add(lecPass);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(400, 380, 120, 40);
        btnUpdate.addActionListener(e -> updateProfile());
        content.add(btnUpdate);

        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(content, BorderLayout.CENTER);
    }

    private JLabel label(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBounds(x, y, 150, 30);
        return lbl;
    }

    private JTextField textField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 250, 28);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    // ---------- Sidebar ----------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(navLabel("Profile", e -> openFrame(new Lecture_profile(userId))));
        sidebar.add(navLabel("Course", e -> openFrame(new AddCourseMaterials(userId))));
        sidebar.add(navLabel("Marks", e -> openFrame(new UploadMarksExams(userId))));
        sidebar.add(navLabel("Student", e -> openFrame(new StudentDetails(userId))));
        sidebar.add(navLabel("Eligibility", e -> openFrame(new CAEligibility(userId))));
        sidebar.add(navLabel("GPA", e -> openFrame(new GPAcalculation(userId))));
        sidebar.add(navLabel("Grades & Final Marks", e -> openFrame(new GradePoint(userId))));
        sidebar.add(navLabel("Attendance", e -> openFrame(new Attendance(userId))));
        sidebar.add(navLabel("Medical", e -> openFrame(new MedicalLEC(userId))));
        sidebar.add(navLabel("Notices", e -> openFrame(new Notice(userId))));

        JLabel logout = navLabel("LOGOUT", e -> {
            int a = JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (a == JOptionPane.YES_OPTION) { new Login().setVisible(true); dispose(); }
        });
        logout.setForeground(new Color(153, 0, 0));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logout);
        return sidebar;
    }

    private JLabel navLabel(String text, java.util.function.Consumer<MouseEvent> clickAction) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(200, 35));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e){clickAction.accept(e);}
        });
        return lbl;
    }

    private void openFrame(JFrame frame){ frame.setVisible(true); dispose(); }

    // ---------- Logic ----------
    private void loadUserDetails() {
        getUserDetails(userId);
        if (userId != null) {
            lecId.setText(userId);
            lecName.setText(userName);
            lecEmail.setText(userEmail);
            lecPhone.setText(userPhone);
            lecPass.setText(userPass);
            if (userProPic != null && !userProPic.isEmpty()) {
                ImageIcon icon = new ImageIcon(userProPic);
                Image img = icon.getImage().getScaledInstance(lecPhoto.getWidth(), lecPhoto.getHeight(), Image.SCALE_SMOOTH);
                lecPhoto.setIcon(new ImageIcon(img));
            }
        }
    }

    private void getUserDetails(String userId) {
        String sql = "SELECT user_id,user_email,user_password,user_phone,user_pro_pic,user_name FROM user WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userName = rs.getString("user_name");
                userEmail = rs.getString("user_email");
                userPhone = rs.getString("user_phone");
                userPass = rs.getString("user_password");
                userProPic = rs.getString("user_pro_pic");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving user details: " + e.getMessage());
        }
    }

    private void updateProfile() {
        String name = lecName.getText().trim();
        String email = lecEmail.getText().trim();
        String phone = lecPhone.getText().trim();

        String sql = "UPDATE user SET user_name=?, user_email=?, user_phone=? WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, userId);
            int updated = ps.executeUpdate();
            if (updated > 0) JOptionPane.showMessageDialog(this, "Profile updated!");
            else JOptionPane.showMessageDialog(this, "No changes made.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update error: " + e.getMessage());
        }
    }

    private void changeProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg","jpeg","png"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String destinationFolder = "src/SystemImages/";
            String newFileName = "profile_" + userId + "_" + selectedFile.getName();
            File dest = new File(destinationFolder + newFileName);
            try {
                new File(destinationFolder).mkdirs();
                Files.copy(selectedFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                updateProfilePicInDatabase(userId, destinationFolder + newFileName);
                ImageIcon icon = new ImageIcon(dest.getPath());
                Image img = icon.getImage().getScaledInstance(lecPhoto.getWidth(), lecPhoto.getHeight(), Image.SCALE_SMOOTH);
                lecPhoto.setIcon(new ImageIcon(img));
                JOptionPane.showMessageDialog(this, "Profile picture updated!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update photo: " + ex.getMessage());
            }
        }
    }

    private void updateProfilePicInDatabase(String userId, String path) {
        String sql = "UPDATE user SET user_pro_pic=? WHERE user_id=?";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setString(2, userId);
            ps.executeUpdate();
            userProPic = path;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database update failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Lecture_profile("L001").setVisible(true));
    }
}