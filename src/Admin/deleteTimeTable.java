package Admin;
import java.sql.*;
import Database.dbconnection;

public class deleteTimeTable {
    public static boolean validation(String id) {
        return id != null && !id.trim().isEmpty();
    }

    public static boolean timeTableExists(String id) {
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT 1 FROM time_table WHERE time_table_id=?")) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) { return rs.next(); }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public static boolean deleteTimetable(String id) {
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement("DELETE FROM time_table WHERE time_table_id=?")) {
            pst.setString(1, id);
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}