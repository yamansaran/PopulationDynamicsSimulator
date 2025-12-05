package PopulationDynamicsSimulator.src.main.java;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * News panel with background image.
 */
public class NewsPanel extends JPanel {
    private BufferedImage backgroundImage;
    private JTextArea textArea;
    
    public NewsPanel() {
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        backgroundImage = ImageLoader.load("newspanel.png");
    }
    
    public JTextArea getTextArea() {
        return textArea;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            ((Graphics2D) g).drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
