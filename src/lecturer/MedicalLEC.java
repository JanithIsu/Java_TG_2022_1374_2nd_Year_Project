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
 * Pure Swing version of MedicalLEC window.
 */
public class MedicalLEC extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblMedical;
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh;

    public MedicalLEC(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        tableViewMedical();
    }

    // ============================= UI ==============================
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Medical Records");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = buildSidebar();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("MEDICAL RECORDS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        controls.setBackground(new Color(153, 153, 153));
        JLabel lblSearch = new JLabel("Search by Student ID:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");
        controls.add(lblSearch);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);

        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> onRefresh());

        // Table
        tblMedical = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Medical ID", "Course Code", "Student ID", "Medical Date"}
        ));
        tblMedical.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMedical.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tblMedical);

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

        sidebar.add(navItem("Profile", e -> openFrame(new Lecture_profile(currentUserId))));
        sidebar.add(navItem("Course", e -> openFrame(new AddCourseMaterials(currentUserId))));
        sidebar.add(navItem("Marks", e -> openFrame(new UploadMarksExams(currentUserId))));
        sidebar.add(navItem("Student", e -> openFrame(new StudentDetails(currentUserId))));
        sidebar.add(navItem("Eligibility", e -> openFrame(new CAEligibility(currentUserId))));
        sidebar.add(navItem("GPA", e -> openFrame(new GPAcalculation(currentUserId))));
        sidebar.add(navItem("Grades & Final Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(navItem("Attendance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(navItem("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(navItem("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logout = navItem("LOGOUT", e -> {
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

    private JLabel navItem(String text, java.util.function.Consumer<MouseEvent> clickAction) {
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

    // ============================= LOGIC ==============================

    private void tableViewMedical() {
        getDetails(tblMedical, "", currentUserId);
        model = (DefaultTableModel) tblMedical.getModel();
    }

    private void onSearch() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student ID.");
        } else {
            getDetails(tblMedical, text, currentUserId);
        }
    }

    private void onRefresh() {
        txtSearch.setText("");
        getDetails(tblMedical, "", currentUserId);
    }

    // ============================= INTERFACE METHOD ==============================
    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = """
                SELECT m.medical_id, m.course_id, m.ug_id, m.medical_date
                FROM medical m
                JOIN course c ON c.course_id = m.course_id
                WHERE c.lec_id = ?
                """ + (searchValue.isEmpty() ? "" : "AND m.ug_id LIKE ? ") +
                "ORDER BY m.medical_id ASC";

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
                            result.getString("medical_id"),
                            result.getString("course_id"),
                            result.getString("ug_id"),
                            result.getString("medical_date")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MedicalLEC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error fetching medical records:\n" + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicalLEC("L001").setVisible(true));
    }
}