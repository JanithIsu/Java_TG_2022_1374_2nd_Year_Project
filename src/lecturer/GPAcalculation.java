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
 * Pure Swing version of GPAcalculation — no external or NetBeans classes.
 */
public class GPAcalculation extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;

    private JTable tblGpa;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;

    public GPAcalculation(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        tableViewStudentGPA();
    }

    // ===================== UI CONSTRUCTION =====================
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("GPA Calculation");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // sidebar
        JPanel sidebar = buildSidebar();

        // header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("GPA CALCULATION", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // search controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controls.setBackground(new Color(153, 153, 153));
        JLabel lblSearch = new JLabel("Search Student ID:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");
        btnSearch.addActionListener(e -> searchAction());
        btnRefresh.addActionListener(e -> refreshAction());
        controls.add(lblSearch);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnRefresh);

        // table
        tblGpa = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Student ID", "SGPA", "CGPA"}
        ));
        tblGpa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblGpa.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(tblGpa);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(controls, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.add(header, BorderLayout.NORTH);
        content.add(mainPanel, BorderLayout.CENTER);

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(content, BorderLayout.CENTER);
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

    // ===================== Logic =====================

    private void tableViewStudentGPA() {
        getDetails(tblGpa, "", "");
        model = (DefaultTableModel) tblGpa.getModel();
    }

    private void searchAction() {
        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID to search.");
        } else {
            getDetails(tblGpa, text, "");
        }
    }

    private void refreshAction() {
        txtSearch.setText("");
        getDetails(tblGpa, "", "");
    }

    // ===================== Interface Implementation =====================

    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = """
                SELECT s.ug_id, s.SGPA, c.CGPA
                FROM SGPA_view s
                JOIN CGPA_view c ON s.ug_id = c.ug_id
                WHERE CONCAT(s.ug_id, s.SGPA, c.CGPA) LIKE ?
                ORDER BY s.ug_id
                """;

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + searchValue + "%");

            try (ResultSet result = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while (result.next()) {
                    model.addRow(new Object[]{
                            result.getString("ug_id"),
                            result.getDouble("SGPA"),
                            result.getDouble("CGPA")
                    });
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(GPAcalculation.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading GPA data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== Entry Point =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GPAcalculation("L001").setVisible(true));
    }
}