package lecturer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AttendanceEligibility {

    private Connection con;

    public AttendanceEligibility() {
        connect();
    }

    private void connect() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/javaminiproject",
                    "root",
                    "1234"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= ALL ATTENDANCE =================
    public void All_attendance(JTable table, String lecturerId) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT ug_id, course_id, atten_date, session_type, atten_status " +
                    "FROM attendance WHERE to_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, lecturerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getDate("atten_date"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= WITHOUT MEDICAL =================
    public void witoutMedical_attendance(JTable table, String lecturerId) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT ug_id, course_id, atten_date, session_type, atten_status " +
                    "FROM attendance WHERE to_id = ? AND atten_status != 'medical'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, lecturerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getDate("atten_date"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= FILTER GENERAL =================
    public void filterAttendanceWithoutMedical(JTable table,
                                               String studentId,
                                               String course,
                                               String lecturerId) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT ug_id, course_id, atten_date, session_type, atten_status " +
                    "FROM attendance " +
                    "WHERE to_id = ? " +
                    "AND atten_status != 'medical' " +
                    "AND ug_id LIKE ? " +
                    "AND course_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, lecturerId);
            pst.setString(2, "%" + studentId + "%");
            pst.setString(3, course);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getDate("atten_date"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= FILTER THEORY =================
    public void filterTheoryWithoutMedical(JTable table,
                                           String studentId,
                                           String course,
                                           String lecturerId) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT ug_id, course_id, atten_date, session_type, atten_status " +
                    "FROM attendance " +
                    "WHERE to_id = ? " +
                    "AND atten_status != 'medical' " +
                    "AND session_type = 'theory' " +
                    "AND ug_id LIKE ? " +
                    "AND course_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, lecturerId);
            pst.setString(2, "%" + studentId + "%");
            pst.setString(3, course);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getDate("atten_date"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= FILTER PRACTICAL =================
    public void filterPracticalWithoutMedical(JTable table,
                                              String studentId,
                                              String course,
                                              String lecturerId) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT ug_id, course_id, atten_date, session_type, atten_status " +
                    "FROM attendance " +
                    "WHERE to_id = ? " +
                    "AND atten_status != 'medical' " +
                    "AND session_type = 'practical' " +
                    "AND ug_id LIKE ? " +
                    "AND course_id = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, lecturerId);
            pst.setString(2, "%" + studentId + "%");
            pst.setString(3, course);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("ug_id"),
                        rs.getString("course_id"),
                        rs.getDate("atten_date"),
                        rs.getString("session_type"),
                        rs.getString("atten_status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}