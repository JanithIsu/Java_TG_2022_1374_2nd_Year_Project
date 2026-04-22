package Admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import Database.dbconnection;

public class UserTab extends JPanel {
    private final JTable table = new JTable();
    private final String currentUser;

    public UserTab(String username) {
        this.currentUser = username;
        setLayout(new BorderLayout());
        table.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"User ID","Name","Email","Phone","Role"}));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton add = new JButton("ADD");
        JButton update = new JButton("UPDATE");
        JButton delete = new JButton("DELETE");
        actions.add(add); actions.add(update); actions.add(delete);
        add(actions, BorderLayout.SOUTH);

        // Wire up your original handlers
        add.addActionListener(e -> addUserActionPerformed());
        update.addActionListener(e -> updateUserActionPerformed());
        delete.addActionListener(e -> deleteUserActionPerformed());

        tbload(); // same behaviour
    }

    public void tbload() {
        try (Connection c = dbconnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM user")) {
            DefaultTableModel dt = (DefaultTableModel) table.getModel();
            dt.setRowCount(0);
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i=1;i<=7;i++) row.add(rs.getObject(i));
                dt.addRow(row);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // These three call your existing Admin.addUser/updateUser/deleteUser classes unchanged
    private void addUserActionPerformed() {
        AdminService.addUserDialog(this, table);
        tbload();
    }
    private void updateUserActionPerformed() {
        AdminService.updateUserDialog(this, table);
        tbload();
    }
    private void deleteUserActionPerformed() {
        AdminService.deleteUserDialog(this, table);
        tbload();
    }
}