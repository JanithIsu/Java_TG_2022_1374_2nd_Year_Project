package Admin;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {
    private final String username;
    private final JTabbedPane tabs;

    public AdminFrame(String u_name) {
        this.username = u_name;
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.addTab("User", new UserTab(username));
        tabs.addTab("Courses", new CourseTab(username));
        tabs.addTab("Notices", new NoticeTab(username));
        tabs.addTab("Time Table", new TimeTableTab(username));
        tabs.addTab("Profile", new ProfileTab(username));

        JButton logout = new JButton("LOGOUT");
        logout.addActionListener(e -> logout());

        add(tabs, BorderLayout.CENTER);
        add(logout, BorderLayout.SOUTH);
    }

    private void logout() {
        dispose();
        new Login.Login().setVisible(true); // same call as original
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminFrame("admin1").setVisible(true));
    }
}