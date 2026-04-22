package Admin;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import Database.dbconnection;

/**
 * Small dialog for adding a new course.
 * Called from CourseTab → AdminService.addCourseDialog().
 */
public class addCourseDialog extends JDialog {

    private final JTextField idField = new JTextField(10);
    private final JTextField nameField = new JTextField(15);
    private final JTextField lecField = new JTextField(10);
    private final JTextField creditField = new JTextField(5);
    private final JComboBox<String> typeBox =
            new JComboBox<>(new String[]{"Theory", "Practical", "Both"});
    private final JButton addBtn = new JButton("Add Course");
    private final JButton cancelBtn = new JButton("Cancel");

    public addCourseDialog(Frame owner) {
        super(owner, "Add Course", true);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Form panel
        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Course ID:")); form.add(idField);
        form.add(new JLabel("Course Name:")); form.add(nameField);
        form.add(new JLabel("Lec ID:")); form.add(lecField);
        form.add(new JLabel("Credit:")); form.add(creditField);
        form.add(new JLabel("Course Type:")); form.add(typeBox);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(addBtn); buttons.add(cancelBtn);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> saveCourse());
        cancelBtn.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }

    private void saveCourse() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String lec = lecField.getText().trim();
        String creditTxt = creditField.getText().trim();
        String type = typeBox.getSelectedItem().toString().toLowerCase();

        try {
            if (!Admin.addCourse.validation(id, name, lec, creditTxt, type))
                return;
            int credit = Integer.parseInt(creditTxt);

            // --- Use your existing addCourse logic ---
            if (Admin.addCourse.addCourses(id, name, lec, credit, type)) {
                Alerts.success("Course added successfully");
                dispose();
            } else {
                Alerts.fail("Add course failed");
            }
        } catch (Exception ex) {
            Alerts.fail(ex.getMessage());
        }
    }

    /**
     * Utility method for quick use from any component
     */
    public static void showDialog(Component parent) {
        Frame owner = JOptionPane.getFrameForComponent(parent);
        addCourseDialog dlg = new addCourseDialog(owner);
        dlg.setVisible(true);
    }
}