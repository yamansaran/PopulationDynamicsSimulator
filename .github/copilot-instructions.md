# Copilot Instructions - Population Dynamics Simulator

## Project Overview
**Ant Colony Manager** is an educational game simulating population dynamics using the **Allee effect model**. The application demonstrates how species with weak individuals (below critical threshold A) face reduced reproduction rates, leading to interesting bifurcation behaviors.

- **Architecture**: MVC-like pattern with `AntColonyGame` (model), `GameGUI` (view/controller), and specialized UI panels
- **Technology**: Java 21, Swing/AWT for GUI, Maven for build
- **Main package**: `PopulationDynamicsSimulator.src.main.java` (note: unconventional nested structure)

## Architecture Essentials

### Core Components

**AntColonyGame** (`AntColonyGame.java`)
- Implements the Allee effect population model: `x(n+1) = x(n) + r·x(n)·(1 - x(n)/K)·(x(n)/A - 1)`
- Parameters: `r` (growth rate), `K` (carrying capacity), `A` (Allee threshold)
- Tracks history and applies random events each day
- Methods like `layMoreEggs()`, `buildChambers()` modify model parameters with configurable multipliers

**GameGUI** (`GameGUI.java`)
- Main Swing JFrame managing layout and control flow
- Layout: Left panel (status/news/controls) + Center (main display) + Right (4 graphs)
- Delegates animation to `MainDisplayPanel` via 33ms Timer (30 FPS)
- **Critical**: Calls `game.setGUI(this)` during initialization to establish bidirectional reference

**Graph Visualization**
- `GraphPanel`: Mini graphs with click-to-zoom (opens `ZoomableGraphPanel` dialog)
- Four graphs: History (blue), Projection (green), Lyapunov (red), Bifurcation (magenta)
- Data passed as `List<Point2D>` from game logic
- Zoomable variant supports dragging and scrolling

**Asset & Resource Management**
- `ImageLoader.java`: Tries multiple BASE_PATHS before falling back to text/symbols
- Loads images like `queen.png`, `ant1.png`, button PNGs from `res/` directory
- Graceful degradation: displays emoji/symbols if images missing
- Used by: `AntRenderer` (worker ants), `MainDisplayPanel` (queen), button creation

### Data Flow
1. User clicks button in `buildControlPanel()` → `AntColonyGame` method (e.g., `layMoreEggs()`)
2. Game updates model parameters and calculates new population via `newDay()`
3. Game calls `gui.updateStatus()`, `gui.addNewsMessage()`, `gui.updateHistoryGraph()` etc.
4. GUI updates labels, text areas, and triggers graph repaints
5. `MainDisplayPanel` repaints ants based on population count via `updateAnts()`

## Project Conventions

### Styling & Constants
- **Fonts**: All defined in `GameFonts.java` (Comic Sans MS for game charm)
- **Colors**: Use constants where applicable; primary: `BACKGROUND_COLOR = 0xEB9B6E` (coral-peach)
- **Layouts**: BorderLayout for main structure, GridLayout for button panels, FlowLayout for scroll buttons

### Package Structure (Non-Standard)
```
src/main/java/PopulationDynamicsSimulator/src/main/java/
├── AntColonyGame.java
├── GameGUI.java
├── GraphPanel.java
├── MainDisplayPanel.java
├── AntRenderer.java
├── ImageLoader.java
├── GameFonts.java
├── Point2D.java (simple data class)
├── RandomEvent.java
└── [UI Panels]: ActionsPanel, ColonyStatusPanel, NewsPanel, etc.
```
Files follow naming: `[Component]Panel.java` or `[Function].java`

### RandomEvent System
- Weighted event pool with multipliers for `r`, `K`, `A`, and population
- Events triggered each `newDay()` call
- Seed-based constructor for reproducible simulations: `RandomEvent(long seed)`

## Critical Developer Workflows

### Build & Run
```powershell
# Maven clean build
mvn clean compile

# Run the application (Java 21 required)
mvn exec:java -Dexec.mainClass="PopulationDynamicsSimulator.src.main.java.App"

# Package JAR
mvn package
```

### Testing Graph Updates
- Manually verify via GUI or add print statements to `AntColonyGame.updateAllGraphs()`
- Graph data passed as `List<Point2D>` to `updateHistoryGraph()`, `updateProjectionGraph()`, etc.
- Click mini-graphs to open zoomable dialog for detailed inspection

### Image Loading Debugging
- Check console output: `ImageLoader` logs successful loads
- Fallback paths searched: `PopulationDynamicsSimulator/src/main/res/`, `src/main/res/`, `res/`, etc.
- Missing images default to emoji/text symbols in buttons

## Integration Points & Patterns

### Game-GUI Coupling
- `AntColonyGame.setGUI(GameGUI gui)` establishes reference (call in App.main() before showing frame)
- GUI never updates game directly; all input flows through `game.method()` calls
- Game calls `gui.update*()` methods (unidirectional from model to view)

### Swing Animation Loop
- `MainDisplayPanel` repaints via 33ms Timer in `GameGUI.startAnimation()`
- `AntRenderer.updateAntPopulation()` called from `updateStatus()` to sync visual ants with population
- Ant rendering is stateful; maintains `List<AntPosition>` that persists across repaints

### Button Multipliers Pattern
- Each action button has associated multiplier fields: `layMoreEggsRMultiplier = 1.20`
- Game displays multiplier % in button text: `"(+20% r)"`
- Button action retrieves current multiplier via getter for dynamic UI updates

## Common Modifications

**Adding a New Game Action**
1. Add multiplier field(s) to `AntColonyGame` with getter
2. Create button in `buildControlPanel()` with multiplier text
3. Implement handler method in `AntColonyGame` that applies multipliers and calls `updateAllGraphs()`
4. Attach listener: `button.addActionListener(e -> game.actionName());`

**Adding a New Graph**
1. Create `GraphPanel` in `buildCenterPanel()` grid
2. Add update method in `GameGUI`: `public void updateNewGraph(List<Point2D> data) { graphPanel.setData(data); }`
3. Call from `AntColonyGame.updateAllGraphs()`: `gui.updateNewGraph(data);`

**Modifying Population Model**
- Update formula in `AntColonyGame.populationModel()` (currently Allee effect)
- All subsequent calls use new model; no other changes needed if interface matches

## Known Issues & Quirks
- Nested package path `PopulationDynamicsSimulator/src/main/java` is unconventional; may require Maven configuration adjustments if building differs
- Image loading relies on relative paths; JAR deployment may need resource bundling changes
- News bar text prepends new messages (prepend logic in `addNewsMessage()`); oldest messages eventually lost

---
**Last Updated**: December 2025  
**Java Version**: 21  
**Build Tool**: Maven
