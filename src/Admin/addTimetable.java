package Admin;

import java.sql.*;
import Database.dbconnection;

/**
 * Handles validation and insertion for new timetable records.
 * Mirrors the logic used in your original admin.java file.
 */
public class addTimetable {

    // 🧠 Validation logic — identical philosophy as your other helpers
    public static boolean validation(String day, String id, String dept,
                                     String lec, String course,
                                     String start, String end, String sess, String lec2) {
        if (day == null || day.isEmpty() ||
                id == null || id.isEmpty() ||
                dept == null || dept.isEmpty() ||
                lec == null || lec.isEmpty() ||
                course == null || course.isEmpty() ||
                start == null || start.isEmpty() ||
                end == null || end.isEmpty() ||
                sess == null || sess.isEmpty()) {

            javax.swing.JOptionPane.showMessageDialog(null, "All fields are required!");
            return false;
        }
        // You can extend this check for valid times, etc.
        return true;
    }

    /**
     * Performs insertion of a new record into the time_table.
     *
     * @return true on success, false otherwise
     */
    public static boolean addTimeTable(String day, String course, String start, String end,
                                       String sess, String lec, String id, String dept, String admin) {
        String sql = "INSERT INTO time_table (time_table_id, department, lec_id, course_id, admin_id, " +
                "day, start_time, end_time, session_type) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.setString(2, dept);
            pst.setString(3, lec);
            pst.setString(4, course);
            pst.setString(5, "AD0001");
            pst.setString(6, day);
            pst.setString(7, start);
            pst.setString(8, end);
            pst.setString(9, sess);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Failed to add timetable record:\n" + e.getMessage());
            return false;
        }
    }
}