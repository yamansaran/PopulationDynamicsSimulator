package PopulationDynamicsSimulator.src.main.java;
/**
 * Simple 2D point class for graph data.
 */
public class Point2D {
    public double x;
    public double y;
    
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return String.format("Point2D(%.2f, %.2f)", x, y);
    }
}
