package PopulationDynamicsSimulator.src.main.java;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Renders animated worker ants around the queen.
 */
public class AntRenderer {
    private List<BufferedImage> antImages;
    private List<Double> antWeights;  // Weights for each ant image (probabilities)
    private List<AntPosition> ants;
    private Random random;
    
    // Configurable parameters
    private double antScaleFactor = 0.05;
    private int minDistanceFromQueen = 100;
    private int marginFromEdge = 30;
    
    // Animation parameters
    private long animationStartTime;
    private boolean animationEnabled = true;
    
    public AntRenderer() {
        ants = new ArrayList<>();
        antImages = new ArrayList<>();
        antWeights = new ArrayList<>();
        random = new Random();
        loadAntImages();
        animationStartTime = System.currentTimeMillis();
    }
    
    // ==================== IMAGE LOADING ====================
    
    private void loadAntImages() {
        String[] antFileNames = { "ant1.png", "ant2.png" , "snail.png"};
        double[] defaultWeights = { 0.5, 0.2 ,0.01};  // Equal weights by default
        
        for (int i = 0; i < antFileNames.length; i++) {
            BufferedImage img = loadImage(antFileNames[i]);
            if (img != null) {
                antImages.add(img);
                antWeights.add(defaultWeights[i]);
            }
        }
        
        // Normalize weights to sum to 1.0
        normalizeWeights();
        
        if (antImages.isEmpty()) {
            System.err.println("WARNING: No ant images loaded - ants will not be rendered");
        }
    }
    
    /**
     * Gets a random ant image using weighted probabilities.
     * Each ant type has a different likelihood of being selected.
     */
    private BufferedImage getRandomAntImage() {
        if (antImages.isEmpty()) {
            return null;
        }
        
        // Use weighted selection if we have weights
        if (!antWeights.isEmpty() && antWeights.size() == antImages.size()) {
            return getRandomAntImageWithWeights();
        }
        
        // Fallback to uniform random selection
        return antImages.get(random.nextInt(antImages.size()));
    }
    
    /**
     * Selects a random ant image based on weighted probabilities.
     * Uses cumulative probability distribution for selection.
     */
    private BufferedImage getRandomAntImageWithWeights() {
        double rand = random.nextDouble();  // [0.0, 1.0)
        double cumulative = 0.0;
        
        for (int i = 0; i < antWeights.size(); i++) {
            cumulative += antWeights.get(i);
            if (rand < cumulative) {
                return antImages.get(i);
            }
        }
        
        // Fallback (shouldn't reach here if weights are properly normalized)
        return antImages.get(antImages.size() - 1);
    }
    
    /**
     * Normalizes weights so they sum to 1.0
     */
    private void normalizeWeights() {
        if (antWeights.isEmpty()) {
            return;
        }
        
        double sum = antWeights.stream().mapToDouble(Double::doubleValue).sum();
        if (sum > 0) {
            for (int i = 0; i < antWeights.size(); i++) {
                antWeights.set(i, antWeights.get(i) / sum);
            }
        }
    }
    
    /**
     * Sets custom weights for ant images.
     * Weights don't need to sum to 1.0 - they will be normalized automatically.
     * Example: setAntWeights(Arrays.asList(70.0, 30.0)) for 70% ant1, 30% ant2
     */
    public void setAntWeights(List<Double> weights) {
        if (weights.size() != antImages.size()) {
            throw new IllegalArgumentException(
                "Number of weights (" + weights.size() + ") must match number of ant images (" + antImages.size() + ")"
            );
        }
        this.antWeights = new ArrayList<>(weights);
        normalizeWeights();
    }
    
    /**
     * Gets the current weights for ant images (normalized to sum to 1.0)
     */
    public List<Double> getAntWeights() {
        return new ArrayList<>(antWeights);
    }
    
