package Undergraduate.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.dbconnection;

public class GradePanel extends JPanel {

    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;

    // 🎨 Theme Colors (same as other panels)
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    public GradePanel(String userId) {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI(userId);
    }

    private void initUI(String userId) {

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Grades & GPA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // ===== CONTENT PANEL =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        createGradeTable();
        loadGrades(userId);

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void createGradeTable() {

        String[] columnNames = {
                "Course Code", "Course Name",
                "Grade", "SGPA", "CGPA"
        };

        gradesTableModel = new DefaultTableModel(columnNames, 0);
        gradesTable = new JTable(gradesTableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        styleTable();

        // Right-align SGPA & CGPA
        DefaultTableCellRenderer rightAlign =
                new DefaultTableCellRenderer();
        rightAlign.setHorizontalAlignment(SwingConstants.RIGHT);

        gradesTable.getColumnModel()
                .getColumn(3).setCellRenderer(rightAlign);

        gradesTable.getColumnModel()
                .getColumn(4).setCellRenderer(rightAlign);
    }

    private void loadGrades(String userId) {

        String sql = "SELECT * FROM gradesOfUgs WHERE ug_id = ?";

        try (Connection con = dbconnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                gradesTableModel.addRow(new Object[]{
                        rs.getString("course_id"),
                        rs.getString("course_name"),
                        rs.getString("grade"),
                        rs.getString("SGPA"),
                        rs.getString("CGPA")
                });
            }

            rs.close();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading Grades data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleTable() {

        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gradesTable.setRowHeight(28);
        gradesTable.setSelectionBackground(PRIMARY_COLOR);
        gradesTable.setSelectionForeground(Color.WHITE);
        gradesTable.setGridColor(new Color(230, 230, 230));
        gradesTable.setShowVerticalLines(false);

        JTableHeader header = gradesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra striping
        gradesTable.setDefaultRenderer(
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