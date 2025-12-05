package PopulationDynamicsSimulator.src.main.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

/**
 * Zoomable and pannable graph panel for popup windows.
 * Supports non-linear scaling for bifurcation diagrams.
 */
public class ZoomableGraphPanel extends JPanel {
    private String title;
    private Color plotColor;
    private List<AntColonyGame.Point2D> data;
    private boolean markFirstPoint;
    private double zoomLevel = 1.0;
    private double panX = 0, panY = 0;
    private Point lastMousePos;
    
    // Bifurcation scaling parameters
    private boolean useNonLinearScaling = true;
    private double scalingPower = 0.4;  // < 1 stretches higher values, compresses lower
    
    public ZoomableGraphPanel(String title, Color plotColor, List<AntColonyGame.Point2D> data, boolean markFirstPoint) {
        this.title = title;
        this.plotColor = plotColor;
        this.data = data;
        this.markFirstPoint = markFirstPoint;
        setBackground(Color.WHITE);
        
        addMouseWheelListener(e -> {
            zoomLevel *= (e.getWheelRotation() < 0) ? 1.1 : 0.9;
            zoomLevel = Math.max(0.1, Math.min(zoomLevel, 10.0));
            repaint();
        });
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                lastMousePos = e.getPoint();
            }
        });
        
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (lastMousePos != null) {
                    panX += (e.getX() - lastMousePos.x) / (zoomLevel * 5.0);
                    panY -= (e.getY() - lastMousePos.y) / (zoomLevel * 5.0);
                    lastMousePos = e.getPoint();
                    repaint();
                }
            }
        });
    }
    
    /**
     * Apply non-linear transformation to x-coordinate for bifurcation diagrams.
     * Uses power-law scaling: transformed = ((x - min) / range)^power
     * Power < 1 stretches higher values (where bifurcations occur)
     */
    private double transformX(double x, double minX, double maxX) {
        if (!useNonLinearScaling || !title.contains("Bifurcation")) {
            return x;
        }
        
        double range = maxX - minX;
        if (range < 0.001) return x;
        
        // Normalize to [0, 1]
        double normalized = (x - minX) / range;
        
        // Apply power transformation
        double transformed = Math.pow(normalized, scalingPower);
        
        // Scale back to original range
        return minX + transformed * range;
    }
    
    /**
     * Get the display x-coordinate for a given data x value
     */
    private int getDisplayX(double dataX, double minX, double maxX, int margin, int plotWidth) {
        double transformedX = transformX(dataX, minX, maxX);
        double transformedMin = transformX(minX, minX, maxX);
        double transformedMax = transformX(maxX, minX, maxX);
        
        return margin + (int) ((transformedX - transformedMin) / (transformedMax - transformedMin) * plotWidth);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int margin = 60;
        
        g2.setColor(Color.BLACK);
        g2.setFont(GameFonts.TITLE);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 30);
        
        g2.setFont(GameFonts.SMALL);
        String controlsText = String.format("Zoom: %.1fx (Scroll to zoom, Drag to pan)", zoomLevel);
        if (title.contains("Bifurcation") && useNonLinearScaling) {
            controlsText += " | Non-linear X scaling active";
        }
        g2.drawString(controlsText, 10, height - 10);
        
        if (data == null || data.isEmpty()) return;
        
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
        
        // Store original bounds for axis labels
        double origMinX = minX;
        double origMaxX = maxX;
        
        if (title.contains("History") || title.contains("Projection")) {
            minX = 0;
            minY = 0;
        } else if (title.contains("Bifurcation") || title.contains("Lyapunov")) {
            minX = 0.0;
            minY -= rangeY * 0.05;
            maxY += rangeY * 0.05;
            origMinX = minX;
        } else {
            minX -= rangeX * 0.05;
            minY -= rangeY * 0.05;
            maxX += rangeX * 0.05;
            maxY += rangeY * 0.05;
        }
        
        // Apply zoom and pan
        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;
        double zoomedRangeX = (maxX - minX) / zoomLevel;
        double zoomedRangeY = (maxY - minY) / zoomLevel;
        
        minX = centerX - zoomedRangeX / 2 - panX;
        maxX = centerX + zoomedRangeX / 2 - panX;
        minY = centerY - zoomedRangeY / 2 - panY;
        maxY = centerY + zoomedRangeY / 2 - panY;
        
        // Draw axes
        g2.setColor(Color.GRAY);
        g2.drawLine(margin, height - margin, width - margin, height - margin);
        g2.drawLine(margin, margin, margin, height - margin);
        
        // Draw axis labels
        g2.setFont(GameFonts.SMALL);
        g2.drawString(String.format("%.2f", minX), margin - 30, height - margin + 20);
        g2.drawString(String.format("%.2f", maxX), width - margin - 30, height - margin + 20);
        g2.drawString(String.format("%.2f", minY), 10, height - margin);
        g2.drawString(String.format("%.2f", maxY), 10, margin + 10);
        
        // Draw intermediate tick marks for bifurcation with non-linear scaling
        if (title.contains("Bifurcation") && useNonLinearScaling) {
            g2.setColor(new Color(200, 200, 200));
            g2.setFont(GameFonts.TINY);
            int plotWidth = width - 2 * margin;
            
            // Draw tick marks at regular intervals in data space
            double tickInterval = (maxX - minX) / 5;
            for (int i = 1; i < 5; i++) {
                double tickValue = minX + i * tickInterval;
                int tickX = getDisplayX(tickValue, minX, maxX, margin, plotWidth);
                g2.drawLine(tickX, height - margin - 5, tickX, height - margin + 5);
                g2.drawString(String.format("%.2f", tickValue), tickX - 15, height - margin + 18);
            }
        }
        
        g2.setColor(plotColor);
        g2.setStroke(new BasicStroke(2));
        int plotWidth = width - 2 * margin;
        int plotHeight = height - 2 * margin;
        
        if (title.contains("Bifurcation")) {
            // For bifurcation, draw every point individually with non-linear x scaling
            for (AntColonyGame.Point2D p : data) {
                if (p.x < minX || p.x > maxX) continue;
                
                int x = getDisplayX(p.x, minX, maxX, margin, plotWidth);
                int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
                
                // Bounds check
                if (x >= margin && x <= width - margin && y >= margin && y <= height - margin) {
                    g2.fillRect(x, y, 1, 1);
                }
            }
        } else {
            for (int i = 0; i < data.size() - 1; i++) {
                AntColonyGame.Point2D p1 = data.get(i);
                AntColonyGame.Point2D p2 = data.get(i + 1);
                int x1 = margin + (int) ((p1.x - minX) / (maxX - minX) * plotWidth);
                int y1 = height - margin - (int) ((p1.y - minY) / (maxY - minY) * plotHeight);
                int x2 = margin + (int) ((p2.x - minX) / (maxX - minX) * plotWidth);
                int y2 = height - margin - (int) ((p2.y - minY) / (maxY - minY) * plotHeight);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
        
        if (title.contains("Bifurcation") && !data.isEmpty()) {
            AntColonyGame.Point2D p = data.get(data.size() - 1);
            int x = getDisplayX(p.x, minX, maxX, margin, plotWidth);
            int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
            g2.fillRect(x, y, 1, 1);
        }
        
        if (markFirstPoint && !data.isEmpty()) {
            AntColonyGame.Point2D p = data.get(0);
            int x;
            if (title.contains("Bifurcation")) {
                x = getDisplayX(p.x, minX, maxX, margin, plotWidth);
            } else {
                x = margin + (int) ((p.x - minX) / (maxX - minX) * plotWidth);
            }
            int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
            
            g2.setColor(Color.RED);
            g2.fillOval(x - 8, y - 8, 16, 16);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - 8, y - 8, 16, 16);
        }
    }
    
    // Setters for scaling parameters
    public void setUseNonLinearScaling(boolean use) {
        this.useNonLinearScaling = use;
        repaint();
    }
    
    public void setScalingPower(double power) {
        this.scalingPower = power;
        repaint();
    }
}