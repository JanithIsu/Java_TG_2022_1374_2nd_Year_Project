package Undergraduate.components;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import Undergraduate.Attendance.Attendance;
import Database.dbconnection;

public class AttendancePanel extends JPanel {

    private String userId;
    private dbconnection dbConnector;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel tableTitleLabel;
    private JCheckBox medicalCheckBox;
    private JButton viewAttendanceButton;

    // 🎨 Theme Colors (Same as Undergraduate class)
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color HEADER_COLOR = new Color(33, 43, 54);

    public AttendancePanel(String userId, dbconnection dbConnector) {
        this.userId = userId;
        this.dbConnector = dbConnector;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        initUI();
    }

    private void initUI() {

        // ===== TOP TITLE PANEL =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(HEADER_COLOR);
        titlePanel.setPreferredSize(new Dimension(100, 60));

        JLabel mainTitle = new JLabel("   Attendance Overview");
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(mainTitle, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);


        // ===== CENTER CONTENT PANEL =====
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---- Top Controls Panel ----
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        controlsPanel.setBackground(BACKGROUND_COLOR);

        medicalCheckBox = new JCheckBox("Include Medical");
        medicalCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        medicalCheckBox.setBackground(BACKGROUND_COLOR);
        medicalCheckBox.setFocusable(false);

        viewAttendanceButton = new JButton("View Attendance");
        styleButton(viewAttendanceButton);

        controlsPanel.add(medicalCheckBox);
        controlsPanel.add(viewAttendanceButton);

        // ---- Table Title ----
        tableTitleLabel = new JLabel("Attendance (Without Medical)");
        tableTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel tableTopPanel = new JPanel(new BorderLayout());
        tableTopPanel.setBackground(BACKGROUND_COLOR);
        tableTopPanel.add(tableTitleLabel, BorderLayout.WEST);

        // ---- Table ----
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // ---- Add to content panel ----
        JPanel northSection = new JPanel(new BorderLayout());
        northSection.setBackground(BACKGROUND_COLOR);
        northSection.add(controlsPanel, BorderLayout.NORTH);
        northSection.add(tableTopPanel, BorderLayout.SOUTH);

        contentPanel.add(northSection, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // ===== BUTTON ACTION =====
        viewAttendanceButton.addActionListener(e -> {
            boolean includeMedical = medicalCheckBox.isSelected();
            loadAttendanceData(includeMedical);
        });

        loadAttendanceData(false);
    }

    private void loadAttendanceData(boolean includeMedical) {
        Attendance attendance = new Attendance();

        DefaultTableModel model = includeMedical
                ? attendance.attendanceWithUgIdWithMedical(userId)
                : attendance.attendanceWithUgIdWithoutMedical(userId);

        table.setModel(model);
        styleTable();

        tableTitleLabel.setText(includeMedical
                ? "Attendance (With Medical)"
                : "Attendance (Without Medical)");
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    private void styleTable() {

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setDefaultEditor(Object.class, null);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Zebra stripe effect
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0
                            ? new Color(245, 247, 250)
                            : Color.WHITE);
                }

                return c;
            }
        });
    }
}