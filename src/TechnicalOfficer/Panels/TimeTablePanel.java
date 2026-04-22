package TechnicalOfficer.Panels;

import Database.dbconnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TimeTablePanel extends JPanel {


    private DefaultTableModel model;
    private JLabel titileLable;
    private Font font1;
    private dbconnection dbconnection;
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    private JTable noticeTable;
    private JScrollPane scrollPane;

    public TimeTablePanel(){
        setLayout(null);
        initUI();

    }
    private void initUI(){
        font1 = new Font("Roboto",Font.PLAIN,30);
        titileLable = new JLabel("TIME TABLE");
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
        String[] columnNames = {"Time Table ID", "Department", "LecId", "Course ID","Admin ID","Day","Start_time","end_time","Time"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            dbconnection = new dbconnection();
            con = dbconnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM time_table");

            while (rs.next()) {
                String timeTableId = rs.getString(1);
                String department = rs.getString(2);
                String lecId = rs.getString(3);
                String courseId = rs.getString(4);
                String adminId = rs.getString(5);
                String day = rs.getString(6);
                String startTime = rs.getString(7);
                String endTime = rs.getString(8);
                String time = rs.getString(9);

                model.addRow(new Object[]{timeTableId,department,lecId,courseId,adminId,day,startTime,endTime,time});
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
            JOptionPane.showMessageDialog(this, "Failed to load time_table.");
        }
    }

}
