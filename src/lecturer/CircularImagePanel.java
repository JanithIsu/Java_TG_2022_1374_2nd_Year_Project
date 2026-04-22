package lecturer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CircularImagePanel extends JPanel {
    private BufferedImage image;
    private int diameter;

    public CircularImagePanel(String path, int diameter) {
        this.diameter = diameter;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Shape clip = new Ellipse2D.Float(0, 0, diameter, diameter);
        g2.setClip(clip);
        g2.drawImage(image, 0, 0, diameter, diameter, this);
        g2.dispose();
    }
}
