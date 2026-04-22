package TechnicalOfficer.Panels;

import javax.swing.*;
import javax.swing.table.*;
import com.toedter.calendar.JCalendar;
import Database.dbconnection;

import java.awt.*;
import java.sql.*;
import java.util.*;

public class MedicalPanel extends JPanel {

    private String userId;

    private JComboBox<String> courseIdBox, sessionTypeBox, ugIdBox;
    private JTextArea reasonArea;
    private JTable medicalTable;
    private DefaultTableModel tableModel;
    private JCalendar calendar;

    private Map<String, String> courseTypeMap = new HashMap<>();

    // 🎨 Theme Colors
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public MedicalPanel(String userId) {
        this.userId = userId;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadUgIdsFromDatabase();
        loadCoursesFromDatabase();
        loadMedical();
    }

    private void initUI() {

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(100, 60));

        JLabel title = new JLabel("   Manage Medical Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ===== CONTENT =====
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        content.setBackground(BACKGROUND_COLOR);

        content.add(createFormPanel(), BorderLayout.NORTH);
        content.add(createTablePanel(), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== LEFT SIDE (FIELDS)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        courseIdBox = new JComboBox<>();
        sessionTypeBox = new JComboBox<>();
        ugIdBox = new JComboBox<>();
        reasonArea = new JTextArea(3, 18);

        int row = 0;

        addFormRow(leftPanel, gbc, row++, "Course:", courseIdBox);
        addFormRow(leftPanel, gbc, row++, "Session Type:", sessionTypeBox);
        addFormRow(leftPanel, gbc, row++, "UG ID:", ugIdBox);

        gbc.gridx = 0;
        gbc.gridy = row;
        leftPanel.add(new JLabel("Reason:"), gbc);

        gbc.gridx = 1;
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setPreferredSize(new Dimension(180, 60));
        leftPanel.add(reasonScroll, gbc);

        // ===== RIGHT SIDE (CALENDAR + BUTTON)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel dateLabel = new JLabel("Medical Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ✅ Compact calendar like Attendance
        calendar = new JCalendar();
        calendar.setPreferredSize(new Dimension(220, 160));
        calendar.setMaximumSize(new Dimension(220, 160));

        JButton addBtn = createButton("Add Medical");
        addBtn.addActionListener(e -> addMedicalDetails(userId));

        rightPanel.add(dateLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(calendar);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(addBtn);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {

        String[] columns = {
                "Medical ID", "UG ID", "Course ID",
                "Session Type", "Reason", "Medical Date"
        };

        tableModel = new DefaultTableModel(columns, 0);
        medicalTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(medicalTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc,
                            int row, String labelText, Component comp) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable() {

        medicalTable.setRowHeight(28);
        medicalTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        medicalTable.setSelectionBackground(PRIMARY_COLOR);
        medicalTable.setSelectionForeground(Color.WHITE);

        JTableHeader header = medicalTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
    }

    // =========================
    // DATABASE LOGIC (UNCHANGED)
    // =========================

    private void loadCoursesFromDatabase() {

        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_type FROM course")) {

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String courseType = rs.getString("course_type");

                courseIdBox.addItem(courseId);
                courseTypeMap.put(courseId, courseType);
            }

            updateSessionTypeBox();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading courses.");
        }

        courseIdBox.addActionListener(e -> updateSessionTypeBox());
    }

    private void loadUgIdsFromDatabase() {

        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ug_id FROM undergraduate")) {

            while (rs.next()) {
                ugIdBox.addItem(rs.getString("ug_id"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading UG IDs.");
        }
    }

    private void updateSessionTypeBox() {

        sessionTypeBox.removeAllItems();
        String selectedCourse = (String) courseIdBox.getSelectedItem();

        if (selectedCourse != null && courseTypeMap.containsKey(selectedCourse)) {
            String type = courseTypeMap.get(selectedCourse);

            if ("both".equalsIgnoreCase(type)) {
                sessionTypeBox.addItem("theory");
                sessionTypeBox.addItem("practical");
            } else {
                sessionTypeBox.addItem(type);
            }
        }
    }

    public void loadMedical() {

        tableModel.setRowCount(0);

        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM medical")) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getString("medical_id"),
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getString("session_type"),
                        rs.getString("reason"),
                        rs.getString("medical_date")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load medical records.");
        }
    }

    public void addMedicalDetails(String userId) {

        String ugId = (String) ugIdBox.getSelectedItem();
        String courseId = (String) courseIdBox.getSelectedItem();
        String sessionType = (String) sessionTypeBox.getSelectedItem();
        String reasonText = reasonArea.getText();
        java.sql.Date sqlDate = new java.sql.Date(calendar.getDate().getTime());

        if (ugId == null || courseId == null || sessionType == null || reasonText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection con = dbconnection.getConnection()) {

            // Check attendance
            String checkQuery = "SELECT atten_id FROM attendance WHERE ug_id=? AND course_id=? AND session_type=? AND atten_date=?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, ugId);
            checkStmt.setString(2, courseId);
            checkStmt.setString(3, sessionType);
            checkStmt.setDate(4, sqlDate);

            ResultSet rsCheck = checkStmt.executeQuery();

            if (!rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "No matching attendance record found.");
                return;
            }

            int attenId = rsCheck.getInt("atten_id");

            // Update attendance
            PreparedStatement updateStmt = con.prepareStatement(
                    "UPDATE attendance SET atten_status='medical' WHERE atten_id=?");
            updateStmt.setInt(1, attenId);
            updateStmt.executeUpdate();

            // Insert medical
            PreparedStatement insertStmt = con.prepareStatement(
                    "INSERT INTO medical (ug_id, course_id, session_type, reason, medical_date, to_id, atten_id) VALUES (?, ?, ?, ?, ?, ?, ?)");

            insertStmt.setString(1, ugId);
            insertStmt.setString(2, courseId);
            insertStmt.setString(3, sessionType);
            insertStmt.setString(4, reasonText);
            insertStmt.setDate(5, sqlDate);
            insertStmt.setString(6, userId);
            insertStmt.setInt(7, attenId);

            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Medical added & attendance updated.");
            loadMedical();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }
}