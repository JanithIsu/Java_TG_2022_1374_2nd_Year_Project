package Undergraduate.Attendance;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Attendance {
    private final dbconnection dbConnector = new dbconnection();


    public DefaultTableModel attendanceWithUgIdWithMedical(String userId) {
        String sql = "SELECT \n" +
                "    course_id,\n" +
                "    session_type,\n" +
                "    COUNT(*) AS total_classes,\n" +
                "    SUM(CASE \n" +
                "            WHEN atten_status IN ('present', 'medical') THEN 1 \n" +
                "            ELSE 0 \n" +
                "        END) AS attended,\n" +
                "    ROUND(\n" +
                "        100.0 * SUM(CASE \n" +
                "                        WHEN atten_status IN ('present', 'medical') THEN 1 \n" +
                "                        ELSE 0 \n" +
                "                   END) / COUNT(*), 2\n" +
                "    ) AS attendance_percentage\n" +
                "FROM attendance\n" +
                "WHERE ug_id = ?\n" +
                "GROUP BY course_id, session_type;";
        return getAttendanceTableModel(userId, sql);
    }

    public DefaultTableModel attendanceWithUgIdWithoutMedical(String userId) {
        String sql = "SELECT \n" +
                "    course_id,\n" +
                "    session_type,\n" +
                "    COUNT(*) AS total_classes,\n" +
                "    SUM(CASE \n" +
                "            WHEN atten_status = 'present' THEN 1 \n" +
                "            ELSE 0 \n" +
                "        END) AS attended,\n" +
                "    ROUND(\n" +
                "        100.0 * SUM(CASE \n" +
                "                        WHEN atten_status = 'present' THEN 1 \n" +
                "                        ELSE 0 \n" +
                "                   END) / COUNT(*), 2\n" +
                "    ) AS attendance_percentage\n" +
                "FROM attendance\n" +
                "WHERE ug_id = ?\n" +
                "GROUP BY course_id, session_type;";
        return getAttendanceTableModel(userId, sql);
    }


    private DefaultTableModel getAttendanceTableModel(String userId, String sql) {
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "Course ID", "Session Type", "Total Classes", "Attended", "Percentage"
        }, 0);

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                String sessionType = rs.getString("session_type");
                int totalClasses = rs.getInt("total_classes");
                int attended = rs.getInt("attended");
                double percentage = rs.getDouble("attendance_percentage");

                model.addRow(new Object[]{
                        courseId, sessionType, totalClasses, attended,
                        String.format("%.2f", percentage) + "%"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection error");
        }

        return model;
    }
}
