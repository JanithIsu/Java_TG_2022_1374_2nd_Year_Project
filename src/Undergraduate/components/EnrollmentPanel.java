package Undergraduate.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import Database.dbconnection;

public class EnrollmentPanel extends JPanel {

    private JComboBox<String> courseComboBox;
    private JButton enrollButton;
    private JTable enrolledTable;
    private DefaultTableModel tableModel;
    private JButton unenrollButton;

    private String userId;

    // 🎨 Theme Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    public EnrollmentPanel(String userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadAvailableCourses();
        loadEnrolledCourses();
    }

    private void initUI() {

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   ENROLLMENTS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // ===== MAIN CONTENT =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(BACKGROUND_COLOR);


        // ==========================
        // 🔹 TOP SECTION
        // ==========================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createTitledBorder("Enroll to Courses"));

        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(300, 35));
        courseComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        enrollButton = new JButton("Enroll");
        enrollButton.setFocusPainted(false);
        enrollButton.setBackground(PRIMARY_COLOR);
        enrollButton.setForeground(Color.WHITE);
        enrollButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        topPanel.add(courseComboBox);
        topPanel.add(enrollButton);


        // ==========================
// 🔹 BOTTOM SECTION
// ==========================
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));

        tableModel = new DefaultTableModel(
                new String[]{"Course ID", "Course Name", "Credit", "Type"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrolledTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(enrolledTable);

        unenrollButton = new JButton("Unenroll");
        unenrollButton.setFocusPainted(false);
        unenrollButton.setBackground(new Color(192, 57, 43)); // Red theme
        unenrollButton.setForeground(Color.WHITE);
        unenrollButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        unenrollButton.setPreferredSize(new Dimension(200, 40));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(unenrollButton);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);


        // ===== ENROLL BUTTON ACTION =====
        enrollButton.addActionListener(e -> enrollCourse());
        unenrollButton.addActionListener(e -> unenrollCourse());
    }

    private void unenrollCourse() {

        int selectedRow = enrolledTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to unenroll.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedRow = enrolledTable.convertRowIndexToModel(selectedRow);
        String courseId = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to unenroll from this course?",
                "Confirm Unenrollment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            String query = "DELETE FROM enrollment WHERE ug_id = ? AND course_id = ?";

            try (Connection conn = dbconnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, userId);
                stmt.setString(2, courseId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Successfully Unenrolled!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadAvailableCourses();
                loadEnrolledCourses();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Unenrollment Failed",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAvailableCourses() {

        courseComboBox.removeAllItems();

        String query = """
                SELECT course_id, course_name
                FROM course
                WHERE course_id NOT IN (
                    SELECT course_id FROM enrollment WHERE ug_id = ?
                )
                """;

        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String item = rs.getString("course_id") + " - " +
                        rs.getString("course_name");
                courseComboBox.addItem(item);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load courses",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ Load enrolled courses
    private void loadEnrolledCourses() {

        tableModel.setRowCount(0);

        String query = """
                SELECT c.course_id, c.course_name, c.credit, c.course_type
                FROM enrollment e
                JOIN course c ON e.course_id = c.course_id
                WHERE e.ug_id = ?
                """;

        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credit"),
                        rs.getString("course_type")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load enrolled courses",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ✅ Enroll Action
    private void enrollCourse() {

        String selected = (String) courseComboBox.getSelectedItem();

        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "No course available to enroll.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to enroll in this course?",
                "Confirm Enrollment",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            String courseId = selected.split(" - ")[0];

            String query = "INSERT INTO enrollment (ug_id, course_id) VALUES (?, ?)";

            try (Connection conn = dbconnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, userId);
                stmt.setString(2, courseId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Enrollment Successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                loadAvailableCourses();
                loadEnrolledCourses();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Enrollment Failed",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleTable() {

        enrolledTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        enrolledTable.setRowHeight(28);
        enrolledTable.setSelectionBackground(PRIMARY_COLOR);
        enrolledTable.setSelectionForeground(Color.WHITE);
        enrolledTable.setGridColor(new Color(230, 230, 230));
        enrolledTable.setShowVerticalLines(false);

        JTableHeader header = enrolledTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
}