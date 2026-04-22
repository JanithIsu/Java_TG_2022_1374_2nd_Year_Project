package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;
import java.sql.Date;
import Database.dbconnection;

/**
 * Panel containing Notice table and CRUD actions.
 * All logic preserved from your original admin.java methods.
 */
public class NoticeTab extends JPanel {

    private JTable noticeTable;
    private JTextField nIdField, nTitleField;
    private JTextArea nContentArea;
    private final String currentUser;

    public NoticeTab(String username) {
        this.currentUser = username;
        setLayout(new BorderLayout());
        setBackground(new Color(140, 140, 140));

        // ---- Table --------------------------------------------------------
        noticeTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Notice ID","Notice Title","Notice Content","Notice Date"}));
        add(new JScrollPane(noticeTable), BorderLayout.CENTER);

        // ---- Controls -----------------------------------------------------
        JPanel controls = new JPanel(new GridLayout(3, 2, 5, 5));
        nIdField = new JTextField();       // for delete or update
        nTitleField = new JTextField();    // for add/update
        nContentArea = new JTextArea(3, 20);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e -> addNoticeActionPerformed());
        updateBtn.addActionListener(e -> updateNoticeActionPerformed());
        deleteBtn.addActionListener(e -> deleteNoticeActionPerformed());

        JPanel buttonRow = new JPanel();
        buttonRow.add(addBtn);
        buttonRow.add(updateBtn);
        buttonRow.add(deleteBtn);

        controls.add(new JLabel("Notice ID")); controls.add(nIdField);
        controls.add(new JLabel("Title")); controls.add(nTitleField);
        controls.add(new JLabel("Content")); controls.add(new JScrollPane(nContentArea));

        add(new JScrollPane(controls), BorderLayout.NORTH);
        add(buttonRow, BorderLayout.SOUTH);

        notice_tbload();
    }

    // ---------- Logic identical to admin.java -----------------------------
    public void notice_tbload() {
        DefaultTableModel dt = (DefaultTableModel) noticeTable.getModel();
        dt.setRowCount(0);
        try (Connection conn = dbconnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM notice")) {
            while (rs.next()) {
                Vector<Object> v = new Vector<>();
                v.add(rs.getString(1));
                v.add(rs.getString(2));
                v.add(rs.getString(3));
                v.add(rs.getDate(4));
                dt.addRow(v);
            }
        } catch(Exception e){ e.printStackTrace(); }
    }

    private void addNoticeActionPerformed() {
        LocalDate localdate = LocalDate.now();
        String title = nTitleField.getText();
        String content = nContentArea.getText();
        try {
            if(!Admin.addNotice.validation(title,content)) return;
            Date sqlDate = Date.valueOf(localdate);
            if(Admin.addNotice.addNotice(title, content, sqlDate)) {
                JOptionPane.showMessageDialog(this, "Notice added successfully");
                notice_tbload();
                nIdField.setText(""); nTitleField.setText(""); nContentArea.setText("");
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void updateNoticeActionPerformed() {
        LocalDate localdate = LocalDate.now();
        String idText = nIdField.getText().trim();
        String title = nTitleField.getText();
        String content = nContentArea.getText();
        try {
            if(!Admin.updateNotice.validation(idText, title, content)) return;
            int id = Integer.parseInt(idText);
            Date sqlDate = Date.valueOf(localdate);
            if(Admin.updateNotice.updateNotice(id, title, content, sqlDate)) {
                JOptionPane.showMessageDialog(this,"Notice updated successfully");
                notice_tbload();
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this,e.getMessage()); }
    }

    private void deleteNoticeActionPerformed() {
        String idText = nIdField.getText().trim();
        try {
            if(!Admin.deleteNotice.validation(idText)) return;
            int id = Integer.parseInt(idText);
            if(Admin.deleteNotice.deleteNotice(id)) {
                JOptionPane.showMessageDialog(this,"Notice deleted");
                notice_tbload();
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this,e.getMessage()); }
    }
}