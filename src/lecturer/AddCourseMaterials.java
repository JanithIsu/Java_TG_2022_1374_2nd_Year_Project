package lecturer;

import Login.Login;
import Database.dbconnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

/**
 * Clean IntelliJ-only version of AddCourseMaterials without NetBeans layouts.
 */
public class AddCourseMaterials extends JFrame {

    private final Course course = new Course();
    private final String currentUserId;
    private Connection con;
    private ResultSet rs;
    private PreparedStatement ps;

    // UI components
    private JComboBox<String> courseComboBox;
    private JTable courseMaterialTable;
    private JButton uploadButton;
    private JButton deleteButton;

    public AddCourseMaterials(String userId) {
        this.currentUserId = userId;

        initComponents(); // now handwritten with standard Swing
        setResizable(false);
        course.loadCoursesToComboBox(courseComboBox, currentUserId);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Course Materials");
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // ======= Sidebar =======
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(makeNavLabel("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(makeNavLabel("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(makeNavLabel("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(makeNavLabel("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(makeNavLabel("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(makeNavLabel("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(makeNavLabel("Grades & Final Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(makeNavLabel("Attedance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(makeNavLabel("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(makeNavLabel("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logoutLabel = makeNavLabel("LOGOUT", e -> {
            int a = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (a == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });
        logoutLabel.setForeground(new Color(153, 0, 0));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logoutLabel);

        // ======= Header =======
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("Course Materials", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // ======= Top panel (course selection) =======
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        coursePanel.setBackground(new Color(153, 153, 153));

        JLabel courseLabel = new JLabel("Select Course:");
        courseComboBox = new JComboBox<>();
        courseComboBox.addActionListener(this::courseComboBoxActionPerformed);

        coursePanel.add(courseLabel);
        coursePanel.add(courseComboBox);

        // ======= Table and Buttons =======
        courseMaterialTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, new String[]{"Title", "Content"}
        ));
        courseMaterialTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                courseMaterialTableMouseClicked(e);
            }
        });
        JScrollPane scrollPane = new JScrollPane(courseMaterialTable);

        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(this::uploadButtonActionPerformed);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::deleteButtonActionPerformed);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(153, 153, 153));
        buttonPanel.add(uploadButton);
        buttonPanel.add(deleteButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(153, 153, 153));
        tablePanel.add(buttonPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // ======= Main Layout =======
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(coursePanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        // Outer layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel makeNavLabel(String text, java.util.function.Consumer<MouseEvent> onClick) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(200, 30));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.accept(e);
            }
        });
        return label;
    }

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        this.dispose();
    }

    // === Your event methods and logic remain unchanged ===

    private void courseComboBoxActionPerformed(ActionEvent evt) {
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        if (selectedCourse != null) {
            String[] parts = selectedCourse.split(" - ");
            String courseId = parts[0];
            course.loadCourseMaterials(courseMaterialTable, courseId);
        }
    }

    private void courseMaterialTableMouseClicked(MouseEvent evt) {
        // keep this empty or reuse uploadCourseMaterials() logic
    }

    private void uploadButtonActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String materialName = selectedFile.getName();
            String materialPath = selectedFile.getPath();
            String courseId = getSelectedCourseId();
            insertMaterialToDatabase(materialName, materialPath, courseId);
            course.loadCourseMaterials(courseMaterialTable, getSelectedCourseId());
        }
    }

    private void deleteButtonActionPerformed(ActionEvent evt) {
        int selectedRow = courseMaterialTable.getSelectedRow();

        if (selectedRow != -1) {
            String materialName = (String) courseMaterialTable.getValueAt(selectedRow, 0);
            String materialPath = (String) courseMaterialTable.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this material?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteMaterialFromDatabase(materialName, materialPath);
                course.loadCourseMaterials(courseMaterialTable, getSelectedCourseId());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a material to delete.");
        }
    }

    // === Utility methods from your original code ===

    private String getSelectedCourseId() {
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        if (selectedCourse != null) {
            String[] parts = selectedCourse.split(" - ");
            return parts[0];
        }
        return null;
    }

    private void deleteMaterialFromDatabase(String materialName, String materialPath) {
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM lecture_materials WHERE material_name=? AND material_path=?")) {
            ps.setString(1, materialName);
            ps.setString(2, materialPath);
            int rowsDeleted = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, rowsDeleted > 0 ? "Material deleted successfully." : "Failed to delete material.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void insertMaterialToDatabase(String materialName, String fileName, String courseId) {
        String sql = "INSERT INTO lecture_materials (course_id, material_name, material_path, material_type) VALUES (?, ?, ?, ?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, courseId);
            ps.setString(2, materialName);
            ps.setString(3, fileName);
            ps.setString(4, getMaterialTypeFromFile(fileName));

            int rowsInserted = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, rowsInserted > 0 ? "Material uploaded successfully." : "Failed to upload material.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error uploading material: " + ex.getMessage());
        }
    }

    private String getMaterialTypeFromFile(String materialPath) {
        String extension = materialPath.substring(materialPath.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf": return "pdf";
            case "mp4": return "video";
            case "ppt":
            case "pptx": return "ppt";
            default: return "other";
        }
    }

    // And include your openFile(), uploadCourseMaterials() if still needed.
}