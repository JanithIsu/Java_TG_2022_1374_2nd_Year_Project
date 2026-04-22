package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import Database.dbconnection;

public class TimeTableTab extends JPanel {
    private final JTable table = new JTable();
    private final String currentUser;

    private final JTextField idField = new JTextField(6);
    private final JTextField courseField = new JTextField(8);
    private final JTextField depField = new JTextField(8);
    private final JTextField lecField = new JTextField(8);
    private final JTextField stTime = new JTextField(5);
    private final JTextField endTime = new JTextField(5);
    private final JComboBox<String> dayBox =
            new JComboBox<>(new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday"});
    private final JComboBox<String> sessBox =
            new JComboBox<>(new String[]{"Theory","Practical"});

    public TimeTableTab(String username) {
        this.currentUser = username;
        setLayout(new BorderLayout());
        setBackground(new Color(140,140,140));

        table.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Id","Department","Lec_Id","Course_Id","Admin_Id",
                        "Day","Start Time","End Time","Session"}));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(5,4,4,4));
        form.add(new JLabel("ID")); form.add(idField);
        form.add(new JLabel("Course ID")); form.add(courseField);
        form.add(new JLabel("Department")); form.add(depField);
        form.add(new JLabel("Lec ID")); form.add(lecField);
        form.add(new JLabel("Start")); form.add(stTime);
        form.add(new JLabel("End")); form.add(endTime);
        form.add(new JLabel("Day")); form.add(dayBox);
        form.add(new JLabel("Session")); form.add(sessBox);

        JButton add = new JButton("ADD");
        JButton update = new JButton("UPDATE");
        JButton delete = new JButton("DELETE");

        add.addActionListener(e -> addTimetableActionPerformed());
        update.addActionListener(e -> updateTimetableActionPerformed());
        delete.addActionListener(e -> deleteTimetableActionPerformed());

        JPanel south = new JPanel();
        south.add(add); south.add(update); south.add(delete);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(south, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        loadTimetable();
    }

    private void loadTimetable() {
        DefaultTableModel dt = (DefaultTableModel) table.getModel();
        dt.setRowCount(0);
        try (Connection con = dbconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM time_table")) {
            while(rs.next()){
                Vector<Object> row = new Vector<>();
                for (int i=1; i<=9; i++) row.add(rs.getObject(i));
                dt.addRow(row);
            }
        } catch (Exception e){ Alerts.fail(e.getMessage()); }
    }

    private void addTimetableActionPerformed() {
        String id = idField.getText();
        String day = dayBox.getSelectedItem().toString().toLowerCase();
        String course = courseField.getText();
        String dept = depField.getText();
        String lec = lecField.getText();
        String start = stTime.getText();
        String end = endTime.getText();
        String sess = sessBox.getSelectedItem().toString().toLowerCase();
        String admin = currentUser;
        try {
            if (!Admin.addTimetable.validation(day, id, dept, lec, course, start, end, sess, lec))
                return;

            if (Admin.addTimetable.addTimeTable(day, course, start, end, sess, lec, id, dept, admin)) {
                Alerts.success("Added successfully");
                loadTimetable();
            } else {
                Alerts.fail("Add failed");
            }
        } catch (Exception e) {
            Alerts.fail(e.getMessage());
        }
    }

    private void updateTimetableActionPerformed() {
        String id = idField.getText();
        String day = dayBox.getSelectedItem().toString().toLowerCase();
        String course = courseField.getText();
        String dept = depField.getText();
        String lec = lecField.getText();
        String start = stTime.getText();
        String end = endTime.getText();
        String sess = sessBox.getSelectedItem().toString().toLowerCase();
        String admin = currentUser;
        try {
            if(!Admin.updateTimetable.validation(day, lec, dept, lec, course, start, end, sess)) return;
            if(Admin.updateTimetable.updateTimeTable(day, course, start, end, sess, lec, id, dept, admin)){
                Alerts.success("Updated");
                loadTimetable();
            } else Alerts.fail("Update failed");
        } catch(Exception e){ Alerts.fail(e.getMessage()); }
    }

    private void deleteTimetableActionPerformed() {
        String id = idField.getText().trim();
        try {
            if(!Admin.deleteTimeTable.validation(id)) return;
            if(!Admin.deleteTimeTable.timeTableExists(id)){
                Alerts.fail("ID not found"); return;
            }
            if(Admin.deleteTimeTable.deleteTimetable(id)){
                Alerts.success("Deleted");
                loadTimetable();
            } else Alerts.fail("Delete failed");
        } catch(Exception e){ Alerts.fail(e.getMessage()); }
    }
}