package PopulationDynamicsSimulator.src.main.java;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Core game logic for the Ant Colony simulation using the Allee effect model.
 */
public class AntColonyGame {
    // Population model parameters
    private double x;  // Current population (normalized)
    private double r;  // Growth rate
    private double K;  // Carrying capacity
    private double A;  // Allee threshold
    
    // Button effect parameters
    private double layMoreEggsRMultiplier = 1.20;
    private double layLessEggsRMultiplier = 0.75;
    private double buildChambersKMultiplier = 1.5;
    private double buildChambersAMultiplier = 0.80;
    private double destroyChambersKMultiplier = 0.70;
    private double destroyChambersAMultiplier = 1.15;

    // Initial values
    private double x_initial = 5.0;
    private double r_initial = 0.05;
    private double K_initial = 25.0;
    private double A_initial = 1.0;
    
    // History tracking
    private List<Double> populationHistory;
    private int currentDay;
    
    // Random event generator
    private RandomEvent randomEvent;
    
    // GUI reference
    private GameGUI gui;
    
    public AntColonyGame() {
        x = x_initial;
        r = r_initial;
        K = K_initial;
        A = A_initial;
        populationHistory = new ArrayList<>();
        populationHistory.add(x);
        currentDay = 0;
        randomEvent = new RandomEvent();
    }
    
    public void setGUI(GameGUI gui) {
        this.gui = gui;
        updateAllGraphs();
    }
    
    // Getters
    public double getPopulation() { return x; }
    public double getR() { return r; }
    public double getK() { return K; }
    public double getA() { return A; }
    public int getCurrentDay() { return currentDay; }
    public double getLayMoreEggsRMultiplier() { return layMoreEggsRMultiplier; }
    public double getLayLessEggsRMultiplier() { return layLessEggsRMultiplier; }
    public double getBuildChambersKMultiplier() { return buildChambersKMultiplier; }
    public double getBuildChambersAMultiplier() { return buildChambersAMultiplier; }
    public double getDestroyChambersKMultiplier() { return destroyChambersKMultiplier; }
    public double getDestroyChambersAMultiplier() { return destroyChambersAMultiplier; }
    
    // Setters for technology effects
    public void setR(double newR) { 
        this.r = newR;
        if (this.r < 0) this.r = 0;
    }
    public void setX(double newX) { 
        this.x = newX;
        if (this.x < 0) this.x = 0;
    }
    public void setA(double newA) { 
        this.A = newA;
        if (this.A < 0) this.A = 0;
    }
    
    /**
     * Deducts technology points by advancing the day counter.
     * Since tech points = currentDay / 10, deducting 1 point means advancing 10 days.
     */
    public boolean deductTechnologyPoints(int points) {
        int pointsNeeded = points * 10;
        if (getTechnologyPoints() >= points) {
            currentDay += pointsNeeded;
            return true;
        }
        return false;
    }
    
    /**
     * Calculates technology points earned: 1 point per 10 days
     */
    public int getTechnologyPoints() {
        return currentDay / 10;
    }
    
    /**
     * The Allee effect population model.
     */
    private double populationModel(double xn, double rVal, double KVal, double AVal) {
        return xn + rVal * xn * (1 - xn / KVal) * (xn / AVal - 1);
    }
    