    private BufferedImage loadImage(String imageName) {
        String[] paths = {
            "PopulationDynamicsSimulator/src/main/res/" + imageName,
            "src/main/res/" + imageName,
            "res/" + imageName,
            "../res/" + imageName,
            "../../res/" + imageName,
            imageName
        };
        
        for (String path : paths) {
            try {
                File file = new File(path);
                if (file.exists() && file.canRead()) {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        System.out.println("Loaded " + imageName + " from: " + file.getAbsolutePath());
                        return image;
                    }
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        System.err.println("Could not find " + imageName + " - ants will not be rendered");
        return null;
    }
    
    // ==================== POPULATION MANAGEMENT ====================
    
    public void updateAntPopulation(int targetPopulation, int canvasWidth, int canvasHeight, 
                                   int queenCenterX, int queenCenterY) {
        int currentCount = ants.size();
        
        if (targetPopulation > currentCount) {
            int toAdd = targetPopulation - currentCount;
            for (int i = 0; i < toAdd; i++) {
                AntPosition newAnt = generateRandomPosition(canvasWidth, canvasHeight, 
                                                           queenCenterX, queenCenterY);
                ants.add(newAnt);
            }
        } else if (targetPopulation < currentCount) {
            int toRemove = currentCount - targetPopulation;
            for (int i = 0; i < toRemove; i++) {
                if (!ants.isEmpty()) {
                    ants.remove(ants.size() - 1);
                }
            }
        }
    }
    
    public void repositionAllAnts(int canvasWidth, int canvasHeight, 
                                 int queenCenterX, int queenCenterY) {
        for (AntPosition ant : ants) {
            Point newPos = generateValidPosition(canvasWidth, canvasHeight, 
                                                queenCenterX, queenCenterY);
            ant.baseX = newPos.x;
            ant.baseY = newPos.y;
        }
    }
    
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
            int x = marginFromEdge + random.nextInt(Math.max(1, canvasWidth - 2 * marginFromEdge));
            int y = marginFromEdge + random.nextInt(Math.max(1, canvasHeight - 2 * marginFromEdge));
            
            double distanceFromQueen = Math.sqrt(
                Math.pow(x - queenCenterX, 2) + Math.pow(y - queenCenterY, 2)
            );
            
            if (distanceFromQueen >= minDistanceFromQueen) {
                return new Point(x, y);
            }
            
            attempt++;
        }
        
        return new Point(marginFromEdge, marginFromEdge);
    }
    
    // ==================== RENDERING ====================
    
    public void renderAnts(Graphics2D g2) {
        if (antImages.isEmpty() || ants.isEmpty()) {
            return;
        }
        
        double currentTime = (System.currentTimeMillis() - animationStartTime) / 1000.0;
        
        for (AntPosition ant : ants) {
            BufferedImage antImage = ant.getAntImage();
            if (antImage == null) {
                continue;
            }
            
            int scaledWidth = (int) (antImage.getWidth() * antScaleFactor);
            int scaledHeight = (int) (antImage.getHeight() * antScaleFactor);
            
            Point animatedPos = ant.getAnimatedPosition(currentTime);
            double animatedRotation = ant.getAnimatedRotation(currentTime);
            
            var oldTransform = g2.getTransform();
            
            g2.translate(animatedPos.x, animatedPos.y);
            g2.rotate(Math.toRadians(animatedRotation));
            
            g2.drawImage(antImage, -scaledWidth / 2, -scaledHeight / 2, 
                        scaledWidth, scaledHeight, null);
            
            g2.setTransform(oldTransform);
        }
    }
    
    // ==================== GETTERS/SETTERS ====================
    
    public int getAntCount() { return ants.size(); }
    public void clearAnts() { ants.clear(); }
    public void setAnimationEnabled(boolean enabled) { this.animationEnabled = enabled; }
    public void setAntScaleFactor(double scale) { this.antScaleFactor = scale; }
    public void setMinDistanceFromQueen(int distance) { this.minDistanceFromQueen = distance; }
    public void setMarginFromEdge(int margin) { this.marginFromEdge = margin; }
    
    // ==================== INNER CLASS: AntPosition ====================
    
    private class AntPosition {
        int baseX;
        int baseY;
        double rotation;
        BufferedImage antImage;
        
        AnimationType animType;
        double animSpeed;
        double animAmplitude;
        double phaseOffset;
        
        double circleRadius;
        double circleAngle;
        
        double[] patrolPointsX;
        double[] patrolPointsY;
        
