package lecturer;

import Database.dbconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class marks {
    
    //insert data
    public void insert(String lec_id,String ug_id,String course_id,float quiz_1,float quiz_2,float quiz_3,float quiz_4,float assesment_1,float assesment_2,float mid_term, float final_theory, float final_practical){
        
        if (!validateInsertParameters(lec_id, ug_id, course_id,quiz_1, quiz_2, quiz_3, quiz_4,assesment_1,assesment_2, mid_term, final_theory, final_practical)) {
            return; 
        }
        
        String sql = "INSERT INTO marks(lec_id, ug_id, course_id, quiz_1, quiz_2, quiz_3, quiz_4, assesment_1,assesment_2, mid_term, final_theory, final_practical) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            
            
            ps.setString(1, lec_id);
            ps.setString(2, ug_id);
            ps.setString(3, course_id);
            ps.setFloat(4, quiz_1);
            ps.setFloat(5, quiz_2);
            ps.setFloat(6, quiz_3);
            ps.setFloat(7, quiz_4);
            ps.setFloat(8, assesment_1);
            ps.setFloat(9, assesment_2);
            ps.setFloat(10, mid_term);
            ps.setFloat(11, final_theory);
            ps.setFloat(12, final_practical);
            
            if(ps.executeUpdate() > 0){
                JOptionPane.showMessageDialog(null, "New Marks added Successfully!!");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE, "Error inserting marks", ex);
            JOptionPane.showMessageDialog(null, "Error adding marks: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private boolean validateInsertParameters(String lec_id, String ug_id, String course_id,float quiz_1, float quiz_2, float quiz_3, float quiz_4,float assesment_1,float assesment_2, float mid_term, float final_theory, float final_practical) {
        // Validate IDs are not empty
        
        if (lec_id == null || lec_id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lecturer ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ug_id == null || ug_id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Student ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (course_id == null || course_id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Course ID cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate marks are within reasonable range (0-100)
        if (!isValidMark(quiz_1) || !isValidMark(quiz_2) || !isValidMark(quiz_3) || !isValidMark(quiz_4) || !isValidMark(assesment_1) || !isValidMark(assesment_2) || !isValidMark(mid_term) ||!isValidMark(final_theory) || !isValidMark(final_practical)) {
            JOptionPane.showMessageDialog(null, "All marks must be between 0 and 100","Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidMark(float mark) {
        return mark >= 0 && mark <= 100;
    }
    
    //update marks

    public boolean update(String mark_id, String lec_id, String ug_id, String course_id,float quiz_1, float quiz_2, float quiz_3, float quiz_4,float assesment_1,float  assesment_2, float mid_term, float final_theory, float final_practical) {
        
        String sql = "UPDATE marks SET lec_id=?, ug_id=?, course_id=?, quiz_1=?, quiz_2=?, "
                   + "quiz_3=?, quiz_4=?, assesment_1=?, assesment_2=?, mid_term=?, final_theory=?, final_practical=?"
                   + "WHERE mark_id=?";
        boolean isUpdate = false;

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lec_id);
            ps.setString(2, ug_id);
            ps.setString(3, course_id);
            ps.setFloat(4, quiz_1);
            ps.setFloat(5, quiz_2);
            ps.setFloat(6, quiz_3);
            ps.setFloat(7, quiz_4);
            ps.setFloat(8, assesment_1);
            ps.setFloat(9, assesment_2);
            ps.setFloat(10, mid_term);
            ps.setFloat(11, final_theory);
            ps.setFloat(12, final_practical);
            ps.setString(13, mark_id);  // WHERE clause parameter

            if(ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Updated Successfully!!");
                isUpdate = true;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating marks: " + ex.getMessage());
            Logger.getLogger(marks.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isUpdate;
    }
    
    //delete marks
    
    public boolean delete(String mark_id){
        String sql = "DELETE FROM marks WHERE mark_id = ?";
        boolean isDelete = false;
        
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, mark_id);
            
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Marks deleted successfully!");
                isDelete = true;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting marks: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return isDelete;
    }
    
    //get all data course table
    public void getMarksValues(JTable table,String lecId){
        String sql = "SELECT * FROM marks WHERE lec_id=?";
        
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)){
            
            ps.setString(1,lecId);
            
            try(ResultSet result = ps.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while(result.next()){
                    model.addRow(new Object[]{
                        result.getString("mark_id"),
                        result.getString("lec_id"),
                        result.getString("ug_id"),
                        result.getString("course_id"),
                        result.getString("quiz_1"),
                        result.getString("quiz_2"),
                        result.getString("quiz_3"),
                        result.getString("quiz_4"),
                        result.getString("assesment_1"),
                        result.getString("assesment_2"),
                        result.getString("mid_term"),
                        result.getString("final_theory"),
                        result.getString("final_practical")
                    });
                    
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Course.class.getName()).log(Level.SEVERE,null,ex);
        }
    }

}
