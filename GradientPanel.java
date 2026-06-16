import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(
                0, 0,
                new Color(102, 126, 234),
                getWidth(), getHeight(),
                new Color(118, 75, 162)
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}