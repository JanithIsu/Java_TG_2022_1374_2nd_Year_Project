package lecturer;

import Database.dbconnection;
import Login.Login;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Student details window — clean IntelliJ / Swing version.
 */
public class StudentDetails extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblStudents;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;

    public StudentDetails(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        loadAllStudents();
    }

    // ======================= UI SECTION ===========================
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Student Details");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("STUDENT DETAILS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // Search controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controls.setBackground(new Color(153, 153, 153));
        JLabel lblSearch = new JLabel("Search Student:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");

        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> onRefresh());

        controls.add(lblSearch);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);

        // Table
        tblStudents = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Student ID", "Name", "Phone Number", "Email"}
        ));
        tblStudents.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblStudents.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tblStudents);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(153, 153, 153));
        mainPanel.add(controls, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(15));

        sidebar.add(navLabel("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(navLabel("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(navLabel("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(navLabel("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(navLabel("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(navLabel("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(navLabel("Grades & Marks", e -> openFrame(new GradePoint(currentUserId))));
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

    // ======================= LOGIC SECTION ===========================
    private void loadAllStudents() {
        getDetails(tblStudents, "", currentUserId);
        model = (DefaultTableModel) tblStudents.getModel();
    }

    private void onSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a student ID, name, or email to search.");
        } else {
            getDetails(tblStudents, query, currentUserId);
        }
    }

    private void onRefresh() {
        txtSearch.setText("");
        getDetails(tblStudents, "", currentUserId);
    }

    // ======================= CRUD METHOD ===========================
    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = """
                SELECT u.user_id, u.user_name, u.user_phone, u.user_email
                FROM user u
                JOIN undergraduate ug ON u.user_id = ug.ug_id
                WHERE u.user_role = 'undergraduate'
                AND (u.user_id LIKE ? OR u.user_name LIKE ? OR u.user_email LIKE ?)
                ORDER BY u.user_id ASC
                """;
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String pattern = "%" + searchValue + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("user_phone"),
                            rs.getString("user_email")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDetails.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading student data: " + ex.getMessage());
        }
    }

    // ======================= MAIN TEST ===========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDetails("L001").setVisible(true));
    }
}