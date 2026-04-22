package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.io.File;
import java.sql.*;
import Database.dbconnection;

/**
 * User profile tab: shows user info, picture, and edit/change‑picture options.
 */
public class ProfileTab extends JPanel {
    private final JLabel userLbl  = new JLabel();
    private final JLabel emailLbl = new JLabel();
    private final JLabel roleLbl  = new JLabel();
    private final JLabel phoneLbl = new JLabel();
    private final JLabel picLbl   = new JLabel("Profile Pic", SwingConstants.CENTER);

    private final String currentUser;

    public ProfileTab(String username) {
        this.currentUser = username;
        setLayout(new BorderLayout());
        setBackground(new Color(140, 140, 140));

        //---------------------------------------------------
        // Info panel (text fields)
        //---------------------------------------------------
        JPanel info = new JPanel(new GridLayout(5, 2, 8, 8));
        info.add(new JLabel("Name:"));  info.add(userLbl);
        info.add(new JLabel("Email:")); info.add(emailLbl);
        info.add(new JLabel("Role:"));  info.add(roleLbl);
        info.add(new JLabel("Phone:")); info.add(phoneLbl);

        //---------------------------------------------------
        // Buttons panel
        //---------------------------------------------------
        JButton editBtn = new JButton("Edit Profile");
        JButton changePicBtn = new JButton("Change Picture");

        editBtn.addActionListener(e -> editProfileActionPerformed());
        changePicBtn.addActionListener(e -> changePicture());

        JPanel south = new JPanel();
        south.add(editBtn);
        south.add(changePicBtn);

        //---------------------------------------------------
        // Profile picture placeholder
        //---------------------------------------------------
        picLbl.setPreferredSize(new Dimension(150,150));
        picLbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(info, BorderLayout.WEST);
        add(picLbl, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        //---------------------------------------------------
        // Populate the profile info and load image
        //---------------------------------------------------
        display_profile(currentUser);

        // Use invokeLater → size will exist
        SwingUtilities.invokeLater(() -> {
            setDefaultProfilePic();
            loadProfilePic();
        });
    }

    //=========================================================
    //    ACTIONS & DATABASE INTERACTIONS
    //=========================================================

    private void editProfileActionPerformed() {
        editprofiledailog dialog = new editprofiledailog(null, true, currentUser);
        dialog.setVisible(true);
        display_profile(currentUser);
        loadProfilePic();
    }

    /** Displays textual user info. */
    private void display_profile(String username) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    userLbl.setText(rs.getString("user_name"));
                    emailLbl.setText(rs.getString("user_email"));
                    roleLbl.setText(rs.getString("user_role"));
                    phoneLbl.setText(rs.getString("user_phone"));
                }
            }
        } catch (Exception e) {
            Alerts.fail(e.getMessage());
        }
    }

    /** Tries to load user‑specific profile picture. */
    private void loadProfilePic() {
        try (Connection con = dbconnection.getConnection();
             PreparedStatement pst = con.prepareStatement(
                     "SELECT user_pro_pic FROM user WHERE user_id = ?")) {
            pst.setString(1, currentUser);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String path = rs.getString("user_pro_pic");
                    if (path != null && !path.isEmpty() && new File(path).exists()) {
                        picLbl.setIcon(resizeImage(path, picLbl));
                        return;
                    }
                }
            }
            setDefaultProfilePic();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Sets default placeholder picture if user has none. */
    private void setDefaultProfilePic() {
        String defaultPath = "src/Images/Default_pfp.svg.png";
        File f = new File(defaultPath);
        if (f.exists()) {
            picLbl.setIcon(resizeImage(defaultPath, picLbl));
        } else {
            // fallback text if image missing
            picLbl.setText("No Image");
            picLbl.setIcon(null);
        }
    }

    //=========================================================
    //    IMAGE HELPERS
    //=========================================================

    /**
     * Resizes image safely, even when label has not yet been displayed.
     */
    private ImageIcon resizeImage(String path, JLabel label) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();

        int w = label.getWidth();
        int h = label.getHeight();

        // Fallback to preferred or default size if component not realized yet
        if (w <= 0 || h <= 0) {
            Dimension pref = label.getPreferredSize();
            w = (pref.width  > 0) ? pref.width  : 150;
            h = (pref.height > 0) ? pref.height : 150;
        }

        Image newImg = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    //=========================================================
    //    PICTURE FILE SELECTION
    //=========================================================

    private void changePicture() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();

            if (!isValidImageFile(path)) {
                Alerts.fail("Invalid format. Choose JPG, PNG, GIF or BMP.");
                return;
            }

            try (Connection c = dbconnection.getConnection();
                 PreparedStatement pst = c.prepareStatement(
                         "UPDATE user SET user_pro_pic = ? WHERE user_id = ?")) {
                pst.setString(1, path);
                pst.setString(2, currentUser);
                pst.executeUpdate();
                Alerts.success("Profile photo updated.");
                loadProfilePic();
            } catch (Exception e) {
                Alerts.fail(e.getMessage());
            }
        }
    }

    private boolean isValidImageFile(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".jpg")  ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png")  ||
                lower.endsWith(".gif")  ||
                lower.endsWith(".bmp");
    }
}