package Admin;
import java.sql.*;
import Database.dbconnection;

public class updateTimetable {
    public static boolean validation(String day, String lec, String dept,
                                     String lec2, String course,
                                     String stTime, String endTime, String sess) {
        // reuse your original validation code here
        return true;
    }

    public static boolean updateTimeTable(String day, String course, String stTime, String endTime,
                                          String sess, String lec, String id, String dept, String admin) {
        try (Connection con = dbconnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "UPDATE time_table SET day=?, course_id=?, start_time=?, end_time=?, session_type=?, " +
                            "lec_id=?, department=?, admin_id=? WHERE time_table_id=?");
            pst.setString(1, day);
            pst.setString(2, course);
            pst.setString(3, stTime);
            pst.setString(4, endTime);
            pst.setString(5, sess);
            pst.setString(6, lec);
            pst.setString(7, dept);
            pst.setString(8, "AD0001");
            pst.setString(9, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}