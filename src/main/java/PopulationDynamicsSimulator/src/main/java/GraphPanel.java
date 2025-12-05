package PopulationDynamicsSimulator.src.main.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Panel for displaying graph data with click-to-zoom.
 */
public class GraphPanel extends JPanel {
    private String title;
    private Color plotColor;
    private List<AntColonyGame.Point2D> data;
    private boolean markFirstPoint;
    
    public GraphPanel(String title, Color plotColor) {
        this.title = title;
        this.plotColor = plotColor;
        this.markFirstPoint = false;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openGraphWindow();
            }
        });
    }
    
    private void openGraphWindow() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog graphDialog = new JDialog(parent instanceof JFrame ? (JFrame) parent : null, title, false);
        graphDialog.setSize(800, 600);
        graphDialog.setLocationRelativeTo(parent);
        graphDialog.add(new ZoomableGraphPanel(title, plotColor, data, markFirstPoint));
        graphDialog.setVisible(true);
    }
    
    public void setData(List<AntColonyGame.Point2D> data) {
        setData(data, false);
    }
    
    public void setData(List<AntColonyGame.Point2D> data, boolean markFirstPoint) {
        this.data = data;
        this.markFirstPoint = markFirstPoint;
        repaint();
    }
    
    public String getTitle() { return title; }
    public Color getPlotColor() { return plotColor; }
    public List<AntColonyGame.Point2D> getData() { return data; }
    public boolean isMarkFirstPoint() { return markFirstPoint; }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int margin = 30;
        
        // Draw title
        g2.setColor(Color.BLACK);
        g2.setFont(GameFonts.SMALL);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 15);
        
        if (data == null || data.isEmpty()) return;
        
        // Calculate bounds
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        
        for (AntColonyGame.Point2D p : data) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }
        
        if (title.contains("History") || title.contains("Projection")) {
            minX = Math.max(0, minX);
            minY = Math.max(0, minY);
        }
        
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;
        if (rangeX < 0.001) rangeX = 1;
        if (rangeY < 0.001) rangeY = 1;
        
        if (title.contains("History") || title.contains("Projection")) {
            minX = 0;
            minY = 0;
        } else {
            minX -= rangeX * 0.05;
            minY -= rangeY * 0.05;
        }
        maxX += rangeX * 0.05;
        maxY += rangeY * 0.05;
        
        // Draw axes
        g2.setColor(Color.GRAY);
        g2.drawLine(margin, height - margin, width - margin, height - margin);
        g2.drawLine(margin, margin, margin, height - margin);
        
        // Draw axis labels
        g2.setFont(GameFonts.TINY);
        g2.drawString(String.format("%.1f", minX), margin - 15, height - margin + 12);
        g2.drawString(String.format("%.1f", maxX), width - margin - 15, height - margin + 12);
        g2.drawString(String.format("%.1f", minY), 3, height - margin);
        g2.drawString(String.format("%.1f", maxY), 3, margin + 5);
        
        // Draw data
        g2.setColor(plotColor);
        int plotWidth = width - 2 * margin;
        int plotHeight = height - 2 * margin;
        
        for (int i = 0; i < data.size() - 1; i++) {
            AntColonyGame.Point2D p1 = data.get(i);
            AntColonyGame.Point2D p2 = data.get(i + 1);
            
            int x1 = margin + (int) ((p1.x - minX) / (maxX - minX) * plotWidth);
            int y1 = height - margin - (int) ((p1.y - minY) / (maxY - minY) * plotHeight);
            int x2 = margin + (int) ((p2.x - minX) / (maxX - minX) * plotWidth);
            int y2 = height - margin - (int) ((p2.y - minY) / (maxY - minY) * plotHeight);
            
            if (title.contains("Bifurcation")) {
                g2.fillOval(x1 - 1, y1 - 1, 2, 2);
            } else {
                g2.drawLine(x1, y1, x2, y2);
            }
        }
        
        if (title.contains("Bifurcation") && !data.isEmpty()) {
            AntColonyGame.Point2D p = data.get(data.size() - 1);
            int x = margin + (int) ((p.x - minX) / (maxX - minX) * plotWidth);
            int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
            g2.fillOval(x - 1, y - 1, 2, 2);
        }
        
        // Draw "you are here" marker
        if (markFirstPoint && !data.isEmpty()) {
            AntColonyGame.Point2D p = data.get(0);
            int x = margin + (int) ((p.x - minX) / (maxX - minX) * plotWidth);
            int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
            
            g2.setColor(Color.RED);
            g2.fillOval(x - 5, y - 5, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - 5, y - 5, 10, 10);
        }
    }
}
