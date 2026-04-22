package Undergraduate.components;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CoursePanel extends JPanel {

    private JTable lectureMaterialTable;
    private DefaultTableModel lectureMaterialTableModel;
    private JComboBox<String> courseComboBox;
    private JButton loadMaterialsButton;

    // 🎨 Theme Colors (same as other panels)
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    public CoursePanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadCourseData();
    }

    private void initUI() {

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Course Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // ===== CENTER CONTENT =====
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));


        // ---- Course Selection Panel ----
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        selectionPanel.setBackground(BACKGROUND_COLOR);

        JLabel courseLabel = new JLabel("Select Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(220, 30));
        courseComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        loadMaterialsButton = new JButton("Load Materials");
        styleButton(loadMaterialsButton);

        loadMaterialsButton.addActionListener(e -> loadLectureMaterials());

        selectionPanel.add(courseLabel);
        selectionPanel.add(courseComboBox);
        selectionPanel.add(loadMaterialsButton);


        // ---- Lecture Material Table ----
        String[] lectureColumns = {
                "Material ID", "Material Name",
                "Material Type", "Material Path"
        };

        lectureMaterialTableModel = new DefaultTableModel(lectureColumns, 0);
        lectureMaterialTable = new JTable(lectureMaterialTableModel) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        styleTable();

        JScrollPane scrollPane = new JScrollPane(lectureMaterialTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));


        contentPanel.add(selectionPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);


        // ---- Mouse Click for Open/Download ----
        lectureMaterialTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int row = lectureMaterialTable.rowAtPoint(e.getPoint());
                int column = lectureMaterialTable.columnAtPoint(e.getPoint());

                if (column == 3 && row >= 0) {

                    String filePath = (String)
                            lectureMaterialTable.getValueAt(row, 3);

                    Object[] options = {"Open", "Download", "Cancel"};
                    int choice = JOptionPane.showOptionDialog(
                            CoursePanel.this,
                            "Choose an action for this file:",
                            "File Action",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    if (choice == 0) {
                        openFile(filePath);
                    } else if (choice == 1) {
                        downloadFile(filePath);
                    }
                }
            }
        });
    }

    // ===============================
    // DATABASE METHODS (UNCHANGED)
    // ===============================

    private void loadCourseData() {

        dbconnection mdc = new dbconnection();

        try (Connection con = mdc.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM course")) {

            while (rs.next()) {
                courseComboBox.addItem(
                        rs.getString("course_name")
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading course data: " + e.getMessage()
            );
        }
    }

    private void loadLectureMaterials() {

        String selectedCourse =
                (String) courseComboBox.getSelectedItem();

        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a course first."
            );
            return;
        }

        lectureMaterialTableModel.setRowCount(0);

        dbconnection mdc = new dbconnection();

        try (Connection con = mdc.getConnection();
             PreparedStatement stmt = con.prepareStatement(
                     "SELECT lm.material_id, lm.material_name, " +
                             "lm.material_type, lm.material_path " +
                             "FROM lecture_materials lm " +
                             "JOIN course c ON lm.course_id = c.course_id " +
                             "WHERE c.course_name = ?")) {

            stmt.setString(1, selectedCourse);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                lectureMaterialTableModel.addRow(new Object[]{
                        rs.getInt("material_id"),
                        rs.getString("material_name"),
                        rs.getString("material_type"),
                        rs.getString("material_path")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading lecture materials: " + e.getMessage()
            );
        }
    }

    // ===============================
    // FILE ACTIONS (UNCHANGED)
    // ===============================

    private void openFile(String filePath) {

        File file = new File(filePath);

        if (file.exists()) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to open file: " + e.getMessage()
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "File does not exist."
            );
        }
    }

    private void downloadFile(String filePath) {

        File sourceFile = new File(filePath);

        if (sourceFile.exists()) {
            try {

                String userHome = System.getProperty("user.home");
                String targetPath =
                        userHome + File.separator + "Downloads"
                                + File.separator + sourceFile.getName();

                Files.copy(
                        sourceFile.toPath(),
                        new File(targetPath).toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                JOptionPane.showMessageDialog(
                        this,
                        "File downloaded to: " + targetPath
                );

            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Download failed: " + e.getMessage()
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "File does not exist."
            );
        }
    }

    // ===============================
    // UI STYLING METHODS
    // ===============================

    private void styleButton(JButton button) {

        button.setFocusPainted(false);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        );
    }

    private void styleTable() {

        lectureMaterialTable.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        lectureMaterialTable.setRowHeight(28);
        lectureMaterialTable.setSelectionBackground(PRIMARY_COLOR);
        lectureMaterialTable.setSelectionForeground(Color.WHITE);
        lectureMaterialTable.setGridColor(new Color(230, 230, 230));
        lectureMaterialTable.setShowVerticalLines(false);
        lectureMaterialTable.setDefaultEditor(Object.class, null);

        JTableHeader header =
                lectureMaterialTable.getTableHeader();

        header.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra Stripe
        lectureMaterialTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row, int column) {

                        Component c =
                                super.getTableCellRendererComponent(
                                        table, value, isSelected,
                                        hasFocus, row, column);

                        if (!isSelected) {
                            c.setBackground(
                                    row % 2 == 0
                                            ? new Color(245, 247, 250)
                                            : Color.WHITE
                            );
                        }
                        return c;
                    }
                });
    }
}