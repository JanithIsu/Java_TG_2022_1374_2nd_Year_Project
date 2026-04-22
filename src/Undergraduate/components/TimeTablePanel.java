package Undergraduate.components;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TimeTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    // 🎨 Theme Colors (same as other panels)
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    public TimeTablePanel(dbconnection dbConnector) {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadTimeTableData();
    }

    private void initUI() {

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Weekly Time Table");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // ===== CONTENT PANEL =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{
                "Day", "Start Time", "End Time",
                "Course ID", "Session Type",
                "Lecturer ID", "Department"
        });

        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadTimeTableData() {

        String query =
                "SELECT day, start_time, end_time, course_id, " +
                        "session_type, lec_id, department " +
                        "FROM time_table " +
                        "ORDER BY FIELD(day, " +
                        "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'), " +
                        "start_time";

        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getString("day"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("course_id"),
                        rs.getString("session_type"),
                        rs.getString("lec_id"),
                        rs.getString("department")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load timetable: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleTable() {

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra row styling
        table.setDefaultRenderer(
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
                                        table, value,
                                        isSelected,
                                        hasFocus,
                                        row, column);

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