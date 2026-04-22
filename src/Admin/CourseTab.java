package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import Database.dbconnection;

/**
 * Course management tab — lists, adds, edits, deletes courses.
 */
public class CourseTab extends JPanel {
    private final JTable table = new JTable();
    private final String currentUser;

    public CourseTab(String username) {
        this.currentUser = username;
        setLayout(new BorderLayout());
        setBackground(new Color(140, 140, 140));

        // Table
        table.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Course ID","Course Name","Lec ID","Credit","Type"}));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons for CRUD
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e -> add_courseActionPerformed());
        updateBtn.addActionListener(e -> update_courseActionPerformed());
        deleteBtn.addActionListener(e -> delete_courseActionPerformed());

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        course_table();
    }

    /** Reload the course table from DB. */
    public void course_table() {
        DefaultTableModel dt = (DefaultTableModel) table.getModel();
        dt.setRowCount(0);
        try (Connection con = dbconnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM course")) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= 5; i++) {
                    row.add(rs.getObject(i));
                }
                dt.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- ADD ----------------
    private void add_courseActionPerformed() {
        AdminService.addCourseDialog(this);
        course_table();
    }

    // ---------------- UPDATE ----------------
    private void update_courseActionPerformed() {
        JTextField idF = new JTextField();
        JTextField nameF = new JTextField();
        JTextField lecF = new JTextField();
        JTextField creditF = new JTextField();
        JComboBox<String> typeBox =
                new JComboBox<>(new String[]{"Theory", "Practical", "Both"});

        JPanel panel = new JPanel(new GridLayout(5,2,6,6));
        panel.add(new JLabel("Course ID:")); panel.add(idF);
        panel.add(new JLabel("Course Name:")); panel.add(nameF);
        panel.add(new JLabel("Lec ID:")); panel.add(lecF);
        panel.add(new JLabel("Credit:")); panel.add(creditF);
        panel.add(new JLabel("Type:")); panel.add(typeBox);

        int opt = JOptionPane.showConfirmDialog(this, panel, "Update Course",
                JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String id = idF.getText().trim();
            String name = nameF.getText().trim();
            String lec = lecF.getText().trim();
            String creditText = creditF.getText().trim();
            String type = typeBox.getSelectedItem().toString().toLowerCase();
            try {
                if (!Admin.updateCourse.validation(id,name,lec,creditText,type)) return;
                int credit = Integer.parseInt(creditText);
                if (Admin.updateCourse.updateCourses(id,name,lec,credit,type)) {
                    Alerts.success("Course updated successfully!");
                    course_table();
                } else Alerts.fail("Update failed!");
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }

    // ---------------- DELETE ----------------
    private void delete_courseActionPerformed() {
        JTextField idF = new JTextField();

        JPanel panel = new JPanel(new GridLayout(1,2,6,6));
        panel.add(new JLabel("Course ID:")); panel.add(idF);

        int opt = JOptionPane.showConfirmDialog(this, panel, "Delete Course",
                JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String id = idF.getText().trim();
            try {
                if (!Admin.deleteCourse.validation(id)) return;
                if (!Admin.deleteCourse.courseExists(id)) {
                    Alerts.fail("Course not found!");
                    return;
                }
                if (Admin.deleteCourse.deleteUser(id)) {
                    Alerts.success("Course deleted successfully!");
                    course_table();
                } else Alerts.fail("Delete failed!");
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }
}