package TechnicalOfficer.Panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.toedter.calendar.JCalendar;
import Database.dbconnection;

public class AttendancePanel extends JPanel {

    private JComboBox<String> courseIdBox, sessionTypeBox, ugIdBox, pABox;
    private Map<String, String> courseTypeMap = new HashMap<>();
    private String userId;

    private JTextField attId;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JCalendar calendar;

    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public AttendancePanel(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        initUI();
        loadCoursesFromDatabase();
        loadUgIdFromDatabase();
        loadAttendance();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));
        JLabel title = new JLabel("   Manage Attendance");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        content.setBackground(BACKGROUND_COLOR);
        content.add(createFormPanel(), BorderLayout.NORTH);
        content.add(createTablePanel(), BorderLayout.CENTER);

        // ✅ FIX: make whole page scrollable to prevent out-of-bounds
        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(15,15,15,15)));

        // LEFT - fields
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        courseIdBox = new JComboBox<>();
        sessionTypeBox = new JComboBox<>();
        ugIdBox = new JComboBox<>();
        pABox = new JComboBox<>(new String[]{"present", "absent"});
        attId = new JTextField(12);

        courseIdBox.addActionListener(e -> updateSessionTypeBox());

        int r=0;
        addRow(fields, gbc, r++, "Course:", courseIdBox);
        addRow(fields, gbc, r++, "Session Type:", sessionTypeBox);
        addRow(fields, gbc, r++, "UG ID:", ugIdBox);
        addRow(fields, gbc, r++, "Status:", pABox);
        addRow(fields, gbc, r++, "Attendance ID:", attId);

        // RIGHT - calendar + buttons
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        calendar = new JCalendar();
        // ✅ FIX: compact calendar
        calendar.setPreferredSize(new Dimension(220, 160));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton addBtn = createButton("Add");
        JButton updateBtn = createButton("Update");
        JButton deleteBtn = createButton("Delete");
        addBtn.addActionListener(e -> addAttendanceDetails(userId));
        updateBtn.addActionListener(e -> updateAttendanceDetails(attId.getText().trim()));
        deleteBtn.addActionListener(e -> deleteAttendanceDetails(attId.getText().trim()));
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);

        right.add(dateLabel);
        right.add(Box.createVerticalStrut(5));
        right.add(calendar);
        right.add(Box.createVerticalStrut(15));
        right.add(btnPanel);

        panel.add(fields, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        String[] columns = {"AttenID", "UG ID", "Course ID", "Session Type", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0);
        attendanceTable = new JTable(tableModel);
        styleTable();
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setPreferredSize(new Dimension(100, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridx=0; gbc.gridy=row; gbc.weightx=0;
        p.add(new JLabel(label), gbc);
        gbc.gridx=1; gbc.weightx=1;
        comp.setPreferredSize(new Dimension(180,28));
        p.add(comp, gbc);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90,30));
        return btn;
    }

    private void styleTable() {
        attendanceTable.setRowHeight(26);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        attendanceTable.setSelectionBackground(PRIMARY_COLOR);
        attendanceTable.setSelectionForeground(Color.WHITE);
        JTableHeader h = attendanceTable.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(PRIMARY_COLOR);
        h.setForeground(Color.WHITE);
    }

    // ===== DATABASE =====
    private void updateSessionTypeBox() {
        sessionTypeBox.removeAllItems();
        String sel = (String) courseIdBox.getSelectedItem();
        if(sel != null && courseTypeMap.containsKey(sel)) {
            String type = courseTypeMap.get(sel);
            if("both".equalsIgnoreCase(type)) {
                sessionTypeBox.addItem("theory");
                sessionTypeBox.addItem("practical");
            } else {
                sessionTypeBox.addItem(type);
            }
        }
    }

    private void loadCoursesFromDatabase() {
        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_type FROM course")) {
            while (rs.next()) {
                String id = rs.getString("course_id");
                courseIdBox.addItem(id);
                courseTypeMap.put(id, rs.getString("course_type"));
            }
            if(courseIdBox.getItemCount()>0) updateSessionTypeBox();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading courses.");
        }
    }

    private void loadUgIdFromDatabase() {
        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ug_id FROM undergraduate")) {
            while (rs.next()) ugIdBox.addItem(rs.getString("ug_id"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading UG IDs.");
        }
    }

    public void loadAttendance() {
        tableModel.setRowCount(0);
        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM attendance")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString(1), rs.getString(3), rs.getString(4),
                        rs.getString(6), rs.getString(7), rs.getString(5)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance.");
        }
    }

    public void addAttendanceDetails(String userId) {
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO attendance (ug_id, course_id, atten_date, session_type, atten_status, to_id) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, (String) ugIdBox.getSelectedItem());
            ps.setString(2, (String) courseIdBox.getSelectedItem());
            ps.setDate(3, new java.sql.Date(calendar.getDate().getTime()));
            ps.setString(4, (String) sessionTypeBox.getSelectedItem());
            ps.setString(5, (String) pABox.getSelectedItem());
            ps.setString(6, userId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Attendance added.");
            loadAttendance();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error adding."); }
    }

    public void updateAttendanceDetails(String id) {
        if(id.isEmpty()) return;
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE attendance SET atten_status=? WHERE atten_id=?")) {
            ps.setString(1, (String) pABox.getSelectedItem());
            ps.setString(2, id);
            ps.executeUpdate();
            loadAttendance();
        } catch (SQLException e) { }
    }

    public void deleteAttendanceDetails(String id) {
        if(id.isEmpty()) return;
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM attendance WHERE atten_id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
            loadAttendance();
        } catch (SQLException e) { }
    }
}