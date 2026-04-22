package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class NoticePanel extends JPanel {

    private JTable noticeTable;
    private DefaultTableModel tableModel;

    // 🎨 Theme Colors
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);

    public NoticePanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadNotice();
    }

    private void initUI() {

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Notices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] columnNames = {
                "Notice ID",
                "Notice Title",
                "Notice Content",
                "Notice Date"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        noticeTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadNotice() {

        tableModel.setRowCount(0);

        try (Connection con = dbconnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM notice")) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load notices.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void styleTable() {

        noticeTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        noticeTable.setRowHeight(28);
        noticeTable.setSelectionBackground(PRIMARY_COLOR);
        noticeTable.setSelectionForeground(Color.WHITE);
        noticeTable.setGridColor(new Color(230, 230, 230));
        noticeTable.setShowVerticalLines(false);

        JTableHeader header = noticeTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra stripe effect
        noticeTable.setDefaultRenderer(
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