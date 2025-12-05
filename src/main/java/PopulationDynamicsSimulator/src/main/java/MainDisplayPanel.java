package PopulationDynamicsSimulator.src.main.java;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Main display panel showing queen and worker ants.
 */
public class MainDisplayPanel extends JPanel {
    private BufferedImage queenImage;
    private AntRenderer antRenderer;
    private double imageScaleFactor = 0.2;
    
    public MainDisplayPanel() {
        setBackground(new Color(245, 222, 179));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        antRenderer = new AntRenderer();
        queenImage = ImageLoader.load("queen.png");
    }
    
    public void updateAnts(int population) {
        int queenCenterX = getWidth() / 2;
        int queenCenterY = getHeight() / 2;
        antRenderer.updateAntPopulation(population, getWidth(), getHeight(), 
                                      queenCenterX, queenCenterY);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        antRenderer.renderAnts(g2);
        
        if (queenImage != null) {
            int scaledWidth = (int) (queenImage.getWidth() * imageScaleFactor);
            int scaledHeight = (int) (queenImage.getHeight() * imageScaleFactor);
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2;
            g2.drawImage(queenImage, x, y, scaledWidth, scaledHeight, null);
        } else {
            g2.setColor(Color.GRAY);
            g2.setFont(GameFonts.LARGE);
            String msg = "Place queen.png in res/ folder";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
        }
    }
}
