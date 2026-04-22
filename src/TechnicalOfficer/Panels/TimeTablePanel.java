package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TimeTablePanel extends JPanel {

    private JTable timeTable;
    private DefaultTableModel tableModel;

    // 🎨 Theme Colors
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public TimeTablePanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadTimeTable();
    }

    private void initUI() {

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Time Table");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE SETUP =====
        String[] columnNames = {
                "Time Table ID",
                "Department",
                "Lecturer ID",
                "Course ID",
                "Admin ID",
                "Day",
                "Start Time",
                "End Time",
                "Time Slot"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        timeTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(timeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTimeTable() {

        tableModel.setRowCount(0);

        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM time_table")) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load time table.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleTable() {

        timeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timeTable.setRowHeight(28);
        timeTable.setSelectionBackground(PRIMARY_COLOR);
        timeTable.setSelectionForeground(Color.WHITE);
        timeTable.setGridColor(new Color(230, 230, 230));
        timeTable.setShowVerticalLines(false);

        JTableHeader header = timeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra Row Effect
        timeTable.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

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