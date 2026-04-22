package lecturer;

import Login.Login;
import Database.dbconnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pure Swing version of the Notice frame—no external libraries.
 */
public class Notice extends JFrame implements CRUD_Operation_Interface {

    private DefaultTableModel model;
    private final String currentUserId;
    private JTable tblNotice;
    private JTextField txtSearch;
    private JButton btnSearch, btnRefresh;

    public Notice(String userId) {
        this.currentUserId = userId;
        initComponents();
        setResizable(false);
        viewNotices();
    }

    // --------------------- UI SETUP ---------------------------------
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Notices");
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = buildSidebar();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(153, 187, 187));
        JLabel title = new JLabel("NOTICES", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        header.add(title, BorderLayout.CENTER);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(new Color(153, 153, 153));

        JLabel lblSearch = new JLabel("Search Notice:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");

        btnSearch.addActionListener(e -> onSearch());
        btnRefresh.addActionListener(e -> onRefresh());

        controlPanel.add(lblSearch);
        controlPanel.add(txtSearch);
        controlPanel.add(btnSearch);
        controlPanel.add(btnRefresh);

        // Table
        tblNotice = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Notice ID", "Title", "Content", "Date"}
        ));
        tblNotice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblNotice.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tblNotice);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(153, 153, 153));
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Add to frame
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    // --------------------- SIDEBAR ---------------------------------
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
        sidebar.add(navItem("Grades & Marks", e -> openFrame(new GradePoint(currentUserId))));
        sidebar.add(navItem("Attendance", e -> openFrame(new Attendance(currentUserId))));
        sidebar.add(navItem("Medical", e -> openFrame(new MedicalLEC(currentUserId))));
        sidebar.add(navItem("Notices", e -> openFrame(new Notice(currentUserId))));

        JLabel logout = navItem("LOGOUT", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
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

    // --------------------- LOGIC ---------------------------------
    private void viewNotices() {
        getDetails(tblNotice, "", currentUserId);
        model = (DefaultTableModel) tblNotice.getModel();
    }

    private void onSearch() {
        String term = txtSearch.getText().trim();
        if (term.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.");
        } else {
            getDetails(tblNotice, term, currentUserId);
        }
    }

    private void onRefresh() {
        txtSearch.setText("");
        getDetails(tblNotice, "", currentUserId);
    }

    // --------------------- DATABASE METHOD ------------------------
    @Override
    public void getDetails(JTable table, String searchValue, String lecId) {
        String sql = """
                SELECT notice_id, notice_title, notice_content, notice_date
                FROM notice
                WHERE CONCAT(notice_id, notice_title, notice_content, notice_date) LIKE ?
                ORDER BY notice_id ASC
                """;
        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + searchValue + "%");

            try (ResultSet result = ps.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                while (result.next()) {
                    model.addRow(new Object[]{
                            result.getString("notice_id"),
                            result.getString("notice_title"),
                            result.getString("notice_content"),
                            result.getString("notice_date")
                    });
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Notice.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error loading notices: " + ex.getMessage());
        }
    }

    // --------------------- MAIN ENTRY ------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Notice("L001").setVisible(true));
    }
}