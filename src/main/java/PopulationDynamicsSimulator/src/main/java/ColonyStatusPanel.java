package PopulationDynamicsSimulator.src.main.java;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Colony status panel with background image.
 */
public class ColonyStatusPanel extends JPanel {
    private BufferedImage backgroundImage;
    
    public ColonyStatusPanel() {
        setLayout(new BorderLayout());
        backgroundImage = ImageLoader.load("colonystatus.png");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            ((Graphics2D) g).drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
