package TechnicalOfficer.Panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JCalendar;
import Database.dbconnection;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MedicalPanel extends JPanel {

    private String userId;
    private JComboBox courseIdBox, sessionTypeBox, ugIdBox;
    private JLabel courseLabel, sTypeLabel, ugIdLabel, dLabel, rLabel, attenIdLabel;
    private dbconnection dbconnection;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private Map<String, String> courseTypeMap = new HashMap<>();
    private JTable medicalTable;
    private JScrollPane scrollPane;
    private JTextArea reason;
    private JButton addBtn, updateBtn, deleteBtn;
    private JCalendar calendar;
    private JTextField attendanceId;

    public MedicalPanel(String userId) {
        this.userId = userId;
        initUi();
        loadUgIdsFromDatabase();
        loadCoursesFromDatabase();
        loadMedical();
    }

    public void initUi() {
        setLayout(null);

        // COMBO BOXES
        courseIdBox = new JComboBox<>();
        courseIdBox.setBounds(180, 30, 150, 30);
        courseIdBox.addActionListener(e -> updateSessionTypeBox());
        add(courseIdBox);

        sessionTypeBox = new JComboBox<>();
        sessionTypeBox.setBounds(180, 90, 150, 30);
        add(sessionTypeBox);

        ugIdBox = new JComboBox<>();
        ugIdBox.setBounds(180, 150, 150, 30);
        add(ugIdBox);

        // CALENDAR
        calendar = new JCalendar();
        calendar.setBounds(560, 30, 200, 100);
        add(calendar);

        // LABELS
        courseLabel = new JLabel("COURSE :");
        courseLabel.setBounds(50, 30, 150, 30);
        add(courseLabel);

        sTypeLabel = new JLabel("SESSION TYPE:");
        sTypeLabel.setBounds(50, 90, 150, 30);
        add(sTypeLabel);

        ugIdLabel = new JLabel("UG_ID:");
        ugIdLabel.setBounds(50, 150, 150, 30);
        add(ugIdLabel);

        dLabel = new JLabel("MEDICAL DATE:");
        dLabel.setBounds(450, 30, 150, 30);
        add(dLabel);

        rLabel = new JLabel("REASON:");
        rLabel.setBounds(50, 210, 150, 30);
        add(rLabel);

        // TEXT AREA
        reason = new JTextArea();
        reason.setBounds(180, 210, 150, 80);
        add(reason);

        // BUTTON - ADD
        addBtn = new JButton("ADD");
        addBtn.setBounds(450, 200, 90, 20);
        addBtn.addActionListener(e -> addMedicalDetails(userId));
        add(addBtn);
    }

    private void loadCoursesFromDatabase() {
        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
                return;
            }

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT course_id, course_type FROM course");

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String courseType = rs.getString("course_type");

                courseIdBox.addItem(courseId);
                courseTypeMap.put(courseId, courseType);
            }

            if (courseIdBox.getItemCount() > 0) {
                courseIdBox.setSelectedIndex(0);
                updateSessionTypeBox();
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading courses.");
        }
    }

    private void loadUgIdsFromDatabase() {
        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT ug_id FROM undergraduate");

            while (rs.next()) {
                ugIdBox.addItem(rs.getString("ug_id"));
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading UG IDs.");
        }
    }

    private void updateSessionTypeBox() {
        String selectedCourse = (String) courseIdBox.getSelectedItem();
        sessionTypeBox.removeAllItems();

        if (selectedCourse != null && courseTypeMap.containsKey(selectedCourse)) {
            String type = courseTypeMap.get(selectedCourse);

            if (type.equalsIgnoreCase("both")) {
                sessionTypeBox.addItem("theory");
                sessionTypeBox.addItem("practical");
            } else {
                sessionTypeBox.addItem(type);
            }
        }
    }

    public void loadMedical() {
        if (scrollPane != null) {
            remove(scrollPane);
        }

        String[] columnNames = {"Medical ID", "UG ID", "Course ID", "Session Type", "Reason", "MedicalDate"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM medical");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("medical_id"),
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getString("session_type"),
                        rs.getString("reason"),
                        rs.getString("medical_date")
                });
            }

            medicalTable = new JTable(model);
            scrollPane = new JScrollPane(medicalTable);
            scrollPane.setBounds(50, 300, 600, 150);
            add(scrollPane);

            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load medical records.");
        }
    }

    //  UPDATED METHOD
    public void addMedicalDetails(String userId) {
        String ugId = (String) ugIdBox.getSelectedItem();
        String courseId = (String) courseIdBox.getSelectedItem();
        String sessionType = (String) sessionTypeBox.getSelectedItem();
        String reasonText = reason.getText();

        Date selectedDate = calendar.getDate();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        if (ugId == null || courseId == null || sessionType == null || reasonText == null || reasonText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            // 1. Check for matching attendance
            String checkQuery = "SELECT * FROM attendance WHERE ug_id=? AND course_id=? AND session_type=? AND atten_date=?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, ugId);
            checkStmt.setString(2, courseId);
            checkStmt.setString(3, sessionType);
            checkStmt.setDate(4, sqlDate);
            ResultSet rsCheck = checkStmt.executeQuery();

            if (!rsCheck.next()) {
                JOptionPane.showMessageDialog(this, "No matching attendance record found.");
                rsCheck.close();
                checkStmt.close();
                con.close();
                return;
            }

            int matchedAttenId = rsCheck.getInt("atten_id");

            // 2. Update attendance status
            String updateQuery = "UPDATE attendance SET atten_status='medical' WHERE atten_id=?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setInt(1, matchedAttenId);
            int updated = updateStmt.executeUpdate();
            updateStmt.close();

            if (updated == 0) {
                JOptionPane.showMessageDialog(this, "Failed to update attendance status.");
                rsCheck.close();
                checkStmt.close();
                con.close();
                return;
            }

            // 3. Insert into medical
            String insertQuery = "INSERT INTO medical (ug_id, course_id, session_type, reason, medical_date, to_id, atten_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertQuery);
            ps.setString(1, ugId);
            ps.setString(2, courseId);
            ps.setString(3, sessionType);
            ps.setString(4, reasonText);
            ps.setDate(5, sqlDate);
            ps.setString(6, userId);
            ps.setInt(7, matchedAttenId);

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Medical record added and attendance updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Medical record insert failed.");
            }

            rsCheck.close();
            checkStmt.close();
            ps.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }

        loadMedical();
    }
    public void updateMedical(){

    }
}
