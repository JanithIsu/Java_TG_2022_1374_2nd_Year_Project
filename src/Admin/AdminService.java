package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import Database.dbconnection;

/**
 * Central place for GUI‑driven CRUD operations.
 * UserTab, CourseTab, etc., call these static methods.
 */
public class AdminService {

    // ------------ USER CRUD DIALOGS ----------------

    /** Add a new user */
    public static void addUserDialog(Component parent, JTable table) {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        JComboBox<String> role = new JComboBox<>(new String[] {
                "Admin","Undergraduate","Tech_officer","Lecturer"});
        JTextField email = new JTextField();
        JTextField phone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6,2,6,6));
        panel.add(new JLabel("User ID:")); panel.add(id);
        panel.add(new JLabel("Name:")); panel.add(name);
        panel.add(new JLabel("Password:")); panel.add(pass);
        panel.add(new JLabel("Role:")); panel.add(role);
        panel.add(new JLabel("Email:")); panel.add(email);
        panel.add(new JLabel("Phone:")); panel.add(phone);

        if (JOptionPane.showConfirmDialog(parent, panel, "Add User",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String uid = id.getText().trim();
                String uname = name.getText().trim();
                char[] pwd = pass.getPassword();
                String urole = role.getSelectedItem().toString().toLowerCase();
                String uemail = email.getText().trim();
                String uphone = phone.getText().trim();

                if (!Admin.addUser.validation(uid, pwd, urole, uname, uemail, uphone)) return;
                if (Admin.addUser.usernameExists(uid)) {
                    Alerts.fail("User ID already exists!");
                    return;
                }
                if (Admin.addUser.addUser(uid, uname, pwd, urole, uemail, uphone)) {
                    Alerts.success("User added successfully!");
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                } else Alerts.fail("Add failed.");
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }

    /** Update user details */
    public static void updateUserDialog(Component parent, JTable table) {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JPasswordField pass = new JPasswordField();
        JComboBox<String> role = new JComboBox<>(new String[] {
                "Admin","Undergraduate","Tech_officer","Lecturer"});
        JTextField email = new JTextField();
        JTextField phone = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6,2,6,6));
        panel.add(new JLabel("User ID:")); panel.add(id);
        panel.add(new JLabel("Name:")); panel.add(name);
        panel.add(new JLabel("Password (optional):")); panel.add(pass);
        panel.add(new JLabel("Role:")); panel.add(role);
        panel.add(new JLabel("Email:")); panel.add(email);
        panel.add(new JLabel("Phone:")); panel.add(phone);

        // Pre‑fill if record exists
        if (JOptionPane.showConfirmDialog(parent, panel, "Update User",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String uid = id.getText().trim();
                String uname = name.getText().trim();
                char[] pwd = pass.getPassword();
                String urole = role.getSelectedItem().toString().toLowerCase();
                String uemail = email.getText().trim();
                String uphone = phone.getText().trim();

                if (!Admin.updateUser.validation(uid, urole, uname, uemail, uphone))
                    return;

                boolean ok = (pwd.length > 0)
                        ? Admin.updateUser.updateUserWithPassword(uid, pwd, urole, uname, uemail, uphone)
                        : Admin.updateUser.updateUserWithoutPassword(uid, urole, uname, uemail, uphone);

                if (ok) {
                    Alerts.success("User updated successfully!");
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                } else Alerts.fail("Update failed.");
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }

    /** Delete an existing user */
    public static void deleteUserDialog(Component parent, JTable table) {
        JTextField id = new JTextField();

        JPanel panel = new JPanel(new GridLayout(1,2,5,5));
        panel.add(new JLabel("User ID:")); panel.add(id);

        if (JOptionPane.showConfirmDialog(parent, panel, "Delete User",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String uid = id.getText().trim();
                if (!Admin.deleteUser.validation(uid)) return;
                if (!Admin.deleteUser.usernameExists(uid)) {
                    Alerts.fail("User not found.");
                    return;
                }
                if (Admin.deleteUser.deleteUser(uid)) {
                    Alerts.success("User removed successfully.");
                    ((DefaultTableModel) table.getModel()).setRowCount(0);
                } else Alerts.fail("Delete failed.");
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }

    /** Dialog for adding a course */
    public static void addCourseDialog(Component parent) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField lecField = new JTextField();
        JTextField creditField = new JTextField();
        JComboBox<String> typeBox =
                new JComboBox<>(new String[]{"Theory","Practical","Both"});

        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.add(new JLabel("Course ID:")); form.add(idField);
        form.add(new JLabel("Course Name:")); form.add(nameField);
        form.add(new JLabel("Lec ID:")); form.add(lecField);
        form.add(new JLabel("Credit:")); form.add(creditField);
        form.add(new JLabel("Type:")); form.add(typeBox);

        int result = JOptionPane.showConfirmDialog(
                parent,
                form,
                "Add Course",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String lec = lecField.getText().trim();
            String creditTxt = creditField.getText().trim();
            String type = typeBox.getSelectedItem().toString().toLowerCase();

            try {
                if (!Admin.addCourse.validation(id, name, lec, creditTxt, type)) return;
                int credit = Integer.parseInt(creditTxt);

                if (Admin.addCourse.addCourses(id, name, lec, credit, type)) {
                    Alerts.success("Course added successfully!");
                } else {
                    Alerts.fail("Add course failed.");
                }
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }
}