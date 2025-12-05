package PopulationDynamicsSimulator.src.main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random events that affect the colony.
 */
public class RandomEvent {
    private Random random;
    private List<WeightedEvent> eventPool;
    
    // ==================== INNER CLASS: Event ====================
    
    public static class Event {
        public String message;
        public double rMultiplier;
        public double kMultiplier;
        public double aMultiplier;
        public double populationMultiplier;
        
        public Event(String message, double rMult, double kMult, double aMult) {
            this(message, rMult, kMult, aMult, 1.0);
        }
        
        public Event(String message, double rMult, double kMult, double aMult, double popMult) {
            this.message = message;
            this.rMultiplier = rMult;
            this.kMultiplier = kMult;
            this.aMultiplier = aMult;
            this.populationMultiplier = popMult;
        }
    }
    
    // ==================== INNER CLASS: WeightedEvent ====================
    
    private static class WeightedEvent {
        Event event;
        double weight;
        
        WeightedEvent(Event event, double weight) {
            this.event = event;
            this.weight = weight;
        }
    }
    
    // ==================== CONSTRUCTORS ====================
    
    public RandomEvent() {
        random = new Random();
        initializeEventPool();
    }
    
    public RandomEvent(long seed) {
        random = new Random(seed);
        initializeEventPool();
    }
    
    // ==================== EVENT POOL INITIALIZATION ====================
    
    private void initializeEventPool() {
        eventPool = new ArrayList<>();
        
        // 75% chance of no event
        eventPool.add(new WeightedEvent(
            new Event("☀️ It's a beautiful day today!", 1.0, 1.0, 1.0),
            75.0
        ));
        
        // Common events (weight 3 each)
        eventPool.add(new WeightedEvent(
            new Event("🌧️ Light rainfall. The colony found some food.\n+5% growth rate", 1.05, 1.0, 1.0),
            3.0
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🌱 New foraging trail discovered!\n+8% growth rate, +5% carrying capacity", 1.08, 1.05, 1.0),
            3.0
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🌸 Mild spring weather. Good conditions.\n+10% growth rate", 1.10, 1.0, 1.0),
            3.0
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🌿 Perfect weather conditions!\n+8% growth rate, +5% carrying capacity, -8% Allee threshold", 1.08, 1.05, 0.92),
            3.0
        ));
        
        // Uncommon events (weight 1.5 each)
        eventPool.add(new WeightedEvent(
            new Event("☀️ Hot day. Workers are less active.\n-10% growth rate", 0.90, 1.0, 1.0),
            1.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🍄 Discovered a small fungus patch!\n+12% carrying capacity", 1.0, 1.12, 1.0),
            1.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🦗 Insects nearby! Easy prey for workers.\n+12% growth rate, +8% carrying capacity", 1.12, 1.08, 1.0),
            1.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🏗️ Workers expanded some chambers.\n+10% carrying capacity, -8% Allee threshold", 1.0, 1.10, 0.92),
            1.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🔬 Workers developed better coordination!\n-12% Allee threshold", 1.0, 1.0, 0.88),
            1.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🌪️ Windstorm disrupted foraging.\n-12% growth rate", 0.88, 1.0, 1.0),
            1.5
        ));
        
        // Rare events (weight 0.5 each)
        eventPool.add(new WeightedEvent(
            new Event("🦎 Predator spotted near colony!\n-10% carrying capacity, +8% Allee threshold", 1.0, 0.90, 1.08),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("☀️ Drought conditions. Food scarce.\n-18% growth rate", 0.82, 1.0, 1.0),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("❄️ Cold snap! Workers sluggish.\n-15% growth rate", 0.85, 1.0, 1.0),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🐜 Rival colony detected nearby.\n-12% carrying capacity, +10% Allee threshold", 1.0, 0.88, 1.10),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🏚️ Found abandoned nest! Free chambers.\n+20% carrying capacity, -12% Allee threshold", 1.0, 1.20, 0.88),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🍯 Found a rich sugar source!\n+15% growth rate, +10% carrying capacity, -8% Allee threshold", 1.15, 1.10, 0.92),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("👑 Queen produced extra eggs!\n+25% growth rate", 1.25, 1.0, 1.0),
            0.5
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🌧️ Heavy rainfall! Abundant food.\n+18% growth rate, +10% carrying capacity", 1.18, 1.10, 1.0),
            0.5
        ));
        
        // Very rare events (weight 0.2 each)
        eventPool.add(new WeightedEvent(
            new Event("🦠 Disease outbreak! Colony health compromised.\n-25% growth rate, +15% Allee threshold", 0.75, 1.0, 1.15),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("☠️ DEVASTATING PLAGUE! Half the colony has perished!\nPopulation reduced by 50%, -20% growth rate", 0.80, 1.0, 1.0, 0.5),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🏗️ Tunnel collapse! Lost chambers.\n-22% carrying capacity, +10% Allee threshold", 1.0, 0.78, 1.10),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🦂 Scorpion nest discovered nearby!\n-20% growth rate, +18% Allee threshold", 0.80, 1.0, 1.18),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("⚡ Lightning strike! Workers scattered.\n-28% growth rate, +12% Allee threshold", 0.72, 1.0, 1.12),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("🪨 Discovered deep caverns! Major expansion.\n+28% carrying capacity, -20% Allee threshold", 1.0, 1.28, 0.80),
            0.2
        ));
        
        eventPool.add(new WeightedEvent(
            new Event("☠️ PLAGUE! Devastating disease sweeps through colony!\nHalf the population perished. -20% growth rate, +20% Allee threshold", 0.80, 1.0, 1.20, 0.5),
            0.2
        ));
    }
    
    // ==================== PUBLIC METHODS ====================
    
    /**
     * Generates a random event based on probability distribution.
     */
    public Event generateEvent() {
        double totalWeight = 0;
        for (WeightedEvent we : eventPool) {
            totalWeight += we.weight;
        }
        
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0;
        
        for (WeightedEvent we : eventPool) {
            cumulativeWeight += we.weight;
            if (randomValue <= cumulativeWeight) {
                return we.event;
            }
        }
        
        return eventPool.get(0).event;
    }
}
