package PopulationDynamicsSimulator.src.main.java;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Actions panel with background image.
 */
public class ActionsPanel extends JPanel {
    private BufferedImage backgroundImage;
    
    public ActionsPanel() {
        backgroundImage = ImageLoader.load("actionspanel.png");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            ((Graphics2D) g).drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
