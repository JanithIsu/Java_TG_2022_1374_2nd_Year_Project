package lecturer;

import Login.Login;
import Database.dbconnection;
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
 * Pure Swing version of CAEligibility — no NetBeans or third‑party components.
 */
public class CAEligibility extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblEligibility;
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh;

    public CAEligibility(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        getDetails(tblEligibility, "", currentUserId);
    }

    // ======================= UI CONSTRUCTION ==========================
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("CA Eligibility");
        setSize(1200, 750);
        setLocationRelativeTo(null);

        // ---------- Sidebar ----------
        JPanel sidebar = buildSidebar();

        // ---------- Header ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("CA ELIGIBILITY", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // ---------- Controls ----------
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(new Color(153, 153, 153));
        JLabel lblSearch = new JLabel("Search Student ID:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");

        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> onRefresh());
        controlPanel.add(lblSearch);
        controlPanel.add(txtSearch);
        controlPanel.add(btnSearch);
        controlPanel.add(btnRefresh);

        // ---------- Table ----------
        tblEligibility = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Student ID", "Course Code", "Quiz 1", "Quiz 2",
                        "Quiz 3", "Quiz 4", "Ass 1", "Ass 2",
                        "Mid Marks", "CA Marks", "Eligibility"
                }
        ));
        tblEligibility.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblEligibility.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(tblEligibility);

        // ---------- Combine ----------
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(153, 187, 187));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel(new ImageIcon(getClass().getResource("/SystemImages/resize_logo.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.add(menuLabel("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(menuLabel("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(menuLabel("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(menuLabel("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(menuLabel("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(menuLabel("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(menuLabel("Grades & Final Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(menuLabel("Attendance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(menuLabel("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(menuLabel("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logout = menuLabel("LOGOUT", e -> {
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

    private JLabel menuLabel(String text, java.util.function.Consumer<MouseEvent> clickAction) {
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

    private void openFrame(JFrame frame) {
        frame.setVisible(true);
        dispose();
    }

    // ======================= Event actions ==========================
    private void onSearch() {
        String search = txtSearch.getText().trim();
        if (search.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a student ID or course code.");
        } else {
            getDetails(tblEligibility, search, currentUserId);
        }
    }

    private void onRefresh() {
        txtSearch.setText("");
        getDetails(tblEligibility, "", currentUserId);
    }

    // ======================= Data loading logic ==========================
    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = """
                SELECT m.ug_id, m.course_id, m.quiz_1, m.quiz_2, m.quiz_3, m.quiz_4,
                       m.assesment_1, m.assesment_2, m.mid_term,
                       cm.ca_mark, ce.ca_eligibility_status
                FROM marks m
                LEFT JOIN ca_marks cm ON m.ug_id = cm.ug_id AND m.course_id = cm.course_id
                LEFT JOIN ca_eligibility ce ON m.ug_id = ce.ug_id AND m.course_id = ce.course_id
                WHERE m.lec_id = ?
                """ + (searchValue.isEmpty() ? "" : "AND m.ug_id LIKE ? ") +
                "ORDER BY m.ug_id ASC";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lecId);
            if (!searchValue.isEmpty()) ps.setString(2, "%" + searchValue + "%");

            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("ug_id"),
                            rs.getString("course_id"),
                            rs.getFloat("quiz_1"),
                            rs.getFloat("quiz_2"),
                            rs.getFloat("quiz_3"),
                            rs.getFloat("quiz_4"),
                            rs.getFloat("assesment_1"),
                            rs.getFloat("assesment_2"),
                            rs.getFloat("mid_term"),
                            rs.getDouble("ca_mark"),
                            rs.getString("ca_eligibility_status")
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(CAEligibility.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading CA eligibility data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ======================= Entry point / test ==========================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CAEligibility("L001").setVisible(true));
    }
}