package PopulationDynamicsSimulator.src.main.java;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Actions label panel with background image and "ACTIONS" text.
 */
public class ActionsLabelPanel extends JPanel {
    private BufferedImage backgroundImage;
    
    public ActionsLabelPanel() {
        backgroundImage = ImageLoader.load("actionslabel.png");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        g2.setColor(Color.RED);
        g2.setFont(GameFonts.ACTIONS);
        String text = "ACTIONS";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, 
                     (getHeight() + fm.getAscent()) / 2 - 2);
    }
}
