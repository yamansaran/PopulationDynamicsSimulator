package PopulationDynamicsSimulator.src.main.java;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AntColonyGame {
    // Population model parameters
    private double x;  // Current population (normalized)
    private double r;  // Growth rate
    private double K;  // Carrying capacity
    private double A;  // Allee threshold
    
    // Button effect parameters (modifiable for future functionality)
    private double layMoreEggsRMultiplier = 1.20;      // +20% r
    private double layLessEggsRMultiplier = 0.75;      // -25% r
    private double buildChambersKMultiplier = 1.5;    // +15% K
    private double buildChambersAMultiplier = 0.80;    // -20% A
    private double destroyChambersKMultiplier = 0.70;  // -30% K
    private double destroyChambersAMultiplier = 1.15;  // +15% A

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
        // Initialize parameters
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
    
    // Getters for GUI
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
    
    private double populationModel(double xn, double rVal, double KVal, double AVal) {
        return xn + rVal * xn * (1 - xn / KVal) * (xn / AVal - 1);
    }
    
    public void newDay() {
        // Generate random event
        RandomEvent.Event event = randomEvent.generateEvent();
        
        // Apply event effects to parameters
        r *= event.rMultiplier;
        K *= event.kMultiplier;
        A *= event.aMultiplier;
        x *= event.populationMultiplier; // Apply direct population change
        
        // Calculate new population
        double newX = populationModel(x, r, K, A);
        
        // Ensure population doesn't go negative
        if (newX < 0) newX = 0;
        
        x = newX;
        currentDay++;
        populationHistory.add(x);
        
        updateStatus();
        updateAllGraphs();
        
        // Send event message to news bar
        if (gui != null) {
            gui.addNewsMessage(event.message);
        }
        
        // Check for extinction
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
        
        // Add current position as first point (will be marked specially)
        points.add(new Point2D(currentDay, projX));
        
        // Project future - reduced to 30 days for better short-term view
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
        int samples = 200;
        double epsilon = 1e-8;
        
        // Test different r values
        for (double testR = 0.01; testR <= 3.0; testR += 0.02) {
            double x1 = 50.0;
            double x2 = 50.0 + epsilon;
            
            // Warmup
            for (int i = 0; i < warmup; i++) {
                x1 = populationModel(x1, testR, K, A);
                x2 = populationModel(x2, testR, K, A);
                if (x1 < 0) x1 = 0;
                if (x2 < 0) x2 = 0;
            }
            
            // Calculate Lyapunov exponent
            double lyapunov = 0;
            for (int i = 0; i < samples; i++) {
                x1 = populationModel(x1, testR, K, A);
                x2 = populationModel(x2, testR, K, A);
                if (x1 < 0) x1 = 0;
                if (x2 < 0) x2 = 0;
                
                double distance = Math.abs(x2 - x1);
                if (distance > 1e-10) {
                    lyapunov += Math.log(distance / epsilon);
                    // Renormalize
                    x2 = x1 + epsilon;
                }
            }
            lyapunov /= samples;
            
            points.add(new Point2D(testR, lyapunov));
        }
        gui.updateLyapunovGraph(points);
    }
    
    private void updateBifurcationGraph() {
        List<Point2D> points = new ArrayList<>();
        int warmup = 500;
        int samples = 100;
        
        // Test different r values
        for (double testR = 0.01; testR <= 3.0; testR += 0.01) {
            double testX = 50.0;
            
            // Warmup period
            for (int i = 0; i < warmup; i++) {
                testX = populationModel(testX, testR, K, A);
                if (testX < 0) testX = 0;
            }
            
            // Sample points
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
    
    // Helper class for 2D points - made public so GUI can access it
    public static class Point2D {
        public double x, y;
        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}