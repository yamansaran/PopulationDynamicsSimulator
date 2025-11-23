package PopulationDynamicsSimulator.src.main.java;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class AntRenderer {
    private BufferedImage antImage;
    private List<AntPosition> ants;
    private Random random;
    
    // Configurable parameters
    private double antScaleFactor = 0.05;  // Scale ant to 5% of original size
    private int minDistanceFromQueen = 100; // Minimum pixels from center
    private int marginFromEdge = 30;        // Margin from canvas edges
    
    // Animation parameters
    private long animationStartTime;
    private boolean animationEnabled = true;
    
    public AntRenderer() {
        ants = new ArrayList<>();
        random = new Random();
        loadAntImage();
        animationStartTime = System.currentTimeMillis();
    }
    
    private void loadAntImage() {
        try {
            // Try multiple possible locations for ant1.png
            String[] paths = {
                "PopulationDynamicsSimulator/src/main/res/ant1.png",
                "src/main/res/ant1.png",
                "res/ant1.png",
                "../res/ant1.png",
                "../../res/ant1.png",
                "ant1.png"
            };
            
            for (String path : paths) {
                File file = new File(path);
                if (file.exists() && file.canRead()) {
                    antImage = ImageIO.read(file);
                    if (antImage != null) {
                        System.out.println("Loaded ant1.png from: " + file.getAbsolutePath());
                        return;
                    }
                }
            }
            
            System.err.println("Could not find ant1.png - ants will not be rendered");
            antImage = null;
            
        } catch (Exception e) {
            System.err.println("Error loading ant1.png: " + e.getMessage());
            antImage = null;
        }
    }
    
    /**
     * Update the ant population to match the current count
     * @param targetPopulation The desired number of ants
     * @param canvasWidth Width of the canvas
     * @param canvasHeight Height of the canvas
     * @param queenCenterX X position of queen's center
     * @param queenCenterY Y position of queen's center
     */
    public void updateAntPopulation(int targetPopulation, int canvasWidth, int canvasHeight, 
                                   int queenCenterX, int queenCenterY) {
        int currentCount = ants.size();
        
        if (targetPopulation > currentCount) {
            // Add ants
            int toAdd = targetPopulation - currentCount;
            for (int i = 0; i < toAdd; i++) {
                AntPosition newAnt = generateRandomPosition(canvasWidth, canvasHeight, 
                                                           queenCenterX, queenCenterY);
                ants.add(newAnt);
            }
        } else if (targetPopulation < currentCount) {
            // Remove ants from the end
            int toRemove = currentCount - targetPopulation;
            for (int i = 0; i < toRemove; i++) {
                if (!ants.isEmpty()) {
                    ants.remove(ants.size() - 1);
                }
            }
        }
    }
    
    /**
     * Reposition all ants (useful when canvas size changes)
     */
    public void repositionAllAnts(int canvasWidth, int canvasHeight, 
                                 int queenCenterX, int queenCenterY) {
        for (AntPosition ant : ants) {
            Point newPos = generateValidPosition(canvasWidth, canvasHeight, 
                                                queenCenterX, queenCenterY);
            ant.baseX = newPos.x;
            ant.baseY = newPos.y;
        }
    }
    
    /**
     * Generate a random position that avoids the queen and stays within bounds
     */
    private AntPosition generateRandomPosition(int canvasWidth, int canvasHeight, 
                                              int queenCenterX, int queenCenterY) {
        Point pos = generateValidPosition(canvasWidth, canvasHeight, queenCenterX, queenCenterY);
        return new AntPosition(pos.x, pos.y, random.nextDouble() * 360, random);
    }
    
    private Point generateValidPosition(int canvasWidth, int canvasHeight, 
                                       int queenCenterX, int queenCenterY) {
        int maxAttempts = 100;
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            // Generate random position within bounds
            int x = marginFromEdge + random.nextInt(Math.max(1, canvasWidth - 2 * marginFromEdge));
            int y = marginFromEdge + random.nextInt(Math.max(1, canvasHeight - 2 * marginFromEdge));
            
            // Check distance from queen center
            double distanceFromQueen = Math.sqrt(
                Math.pow(x - queenCenterX, 2) + Math.pow(y - queenCenterY, 2)
            );
            
            if (distanceFromQueen >= minDistanceFromQueen) {
                return new Point(x, y);
            }
            
            attempt++;
        }
        
        // If we can't find a valid position, just place it at the edge
        return new Point(marginFromEdge, marginFromEdge);
    }
    
    /**
     * Render all ants onto the graphics context
     */
    public void renderAnts(Graphics2D g2) {
        if (antImage == null || ants.isEmpty()) {
            return;
        }
        
        int scaledWidth = (int) (antImage.getWidth() * antScaleFactor);
        int scaledHeight = (int) (antImage.getHeight() * antScaleFactor);
        
        // Get current animation time
        double currentTime = (System.currentTimeMillis() - animationStartTime) / 1000.0;
        
        for (AntPosition ant : ants) {
            // Calculate animated position
            Point animatedPos = ant.getAnimatedPosition(currentTime);
            double animatedRotation = ant.getAnimatedRotation(currentTime);
            
            // Save current transform
            var oldTransform = g2.getTransform();
            
            // Translate to ant position and rotate
            g2.translate(animatedPos.x, animatedPos.y);
            g2.rotate(Math.toRadians(animatedRotation));
            
            // Draw ant centered at its position
            g2.drawImage(antImage, -scaledWidth / 2, -scaledHeight / 2, 
                        scaledWidth, scaledHeight, null);
            
            // Restore transform
            g2.setTransform(oldTransform);
        }
    }
    
    /**
     * Get current ant count
     */
    public int getAntCount() {
        return ants.size();
    }
    
    /**
     * Clear all ants
     */
    public void clearAnts() {
        ants.clear();
    }
    
    /**
     * Enable or disable animation
     */
    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }
    
    // Setters for configurable parameters
    public void setAntScaleFactor(double scale) {
        this.antScaleFactor = scale;
    }
    
    public void setMinDistanceFromQueen(int distance) {
        this.minDistanceFromQueen = distance;
    }
    
    public void setMarginFromEdge(int margin) {
        this.marginFromEdge = margin;
    }
    
    // Inner class to hold ant position and orientation with animation
    private static class AntPosition {
        int baseX;      // Base X position
        int baseY;      // Base Y position
        double rotation; // Base rotation in degrees
        
        // Animation parameters (randomized per ant)
        AnimationType animType;
        double animSpeed;
        double animAmplitude;
        double phaseOffset;
        
        // For circular motion
        double circleRadius;
        double circleAngle;
        
        // For patrol motion
        double[] patrolPointsX;
        double[] patrolPointsY;
        
        enum AnimationType {
            JIGGLE,     // Small random jiggle in place
            CIRCLE,     // Move in a small circle
            FIGURE8,    // Move in a figure-8 pattern
            PATROL,     // Move between random waypoints (NEW!)
            STATIC      // No movement
        }
        
        AntPosition(int x, int y, double rotation, Random random) {
            this.baseX = x;
            this.baseY = y;
            this.rotation = rotation;
            this.phaseOffset = random.nextDouble() * Math.PI * 2;
            
            // Randomly assign animation type (weighted probabilities)
            double typeRoll = random.nextDouble();
            if (typeRoll < 0.25) {
                // 25% chance of jiggle (reduced from 40%)
                animType = AnimationType.JIGGLE;
                animSpeed = 1.0 + random.nextDouble() * 2.0; // 1-3 Hz
                animAmplitude = 1.0 + random.nextDouble() * 2.0; // 1-3 pixels
            } else if (typeRoll < 0.45) {
                // 20% chance of circle
                animType = AnimationType.CIRCLE;
                animSpeed = 0.3 + random.nextDouble() * 0.7; // 0.3-1.0 Hz
                circleRadius = 8.0 + random.nextDouble() * 20.0; // 8-28 pixels radius (larger!)
                circleAngle = random.nextDouble() * Math.PI * 2;
            } else if (typeRoll < 0.70) {
                // 25% chance of figure-8 (increased!)
                animType = AnimationType.FIGURE8;
                animSpeed = 0.15 + random.nextDouble() * 0.4; // 0.15-0.55 Hz (slower for bigger loops)
                animAmplitude = 15.0 + random.nextDouble() * 25.0; // 15-40 pixels (much larger!)
            } else if (typeRoll < 0.85) {
                // 15% chance of patrol (NEW - random waypoint walking)
                animType = AnimationType.PATROL;
                animSpeed = 0.2 + random.nextDouble() * 0.4; // 0.2-0.6 Hz
                animAmplitude = 20.0 + random.nextDouble() * 30.0; // 20-50 pixels
                // Generate random patrol points
                patrolPointsX = new double[4];
                patrolPointsY = new double[4];
                for (int i = 0; i < 4; i++) {
                    patrolPointsX[i] = (random.nextDouble() - 0.5) * animAmplitude * 2;
                    patrolPointsY[i] = (random.nextDouble() - 0.5) * animAmplitude * 2;
                }
            } else {
                // 15% chance of static (no animation)
                animType = AnimationType.STATIC;
            }
        }
        
        /**
         * Get the animated position based on current time
         */
        Point getAnimatedPosition(double time) {
            int x = baseX;
            int y = baseY;
            
            switch (animType) {
                case JIGGLE:
                    // Small random jiggle using sine waves with different frequencies
                    x += (int) (Math.sin(time * animSpeed * Math.PI * 2 + phaseOffset) * animAmplitude);
                    y += (int) (Math.cos(time * animSpeed * Math.PI * 2 + phaseOffset * 1.3) * animAmplitude);
                    break;
                    
                case CIRCLE:
                    // Circular motion
                    double angle = time * animSpeed * Math.PI * 2 + circleAngle;
                    x += (int) (Math.cos(angle) * circleRadius);
                    y += (int) (Math.sin(angle) * circleRadius);
                    break;
                    
                case FIGURE8:
                    // Figure-8 pattern (Lissajous curve with 2:1 frequency ratio)
                    double t = time * animSpeed * Math.PI * 2 + phaseOffset;
                    x += (int) (Math.sin(t) * animAmplitude);
                    y += (int) (Math.sin(t * 2) * animAmplitude * 0.5);
                    break;
                    
                case PATROL:
                    // Smooth patrol between waypoints using Catmull-Rom spline
                    double patrolT = (time * animSpeed) % 1.0; // 0 to 1
                    int numPoints = patrolPointsX.length;
                    
                    // Determine which segment we're on
                    double segmentLength = 1.0 / numPoints;
                    int segment = (int) (patrolT / segmentLength);
                    double localT = (patrolT % segmentLength) / segmentLength;
                    
                    // Get the 4 control points for Catmull-Rom (wrap around)
                    int p0 = (segment - 1 + numPoints) % numPoints;
                    int p1 = segment;
                    int p2 = (segment + 1) % numPoints;
                    int p3 = (segment + 2) % numPoints;
                    
                    // Catmull-Rom interpolation
                    double tt = localT;
                    double tt2 = tt * tt;
                    double tt3 = tt2 * tt;
                    
                    double q0 = -tt3 + 2*tt2 - tt;
                    double q1 = 3*tt3 - 5*tt2 + 2;
                    double q2 = -3*tt3 + 4*tt2 + tt;
                    double q3 = tt3 - tt2;
                    
                    x += (int) ((patrolPointsX[p0] * q0 + patrolPointsX[p1] * q1 + 
                                 patrolPointsX[p2] * q2 + patrolPointsX[p3] * q3) * 0.5);
                    y += (int) ((patrolPointsY[p0] * q0 + patrolPointsY[p1] * q1 + 
                                 patrolPointsY[p2] * q2 + patrolPointsY[p3] * q3) * 0.5);
                    break;
                    
                case STATIC:
                default:
                    // No animation
                    break;
            }
            
            return new Point(x, y);
        }
        
        /**
         * Get the animated rotation based on current time
         */
        double getAnimatedRotation(double time) {
            double rot = rotation;
            
            // Add slight rotation wobble for circular and figure-8 motion
            if (animType == AnimationType.CIRCLE) {
                rot += Math.sin(time * animSpeed * Math.PI * 4) * 5; // ±5 degree wobble
            } else if (animType == AnimationType.FIGURE8) {
                rot += Math.sin(time * animSpeed * Math.PI * 3) * 8; // ±8 degree wobble
            } else if (animType == AnimationType.JIGGLE) {
                rot += Math.sin(time * animSpeed * Math.PI * 6 + phaseOffset * 2) * 3; // ±3 degree wobble
            } else if (animType == AnimationType.PATROL) {
                // Rotate based on movement direction for more realistic walking
                double patrolT = (time * animSpeed) % 1.0;
                rot += Math.sin(patrolT * Math.PI * 8) * 10; // ±10 degree wobble, changes with position
            }
            
            return rot;
        }
    }
}