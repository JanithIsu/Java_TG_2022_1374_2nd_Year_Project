package Login;

import Admin.AdminFrame;
import TechnicalOfficer.TechnicalOfficer;
import lecturer.*;
import Database.dbconnection;
import javax.swing.*;
import java.sql.*;
import Undergraduate.*;

public class Login extends javax.swing.JFrame {

    public Login() {
        try {
            initComponents();

        }catch(Exception e){
            System.out.println(e);
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cancel = new javax.swing.JButton();
        login = new javax.swing.JButton();
        username = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        register = new javax.swing.JButton(); // <-- ADDED BUTTON
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(18, 52, 88));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LOGIN");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("USERNAME :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("PASSWORD : ");

        cancel.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        cancel.setText("CANCEL");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        login.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        login.setText("LOGIN");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernameActionPerformed(evt);
            }
        });

        password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordActionPerformed(evt);
            }
        });

        // --- NEW BUTTON CODE ---
        register.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        register.setText("REGISTER");
        register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerActionPerformed(evt);
            }
        });
        // --- END NEW BUTTON CODE ---

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(74, 74, 74)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(username)
                                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(119, 119, 119))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        // --- MODIFIED LAYOUT ---
                                                        .addComponent(login)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(register)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(cancel)
                                                        .addGap(30, 30, 30))
                                        // --- END MODIFIED LAYOUT ---
                                ))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(18, 18, 18)
                                                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(65, 65, 65)
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(login)
                                        .addComponent(cancel)
                                        .addComponent(register)) // <-- ADDED TO VERTICAL LAYOUT
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(212, 201, 190));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SystemImages/LOGO_OF_RUHUNA-Alt3.png"))); // NOI18N
        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(62, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        dispose();
    }//GEN-LAST:event_cancelActionPerformed


    // --- (MODIFIED) ---
    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        String username_new =  username.getText();
        String password_new = new String(password.getPassword());

        // This string will now hold a "status" (e.g., "admin", "USER_NOT_FOUND", etc.)
        String loginStatus = authenticate(username_new,password_new);

        switch (loginStatus) {
            case "USER_NOT_FOUND":
                // 2.a user is not register, 1. system display” please register”.
                JOptionPane.showMessageDialog(this, "please register");
                break;
            case "INVALID_PASSWORD":
                // 1.a user enter invalid credentials, 1. system display “invalid username or password”
                JOptionPane.showMessageDialog(this, "invalid username or password");
                break;
            case "DB_ERROR":
                JOptionPane.showMessageDialog(this, "Database error. Please contact admin.");
                break;
            // --- SUCCESS CASES ---
            case "admin":
            case "lecturer":
            case "undergraduate":
            case "tech_officer":
                dispose();
                openRoleWindow(loginStatus); // loginStatus is the role
                break;
            default:
                // This handles any other unexpected case
                JOptionPane.showMessageDialog(this, "An unknown error occurred.");
        }
    }//GEN-LAST:event_loginActionPerformed

    // --- (FIXED) ---
    private String authenticate(String username, String password) {
        // Step 1: Query for the user by user_id only
        String query = "SELECT user_password, user_role FROM user WHERE user_id = ?";

        // This try-with-resources only handles SQL-related errors
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // --- User FOUND ---
                String storedPasswordHash = rs.getString("user_password");
                String userRole = rs.getString("user_role");

                // Step 2: Hash the provided password and compare it
                try {
                    String providedPasswordHash = Admin.encryption.hashpassword(new String(password));

                    if (providedPasswordHash.equals(storedPasswordHash)) {
                        // --- SUCCESS: Password matches ---
                        return userRole;
                    } else {
                        // --- FAILURE: Wrong Password ---
                        return "INVALID_PASSWORD";
                    }
                } catch (Exception e) {
                    // --- FAILURE: Hashing algorithm failed ---
                    System.err.println("Password hashing error!");
                    e.printStackTrace();
                    return "DB_ERROR"; // A hashing error is a system error
                }

            } else {
                // --- FAILURE: User was NOT found ---
                return "USER_NOT_FOUND";
            }

        } catch (SQLException ex) { // <-- CATCH ONLY SQL EXCEPTIONS
            ex.printStackTrace();
            // --- FAILURE: Database error ---
            return "DB_ERROR";
        }
    }

    private void usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernameActionPerformed

    private void passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordActionPerformed

    // --- (MODIFIED) ---
    private void registerActionPerformed(java.awt.event.ActionEvent evt) {
        // Assuming RegisterForm is in the same 'Login' package
        new Register.RegisterForm().setVisible(true);
        dispose(); // Close the login form
    }
    // --- END NEW METHOD ---


    // --- (FIXED) ---
    private void openRoleWindow(String role) {
        // 1. Check if role is null
        if (role == null) {
            JOptionPane.showMessageDialog(this, "Role is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Clean the string: remove whitespace and force to lowercase
        String cleanRole = role.trim().toLowerCase();

        // 3. Switch on the CLEAN string
        switch (cleanRole) {
            case "admin":
                this.dispose();
                AdminFrame add = new AdminFrame(username.getText().toLowerCase());
                add.setVisible(true);
                break;
            case "lecturer":
                Lecture_profile lec = new Lecture_profile(username.getText().toLowerCase());
                lec.setVisible(true);

                break;
            case "undergraduate":
                //new UndergraduateWindow(); // Open Undergraduate window
                //JOptionPane.showMessageDialog(null, "Hello boiya");
                Undergraduate ug = new Undergraduate(username.getText().toLowerCase());
                ug.setVisible(true);
                break;
            case "tech_officer":

                TechnicalOfficer to = new TechnicalOfficer(username.getText().toLowerCase());
                to.setVisible(true);
                break;
            default:
                // This message is now more helpful
                JOptionPane.showMessageDialog(this, "Role not find: '" + role + "'");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton login;
    private javax.swing.JPasswordField password;
    private javax.swing.JButton register; // <-- ADDED VARIABLE
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}