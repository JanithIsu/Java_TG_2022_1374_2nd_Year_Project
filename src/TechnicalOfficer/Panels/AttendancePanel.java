package TechnicalOfficer.Panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.toedter.calendar.JCalendar;
import Database.dbconnection;

public class AttendancePanel extends JPanel {

    private JComboBox<String> courseIdBox, sessionTypeBox,ugIdBox,pABox;
    private Map<String, String> courseTypeMap = new HashMap<>();
    private String userId;
    private JLabel courseLabel,sTypeLabel,ugIdLabel,pALabel,dLabel,attenLabel;
    private dbconnection dbconnection;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private JButton addBtn,updateBtn,deleteBtn,loadDbBtn;
    private JTextField attId;
    private JTable attendanceTable;
    private JScrollPane scrollPane;




    public AttendancePanel(String userId) {
        this.userId = userId;
//        setBackground(Color.BLUE);
        initUi();
        loadCoursesFromDatabase();
        loadugIdfromDatabase();
    }

    public void initUi() {
        setLayout(null);

        //COMBO BOX-------

        courseIdBox = new JComboBox<>();
        courseIdBox.setBounds(180, 30, 150, 30);
        courseIdBox.addActionListener(e -> updateSessionTypeBox());
        add(courseIdBox);

        sessionTypeBox = new JComboBox<>();
        sessionTypeBox.setBounds(180, 90, 150, 30);
        add(sessionTypeBox);

        ugIdBox = new JComboBox<>();
        ugIdBox.setBounds(180,150,150,30);
        add(ugIdBox);

        String[] items = {"present","absent"};
        pABox = new JComboBox<>(items);
        pABox.setBounds(180,210,150,30);
        add(pABox);



        //DATE

        JCalendar calendar = new JCalendar();
        calendar.setBounds(520,30,200,100);
        add(calendar);

        //All JLAble-----------

        courseLabel = new JLabel("COURSE :");
        courseLabel.setBounds(50,30,150,30);
        add(courseLabel);

        sTypeLabel = new JLabel("SESSIONTYPE  :");
        sTypeLabel.setBounds(50,90,150,30);
        add(sTypeLabel);

        ugIdLabel = new JLabel("UG_ID  :");
        ugIdLabel.setBounds(50,150,150,30);
        add(ugIdLabel);

        pALabel = new JLabel("PRESENT/ABSENT:");
        pALabel.setBounds(50,210,150,30);
        add(pALabel);

        dLabel = new JLabel("DATE :");
        dLabel.setBounds(450,30,150,30);
        add(dLabel);

        attenLabel = new JLabel("AttenId :");
        attenLabel.setBounds(450,200,50,30);
        add(attenLabel);

        //text Field
        attId = new JTextField();
        attId.setBounds(510,200,90,25);
        add(attId);



        //Button
        addBtn = new JButton("ADD");
        addBtn.setBounds(450,150,90,20);
        addBtn.addActionListener(e -> addAttendanceDetails(userId));

        add(addBtn);

        updateBtn = new JButton("UPDATE");
        updateBtn.setBounds(550,150,90,20);
        updateBtn.addActionListener(e -> updateAttendanceDetails(attId.getText().trim()));

        add(updateBtn);

        deleteBtn = new JButton("DELETE");
        deleteBtn.setBounds(650,150,90,20);
        deleteBtn.addActionListener(e -> deleteAttendanceDetails(attId.getText().trim()));

        add(deleteBtn);

        loadDbBtn = new JButton("SHOW ATTENDANCE");
        loadDbBtn.setBounds(450,250,200,25);
        loadDbBtn.addActionListener(e -> loadAttendance());
        add(loadDbBtn);





    }
    //courseId,courseType load from course Table to Combo Box
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

    //undergraduate datails load from undergraduate table to ugIdComboBox
    private void loadugIdfromDatabase(){
        try{
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM undergraduate");

            while (rs.next()){
                String ugId = rs.getString("ug_id");
                ugIdBox.addItem(ugId);
            }
        }catch (Exception e){
            System.out.println("Error"+e.getMessage());
        }
    }

    //hash map------------------
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
    //loading attendance table data to GUI table
    public void loadAttendance() {
        // Remove old table if it exists
        if (scrollPane != null) {
            remove(scrollPane);
        }

        String[] columnNames = {"AttenID", "UG ID", "Course ID", "Session Type", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM attendance");

            while (rs.next()) {
                String attId = rs.getString(1);
                String ugId = rs.getString(3);
                String courseId = rs.getString(4);
                String sessionType = rs.getString(6);
                String status = rs.getString(7);
                String date = rs.getString(5);

                model.addRow(new Object[]{attId, ugId, courseId, sessionType, status, date});
            }

            attendanceTable = new JTable(model);
            scrollPane = new JScrollPane(attendanceTable);
            scrollPane.setBounds(50, 300, 600, 150);
            add(scrollPane);


            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load attendance.");
        }
    }

    //Technical Officer Enterd data save database-----------
    public void addAttendanceDetails(String userId) {
        String attendanceId = attId.getText().trim();
        String ugId = (String) ugIdBox.getSelectedItem();
        String courseId = (String) courseIdBox.getSelectedItem();
        String sessionType = (String) sessionTypeBox.getSelectedItem();
        String status = (String) pABox.getSelectedItem();

        // Get selected date from calendar
        Date selectedDate = ((JCalendar) getComponentAt(520, 30)).getDate();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        if (attendanceId.isEmpty() || ugId == null || courseId == null || sessionType == null || status == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            String query = "INSERT INTO attendance (ug_id, course_id, atten_date, session_type, atten_status,to_id) VALUES (?, ? ,?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, ugId);
            pstmt.setString(2, courseId);
            pstmt.setDate(3, sqlDate);
            pstmt.setString(4, sessionType);
            pstmt.setString(5, status);
            pstmt.setString(6, userId);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Attendance record added successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add attendance.");
            }

            pstmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while adding attendance.");
        }
        loadAttendance();
    }
    //UPDATE T/O first Enter details update------------------------
    public void updateAttendanceDetails(String attenId) {
        if (attenId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the AttenID to update.");
            return;
        }

        String ugId = (String) ugIdBox.getSelectedItem();
        String courseId = (String) courseIdBox.getSelectedItem();
        String sessionType = (String) sessionTypeBox.getSelectedItem();
        String status = (String) pABox.getSelectedItem();

        // Get date from JCalendar
        Component comp = getComponentAt(520, 30);
        if (!(comp instanceof JCalendar)) {
            JOptionPane.showMessageDialog(this, "Date picker not found.");
            return;
        }
        JCalendar calendar = (JCalendar) comp;
        Date selectedDate = calendar.getDate();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            String query = "UPDATE attendance SET ug_id = ?, course_id = ?, atten_date = ?, session_type = ?, atten_status = ? WHERE atten_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, ugId);
            pstmt.setString(2, courseId);
            pstmt.setDate(3, sqlDate);
            pstmt.setString(4, sessionType);
            pstmt.setString(5, status);
            pstmt.setString(6, attenId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {

                JOptionPane.showMessageDialog(this, "Attendance record updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No matching AttenID found. Update failed.");
            }

            pstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while updating attendance.");
        }

        loadAttendance();
    }

    public void deleteAttendanceDetails(String attenId) {
        if (attenId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the AttenID to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete attendance record with ID " + attenId + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();

            String query = "DELETE FROM attendance WHERE atten_id = ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, attenId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Attendance record deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No matching AttenID found. Deletion failed.");
            }

            pstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error while deleting attendance.");
        }

        loadAttendance();
    }

}
