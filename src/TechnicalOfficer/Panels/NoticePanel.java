package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NoticePanel extends JPanel {


    private DefaultTableModel model;
    private JLabel titileLable;
    private Font font1;
    private dbconnection dbconnection;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private JTable noticeTable;
    private JScrollPane scrollPane;

    public NoticePanel(){
        setLayout(null);
        initUI();

    }
    private void initUI(){
           font1 = new Font("Roboto",Font.PLAIN,30);
           titileLable = new JLabel("NOTICES");
           titileLable.setBounds(20,20,300,50);
           titileLable.setFont(font1);
           add(titileLable);


           loadNotice();
    }
    private  void loadNotice(){
        // Remove old table if it exists
        if (scrollPane != null) {
            remove(scrollPane);
        }
        String[] columnNames = {"Notice ID", "Notice Title", "Notice Content", "Notice Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM notice");

            while (rs.next()) {
                String noticeId = rs.getString(1);
                String noticeTitle = rs.getString(2);
                String noticeContent = rs.getString(3);
                String noticeData = rs.getString(4);


                model.addRow(new Object[]{noticeId,noticeTitle,noticeContent,noticeData});
            }

            noticeTable = new JTable(model);
            scrollPane = new JScrollPane(noticeTable);
            scrollPane.setBounds(50, 100, 600, 150);
            add(scrollPane);


            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load attendance.");
        }
    }

}

