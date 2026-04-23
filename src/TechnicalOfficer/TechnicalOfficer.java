package TechnicalOfficer;

import TechnicalOfficer.Panels.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Login.Login;

public class TechnicalOfficer extends JFrame implements ActionListener {

    private String userId;
    private JPanel leftPanel, rightPanel;
    private CardLayout cardLayout;

    private JButton attendanceBtn, medicalBtn, profileBtn,
            timeTableBtn, noticeBtn, logOutBtn;

    // 🎨 Modern Theme Colors
    private final Color SIDEBAR_COLOR = new Color(33, 43, 54);
    private final Color ACTIVE_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color TEXT_COLOR = Color.WHITE;

    public TechnicalOfficer(String userId) {
        this.userId = userId;
        initUi();
        setLocationRelativeTo(null);
    }

    private void initUi() {

        setTitle("Technical Officer Portal");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();

        // ===== SIDEBAR =====
        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(220, getHeight()));
        leftPanel.setBackground(SIDEBAR_COLOR);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        leftPanel.add(Box.createVerticalStrut(40));

        JLabel title = new JLabel("TECHNICAL OFFICER");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(title);

        leftPanel.add(Box.createVerticalStrut(30));

        attendanceBtn = createNavButton("Attendance");
        medicalBtn = createNavButton("Medical");
        profileBtn = createNavButton("Profile");
        timeTableBtn = createNavButton("Time Tables");
        noticeBtn = createNavButton("Notices");
        logOutBtn = createNavButton("Log Out");

        leftPanel.add(attendanceBtn);
        leftPanel.add(medicalBtn);
        leftPanel.add(profileBtn);
        leftPanel.add(timeTableBtn);
        leftPanel.add(noticeBtn);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logOutBtn);
        leftPanel.add(Box.createVerticalStrut(20));

        // ===== RIGHT CONTENT =====
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBackground(BACKGROUND_COLOR);

        rightPanel.add(new AttendancePanel(userId), "ATTENDANCE");
        rightPanel.add(new MedicalPanel(userId), "MEDICAL");
        rightPanel.add(new NoticePanel(), "NOTICE");
        rightPanel.add(new TimeTablePanel(), "TIMETABLE");
        rightPanel.add(new ProfilePanel(userId), "PROFILE");

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Default selection
        setActiveButton(attendanceBtn, "ATTENDANCE");

        setVisible(true);
    }

    private JButton createNavButton(String text) {

        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);

        return btn;
    }

    private void resetButtons() {
        JButton[] buttons = {
                attendanceBtn, medicalBtn, profileBtn,
                timeTableBtn, noticeBtn
        };
        for (JButton btn : buttons) {
            btn.setBackground(SIDEBAR_COLOR);
        }
    }

    private void setActiveButton(JButton btn, String card) {
        resetButtons();
        btn.setBackground(ACTIVE_COLOR);
        cardLayout.show(rightPanel, card);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == attendanceBtn)
            setActiveButton(attendanceBtn, "ATTENDANCE");

        else if (src == medicalBtn)
            setActiveButton(medicalBtn, "MEDICAL");

        else if (src == noticeBtn)
            setActiveButton(noticeBtn, "NOTICE");

        else if (src == profileBtn)
            setActiveButton(profileBtn, "PROFILE");

        else if (src == timeTableBtn)
            setActiveButton(timeTableBtn, "TIMETABLE");

        else if (src == logOutBtn) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                new Login().setVisible(true);
            }
        }
    }
}