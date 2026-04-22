package lecturer;

import Database.dbconnection;
import Login.Login;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pure Swing version of GradePoint frame — fully IntelliJ compatible.
 */
public class GradePoint extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblGrade;
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh;

    public GradePoint(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        tableViewStudentGrades();
    }

    // ==========================  UI SETUP  ==================================
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Student Grades");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("STUDENT GRADES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        controls.setBackground(new Color(153, 153, 153));
        JLabel lblSearch = new JLabel("Search Student ID:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");
        controls.add(lblSearch);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);

        btnSearch.addActionListener(e -> searchAction());
        btnRefresh.addActionListener(e -> refreshAction());

        // Table
        tblGrade = new JTable(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Student ID", "Course Code", "Final Marks", "Grade" }
        ));
        tblGrade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblGrade.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tblGrade);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(153, 153, 153));
        main.add(controls, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(main, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(navLabel("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(navLabel("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(navLabel("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(navLabel("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(navLabel("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(navLabel("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(navLabel("Grades & Final Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(navLabel("Attendance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(navLabel("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(navLabel("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logout = navLabel("LOGOUT", e -> {
            int a = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (a == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                dispose();
            }
        });
        logout.setForeground(new Color(153, 0, 0));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(logout);
        return sidebar;
    }

    private JLabel navLabel(String text, java.util.function.Consumer<MouseEvent> clickAction) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(200, 35));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickAction.accept(e);
            }
        });
        return lbl;
    }

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    // ==========================  DATA LOGIC  ==================================

    private void tableViewStudentGrades() {
        getDetails(tblGrade, "", currentUserId);
        model = (DefaultTableModel) tblGrade.getModel();
    }

    private void searchAction() {
        String search = txtSearch.getText().trim();
        if (search.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a student ID to search.");
        } else {
            getDetails(tblGrade, search, currentUserId);
        }
    }

    private void refreshAction() {
        txtSearch.setText("");
        getDetails(tblGrade, "", currentUserId);
    }

    // ==========================  INTERFACE IMPLEMENTATION  =====================

    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = "SELECT e.ug_id, e.course_id, e.final_mark, e.grade "
                + "FROM exam_grades_view e "
                + "JOIN course c ON c.course_id = e.course_id "
                + "WHERE c.lec_id = ? "
                + (searchValue.isEmpty() ? "" : "AND e.ug_id LIKE ? ")
                + "ORDER BY e.ug_id ASC";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecId);
            if (!searchValue.isEmpty()) {
                ps.setString(2, "%" + searchValue + "%");
            }

            try (ResultSet result = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                while (result.next()) {
                    model.addRow(new Object[]{
                            result.getString("ug_id"),
                            result.getString("course_id"),
                            result.getString("final_mark"),
                            result.getString("grade")
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(GradePoint.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading grade data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================  MAIN  =========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradePoint("L001").setVisible(true));
    }
}