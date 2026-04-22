package lecturer;

import Login.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pure Swing version of Attendance JFrame — no external libraries.
 */
public class Attendance extends JFrame {

    private final AttendanceEligibility attendanceEligibility = new AttendanceEligibility();
    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblAttendance;
    private JTable tbl80Attendance;
    private JTextField txtStudentId;
    private JComboBox<String> courseCombo;
    private JButton btnView, btnTheory, btnPractical;
    private JButton btnWithMedical, btnWithoutMedical;

    public Attendance(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        loadDefaultAttendanceTable();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Attendance");
        setSize(1200, 750);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("ATTENDANCE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        JPanel controls = buildControlsPanel();
        JPanel tablesPanel = buildTablesPanel();

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(header, BorderLayout.NORTH);
        contentPanel.add(controls, BorderLayout.CENTER);
        contentPanel.add(tablesPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(sidebarLabel("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(sidebarLabel("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(sidebarLabel("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(sidebarLabel("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(sidebarLabel("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(sidebarLabel("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(sidebarLabel("Grades & Final Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(sidebarLabel("Attendance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(sidebarLabel("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(sidebarLabel("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logout = sidebarLabel("LOGOUT", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });
        logout.setForeground(new Color(153, 0, 0));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logout);

        return sidebar;
    }

    private JLabel sidebarLabel(String text, java.util.function.Consumer<MouseEvent> clickAction) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        lbl.setMaximumSize(new Dimension(200, 35));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickAction.accept(e);
            }
        });
        return lbl;
    }

    private JPanel buildControlsPanel() {
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBackground(new Color(153, 153, 153));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        togglePanel.setBackground(new Color(153, 153, 153));

        btnWithMedical = new JButton("Attendance (With Medical)");
        btnWithoutMedical = new JButton("Attendance (Without Medical)");
        btnWithMedical.addActionListener(e -> showWithMedical());
        btnWithoutMedical.addActionListener(e -> showWithoutMedical());
        togglePanel.add(btnWithMedical);
        togglePanel.add(btnWithoutMedical);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        filterPanel.setBackground(new Color(153, 153, 153));

        filterPanel.add(new JLabel("Select Course:"));
        courseCombo = new JComboBox<>(new String[]{"ict2113", "ict2122", "ict2133", "ict2142", "ict2152"});
        filterPanel.add(courseCombo);

        filterPanel.add(new JLabel("Student ID:"));
        txtStudentId = new JTextField(12);
        filterPanel.add(txtStudentId);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionsPanel.setBackground(new Color(153, 153, 153));

        btnView = new JButton("View");
        btnTheory = new JButton("Theory");
        btnPractical = new JButton("Practical");
        btnView.addActionListener(e -> onView());
        btnTheory.addActionListener(e -> onTheory());
        btnPractical.addActionListener(e -> onPractical());
        actionsPanel.add(btnView);
        actionsPanel.add(btnTheory);
        actionsPanel.add(btnPractical);

        controls.add(togglePanel);
        controls.add(Box.createVerticalStrut(8));
        controls.add(filterPanel);
        controls.add(Box.createVerticalStrut(8));
        controls.add(actionsPanel);

        return controls;
    }

    private JPanel buildTablesPanel() {
        tblAttendance = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Student ID", "Course Code", "Date", "Session Type", "Status"}
        ));

        tbl80Attendance = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Student ID", "Course Code", "Session Type", "80% Percentage", "Eligibility"}
        ));
        tbl80Attendance.setVisible(false);

        JScrollPane scrollAll = new JScrollPane(tblAttendance);
        JScrollPane scroll80 = new JScrollPane(tbl80Attendance);

        JPanel panel = new JPanel(new CardLayout());
        panel.add(scrollAll, "ALL");
        panel.add(scroll80, "EIGHTY");

        return panel;
    }

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    // ================= FIXED METHODS =================

    private void loadDefaultAttendanceTable() {
        tblAttendance.setVisible(true);
        tbl80Attendance.setVisible(false);
        attendanceEligibility.All_attendance(tblAttendance, currentUserId);
        model = (DefaultTableModel) tblAttendance.getModel();
    }

    private void showWithMedical() {
        txtStudentId.setText("");
        tblAttendance.setVisible(true);
        tbl80Attendance.setVisible(false);
        attendanceEligibility.All_attendance(tblAttendance, currentUserId);
    }

    private void showWithoutMedical() {
        txtStudentId.setText("");
        tblAttendance.setVisible(true);
        tbl80Attendance.setVisible(false);
        attendanceEligibility.witoutMedical_attendance(tblAttendance, currentUserId);
    }

    private void onView() {
        attendanceEligibility.filterAttendanceWithoutMedical(
                tblAttendance, txtStudentId.getText(),
                (String) courseCombo.getSelectedItem(),
                currentUserId
        );
    }

    private void onTheory() {
        attendanceEligibility.filterTheoryWithoutMedical(
                tblAttendance,
                txtStudentId.getText(),
                (String) courseCombo.getSelectedItem(),
                currentUserId
        );
    }

    private void onPractical() {
        attendanceEligibility.filterPracticalWithoutMedical(
                tblAttendance,
                txtStudentId.getText(),
                (String) courseCombo.getSelectedItem(),
                currentUserId
        );
    }


}