    public void newDay() {
        RandomEvent.Event event = randomEvent.generateEvent();
        
        r *= event.rMultiplier;
        K *= event.kMultiplier;
        A *= event.aMultiplier;
        x *= event.populationMultiplier;
        
        double newX = populationModel(x, r, K, A);
        if (newX < 0) newX = 0;
        
        x = newX;
        currentDay++;
        populationHistory.add(x);
        
        updateStatus();
        updateAllGraphs();
        
        if (gui != null) {
            gui.addNewsMessage(event.message);
        }
        
        if (x < 0.1) {
            JOptionPane.showMessageDialog(gui, 
                "Your colony has gone extinct! Game Over.", 
                "Extinction", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void layMoreEggs() {
        r *= layMoreEggsRMultiplier;
        updateStatus();
        updateAllGraphs();
    }
    
    public void layLessEggs() {
        r *= layLessEggsRMultiplier;
        updateStatus();
        updateAllGraphs();
    }
    
    public void buildChambers() {
        K *= buildChambersKMultiplier;
        A *= buildChambersAMultiplier;
        updateStatus();
        updateAllGraphs();
    }
    
    public void destroyChambers() {
        K *= destroyChambersKMultiplier;
        A *= destroyChambersAMultiplier;
        updateStatus();
        updateAllGraphs();
    }
    
    public void resetGame() {
        x = x_initial;
        r = r_initial;
        K = K_initial;
        A = A_initial;
        populationHistory.clear();
        populationHistory.add(x);
        currentDay = 0;
        randomEvent = new RandomEvent();
        updateStatus();
        updateAllGraphs();
    }
    
    private void updateStatus() {
        if (gui != null) {
            gui.updateStatus();
        }
    }
    
    private void updateAllGraphs() {
        if (gui != null) {
            updateHistoryGraph();
            updateProjectionGraph();
            updateLyapunovGraph();
            updateBifurcationGraph();
        }
    }
    
    private void updateHistoryGraph() {
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < populationHistory.size(); i++) {
            points.add(new Point2D(i, populationHistory.get(i)));
        }
        gui.updateHistoryGraph(points);
    }
    
    private void updateProjectionGraph() {
        List<Point2D> points = new ArrayList<>();
        double projX = x;
        
        points.add(new Point2D(currentDay, projX));
        
        for (int i = 1; i < 30; i++) {
            projX = populationModel(projX, r, K, A);
            if (projX < 0) projX = 0;
            points.add(new Point2D(currentDay + i, projX));
        }
        gui.updateProjectionGraph(points);
    }
    
    private void updateLyapunovGraph() {
        List<Point2D> points = new ArrayList<>();
        int warmup = 500;
        int samples = 500;
        
        for (double testR = 0.01; testR <= 3.0; testR += 0.02) {
            double x = K / 2.0;  // Start at middle of carrying capacity
            
            // Warmup: let the system settle onto its attractor
            for (int i = 0; i < warmup; i++) {
                x = populationModel(x, testR, K, A);
                if (x < 1e-10) {
                    x = 1e-10;  // Prevent complete extinction for calculation
                }
                if (x > K * 10) {
                    x = K * 10;  // Prevent blowup
                }
            }
            
            // Calculate Lyapunov exponent using derivative method
            // λ = (1/n) * Σ ln|f'(x_i)|
            double lyapunov = 0;
            int validSamples = 0;
            
            for (int i = 0; i < samples; i++) {
                // Calculate the derivative of the map at current point
                double derivative = populationModelDerivative(x, testR, K, A);
                
                if (Math.abs(derivative) > 1e-15) {
                    lyapunov += Math.log(Math.abs(derivative));
                    validSamples++;
                }
                
                // Iterate the map
                x = populationModel(x, testR, K, A);
                if (x < 1e-10) {
                    x = 1e-10;
                }
                if (x > K * 10) {
                    x = K * 10;
                }
            }
            
            if (validSamples > 0) {
                lyapunov /= validSamples;
            }
            
            points.add(new Point2D(testR, lyapunov));
        }
        gui.updateLyapunovGraph(points);
    }
    
    /**
     * Derivative of the Allee effect population model with respect to x.
     * f(x) = x + r * x * (1 - x/K) * (x/A - 1)
     * f'(x) = 1 + r * [(1 - x/K)(x/A - 1) + x * (-1/K)(x/A - 1) + x * (1 - x/K)(1/A)]
     *       = 1 + r * [(1 - x/K)(x/A - 1) - (x/K)(x/A - 1) + (x/A)(1 - x/K)]
     *       = 1 + r * [(x/A - 1)(1 - 2x/K) + (x/A)(1 - x/K)]
     */
    private double populationModelDerivative(double x, double rVal, double KVal, double AVal) {
        double term1 = (x / AVal - 1) * (1 - 2 * x / KVal);
        double term2 = (x / AVal) * (1 - x / KVal);
        return 1 + rVal * (term1 + term2);
    }
    
    private void updateBifurcationGraph() {
        List<Point2D> points = new ArrayList<>();
        int warmup = 500;
        int samples = 100;
        
        // Use finer stepping for smoother bifurcation diagram
        for (double testR = 0.001; testR <= 3.0; testR += 0.005) {
            double testX = 50.0;
            
            for (int i = 0; i < warmup; i++) {
                testX = populationModel(testX, testR, K, A);
                if (testX < 0) testX = 0;
            }
            
            for (int i = 0; i < samples; i++) {
                testX = populationModel(testX, testR, K, A);
                if (testX < 0) testX = 0;
                points.add(new Point2D(testR, testX));
            }
        }
        gui.updateBifurcationGraph(points);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AntColonyGame game = new AntColonyGame();
            GameGUI gui = new GameGUI(game);
            game.setGUI(gui);
            gui.setVisible(true);
        });
    }
    
    // ==================== INNER CLASS: Point2D ====================
    
    public static class Point2D {
        public double x, y;
        
        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}