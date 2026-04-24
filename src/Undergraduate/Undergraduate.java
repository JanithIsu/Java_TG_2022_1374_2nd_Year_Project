package Undergraduate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Database.dbconnection;
import Undergraduate.components.*;
import Login.Login;

public class Undergraduate extends JFrame implements ActionListener {

    private JPanel sidebarPanel, contentPanel;
    private CardLayout cardLayout;

    private JButton courseBtn, attendanceBtn, medicalBtn, gradeBtn,
            timeTableBtn, noticeBtn, profileBtn, enrollmentsBtn, logOutBtn;

    private final String userId;
    private final dbconnection dbConnector;

    // 🎨 Color Theme
    private final Color SIDEBAR_COLOR = new Color(33, 43, 54);
    private final Color HOVER_COLOR = new Color(52, 73, 94);
    private final Color ACTIVE_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color TEXT_COLOR = Color.WHITE;

    public Undergraduate(String userId) {
        this.userId = userId;
        this.dbConnector = new dbconnection();

        setTitle("Undergraduate Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        // ---------- Sidebar ----------
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        sidebarPanel.add(Box.createVerticalStrut(40));

        JLabel title = new JLabel("STUDENT PORTAL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(title);

        sidebarPanel.add(Box.createVerticalStrut(30));

        courseBtn     = createNavButton("Courses", buttonFont);
        attendanceBtn = createNavButton("Attendance", buttonFont);
        medicalBtn    = createNavButton("Medical", buttonFont);
        gradeBtn      = createNavButton("Grades", buttonFont);
        timeTableBtn  = createNavButton("Time Table", buttonFont);
        noticeBtn     = createNavButton("Notices", buttonFont);
        profileBtn    = createNavButton("My Profile", buttonFont);
        enrollmentsBtn= createNavButton("Enrollments", buttonFont);
        logOutBtn     = createNavButton("Log Out", buttonFont);

        sidebarPanel.add(courseBtn);
        sidebarPanel.add(attendanceBtn);
        sidebarPanel.add(medicalBtn);
        sidebarPanel.add(gradeBtn);
        sidebarPanel.add(timeTableBtn);
        sidebarPanel.add(noticeBtn);
        sidebarPanel.add(profileBtn);
        sidebarPanel.add(enrollmentsBtn);

        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(logOutBtn);
        sidebarPanel.add(Box.createVerticalStrut(20));

        // ---------- Content Panel ----------
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);

        contentPanel.add(new CoursePanel(), "Courses");
        contentPanel.add(new AttendancePanel(userId, dbConnector), "Attendance");
        contentPanel.add(new MedicalPanel(userId, dbConnector), "Medical");
        contentPanel.add(new GradePanel(userId), "Grades");
        contentPanel.add(new TimeTablePanel(dbConnector), "Time Table");
        contentPanel.add(new NoticePanel(userId), "Notices");
        contentPanel.add(new ProfilePanel(userId), "My Profile");
        contentPanel.add(new EnrollmentPanel(userId), "Enrollments");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Default selection
        setActiveButton(courseBtn, "Courses");
    }

    private JButton createNavButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setFont(font);
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SIDEBAR_COLOR);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(this);

        return btn;
    }

    private void resetButtons() {
        JButton[] buttons = {
                courseBtn, attendanceBtn, medicalBtn, gradeBtn,
                timeTableBtn, noticeBtn, profileBtn, enrollmentsBtn
        };

        for (JButton btn : buttons) {
            btn.setBackground(SIDEBAR_COLOR);
        }
    }

    private void setActiveButton(JButton btn, String cardName) {
        resetButtons();
        btn.setBackground(ACTIVE_COLOR);
        cardLayout.show(contentPanel, cardName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == courseBtn)
            setActiveButton(courseBtn, "Courses");

        else if (src == attendanceBtn)
            setActiveButton(attendanceBtn, "Attendance");

        else if (src == medicalBtn)
            setActiveButton(medicalBtn, "Medical");

        else if (src == gradeBtn)
            setActiveButton(gradeBtn, "Grades");

        else if (src == timeTableBtn)
            setActiveButton(timeTableBtn, "Time Table");

        else if (src == noticeBtn)
            setActiveButton(noticeBtn, "Notices");

        else if (src == profileBtn)
            setActiveButton(profileBtn, "My Profile");

        else if (src == enrollmentsBtn)
            setActiveButton(enrollmentsBtn, "Enrollments");

        else if (src == logOutBtn) {
            int res = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (res == JOptionPane.YES_OPTION) {
                dispose();
                new Login().setVisible(true);
            }
        }
    }
}