package TechnicalOfficer;
import TechnicalOfficer.Panels.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Login.Login;

public class TechnicalOfficer extends JFrame implements ActionListener {

    private String userId;
    private JPanel leftPanel,rightPanel;
    private Color color1,color2;
    private CardLayout cardLayout;
    private JButton attendanceBtn,medicalBtn,profileBtn,timeTableBtn,noticeBtn,logOutBtn;
    private Font font1;
    
    public TechnicalOfficer(String userId){
        this.userId = userId;
        initUi();
        setLocationRelativeTo(null);

    }

    private void initUi(){

        color1 = new Color(153,187,187);
        color2 = new Color(156, 76, 62);

        font1 = new Font("Roboto",Font.PLAIN,15);

        setTitle("Technical Officer");
        setLayout(null);
        setSize(1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        //make cardlayout
        cardLayout = new CardLayout();

        leftPanel = new JPanel(null);
        leftPanel.setBounds(0,0,200,600);
        leftPanel.setBackground(color1);

        //right panel add cardLayout
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBounds(200,0,800,600);

        //left panel button create-----
        attendanceBtn = createbuttons("ATTENDANCE",150);
        medicalBtn = createbuttons("MEDICAL",200);
        profileBtn = createbuttons("PROFILE",250);
        timeTableBtn = createbuttons("TIME TABLES",300);
        noticeBtn = createbuttons("NOTICES",350);
        logOutBtn = createbuttons("Log Out", 500);
        logOutBtn.setForeground(Color.RED);

        //All button add leftpanel
        leftPanel.add(medicalBtn);
        leftPanel.add(attendanceBtn);
        leftPanel.add(profileBtn);
        leftPanel.add(noticeBtn);
        leftPanel.add(timeTableBtn);
        leftPanel.add(logOutBtn);

        //left & Right panel add JFRAME
        add(leftPanel);
        add(rightPanel);

        //right panel added all Panels
        rightPanel.add(new AttendancePanel(userId),"ATTENDANCE");
        rightPanel.add(new MedicalPanel(userId),"MEDICAL");
        rightPanel.add(new NoticePanel(),"NOTICE");
        rightPanel.add(new TimeTablePanel(),"TIMETABLE");
        rightPanel.add(new ProfilePanel(userId),"PROFILE");


        //default show tabPane--
        cardLayout.show(rightPanel,"ATTENDANCE");

        setVisible(true);
    }

    private JButton createbuttons(String text, int y){
        JButton btn = new JButton(text);
        btn.setBounds(0,y,200,50);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color1);
        btn.setFont(font1);
        btn.setFocusable(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.addActionListener(this);

        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == attendanceBtn){
            cardLayout.show(rightPanel,"ATTENDANCE");


        } else if (e.getSource() == medicalBtn) {
            cardLayout.show(rightPanel,"MEDICAL");

        } else if (e.getSource()==noticeBtn) {
            cardLayout.show(rightPanel,"NOTICE");

        } else if (e.getSource()==profileBtn) {
            cardLayout.show(rightPanel,"PROFILE");

        } else if (e.getSource()==timeTableBtn) {
            cardLayout.show(rightPanel,"TIMETABLE");

        }  else if (e.getSource() == logOutBtn) {
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                profileBtn.setEnabled(false);
                profileBtn.setForeground(Color.WHITE);

                this.dispose();
                Login login = new Login();
                login.setVisible(true);
            }
        }

    }
}
