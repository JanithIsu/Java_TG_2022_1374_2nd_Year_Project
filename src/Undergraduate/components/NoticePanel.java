package Undergraduate.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import Database.dbconnection;

public class NoticePanel extends JPanel {

    private JTable noticeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // 🎨 Theme Colors (same as other panels)
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color HEADER_COLOR = new Color(33, 43, 54);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);

    public NoticePanel(String userId) {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
        loadNotices();
    }

    private void initUI() {

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));

        JLabel titleLabel = new JLabel("   Notices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);


        // ===== CONTENT PANEL =====
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---- Search Panel ----
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(BACKGROUND_COLOR);

        JLabel searchLabel = new JLabel("Search Notices:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);


        // ---- Table ----
        String[] columnNames = {
                "Notice Title",
                "Content",
                "Date"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        noticeTable = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(noticeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // ===== SEARCH FILTER =====
        rowSorter = new TableRowSorter<>(tableModel);
        noticeTable.setRowSorter(rowSorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText();
                rowSorter.setRowFilter(
                        text.trim().isEmpty()
                                ? null
                                : RowFilter.regexFilter("(?i)" + text)
                );
            }
        });

        // ===== CLICK TO VIEW NOTICE =====
        noticeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                int selectedRow = noticeTable.getSelectedRow();

                if (selectedRow >= 0) {

                    selectedRow = noticeTable.convertRowIndexToModel(selectedRow);

                    String title = (String) tableModel.getValueAt(selectedRow, 0);
                    String content = (String) tableModel.getValueAt(selectedRow, 1);
                    String date = tableModel.getValueAt(selectedRow, 2).toString();

                    JTextArea contentArea = new JTextArea(content);
                    contentArea.setEditable(false);
                    contentArea.setLineWrap(true);
                    contentArea.setWrapStyleWord(true);
                    contentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    contentArea.setBackground(new Color(248, 248, 248));
                    contentArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

                    JScrollPane scroll = new JScrollPane(contentArea);
                    scroll.setPreferredSize(new Dimension(450, 250));

                    JOptionPane.showMessageDialog(
                            NoticePanel.this,
                            scroll,
                            title + " (" + date + ")",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }

    private void loadNotices() {

        String query = "SELECT notice_title, notice_content, notice_date " +
                "FROM notice ORDER BY notice_date DESC";

        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getString("notice_title"),
                        rs.getString("notice_content"),
                        rs.getDate("notice_date")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load notices",
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

        // Zebra row styling
        noticeTable.setDefaultRenderer(
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