        enum AnimationType {
            JIGGLE, CIRCLE, FIGURE8, PATROL, STATIC
        }
        
        AntPosition(int x, int y, double rotation, Random random) {
            this.baseX = x;
            this.baseY = y;
            this.rotation = rotation;
            this.antImage = getRandomAntImage();
            this.phaseOffset = random.nextDouble() * Math.PI * 2;
            
            double typeRoll = random.nextDouble();
            if (typeRoll < 0.25) {
                animType = AnimationType.JIGGLE;
                animSpeed = 1.0 + random.nextDouble() * 2.0;
                animAmplitude = 1.0 + random.nextDouble() * 2.0;
            } else if (typeRoll < 0.45) {
                animType = AnimationType.CIRCLE;
                animSpeed = 0.3 + random.nextDouble() * 0.7;
                circleRadius = 8.0 + random.nextDouble() * 20.0;
                circleAngle = random.nextDouble() * Math.PI * 2;
            } else if (typeRoll < 0.70) {
                animType = AnimationType.FIGURE8;
                animSpeed = 0.15 + random.nextDouble() * 0.4;
                animAmplitude = 15.0 + random.nextDouble() * 25.0;
            } else if (typeRoll < 0.85) {
                animType = AnimationType.PATROL;
                animSpeed = 0.2 + random.nextDouble() * 0.4;
                animAmplitude = 20.0 + random.nextDouble() * 30.0;
                patrolPointsX = new double[4];
                patrolPointsY = new double[4];
                for (int i = 0; i < 4; i++) {
                    patrolPointsX[i] = (random.nextDouble() - 0.5) * animAmplitude * 2;
                    patrolPointsY[i] = (random.nextDouble() - 0.5) * animAmplitude * 2;
                }
            } else {
                animType = AnimationType.STATIC;
            }
        }
        
        Point getAnimatedPosition(double time) {
            int x = baseX;
            int y = baseY;
            
            switch (animType) {
                case JIGGLE:
                    x += (int) (Math.sin(time * animSpeed * Math.PI * 2 + phaseOffset) * animAmplitude);
                    y += (int) (Math.cos(time * animSpeed * Math.PI * 2 + phaseOffset * 1.3) * animAmplitude);
                    break;
                    
                case CIRCLE:
                    double angle = time * animSpeed * Math.PI * 2 + circleAngle;
                    x += (int) (Math.cos(angle) * circleRadius);
                    y += (int) (Math.sin(angle) * circleRadius);
                    break;
                    
                case FIGURE8:
                    double t = time * animSpeed * Math.PI * 2 + phaseOffset;
                    x += (int) (Math.sin(t) * animAmplitude);
                    y += (int) (Math.sin(t * 2) * animAmplitude * 0.5);
                    break;
                    
                case PATROL:
                    double patrolT = (time * animSpeed) % 1.0;
                    int numPoints = patrolPointsX.length;
                    
                    double segmentLength = 1.0 / numPoints;
                    int segment = (int) (patrolT / segmentLength);
                    double localT = (patrolT % segmentLength) / segmentLength;
                    
                    int p0 = (segment - 1 + numPoints) % numPoints;
                    int p1 = segment;
                    int p2 = (segment + 1) % numPoints;
                    int p3 = (segment + 2) % numPoints;
                    
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
                    break;
            }
            
            return new Point(x, y);
        }
        
        double getAnimatedRotation(double time) {
            double rot = rotation;
            
            if (animType == AnimationType.CIRCLE) {
                rot += Math.sin(time * animSpeed * Math.PI * 4) * 5;
            } else if (animType == AnimationType.FIGURE8) {
                rot += Math.sin(time * animSpeed * Math.PI * 3) * 8;
            } else if (animType == AnimationType.JIGGLE) {
                rot += Math.sin(time * animSpeed * Math.PI * 6 + phaseOffset * 2) * 3;
            } else if (animType == AnimationType.PATROL) {
                double patrolT = (time * animSpeed) % 1.0;
                rot += Math.sin(patrolT * Math.PI * 8) * 10;
            }
            
            return rot;
        }
        
        BufferedImage getAntImage() {
            return antImage;
        }
    }
}
