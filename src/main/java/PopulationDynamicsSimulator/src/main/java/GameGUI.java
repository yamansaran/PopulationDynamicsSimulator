package PopulationDynamicsSimulator.src.main.java;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class GameGUI extends JFrame {
    private JLabel statusLabel;
    private JLabel paramLabel;
    private JTextArea newsBar;
    private GraphPanel historyPanel;
    private GraphPanel projectionPanel;
    private GraphPanel lyapunovPanel;
    private GraphPanel bifurcationPanel;
    private MainDisplayPanel mainDisplayPanel;
    
    private AntColonyGame game;
    
    // Animation timer
    private Timer animationTimer;
    
    // Jungle-themed font - Comic Sans MS for fun, playful feel
    // You can also try: "Papyrus", "Brush Script MT", "Chalkboard", "Bradley Hand"
    private static final Font JUNGLE_FONT_TITLE = new Font("Comic Sans MS", Font.BOLD, 18);
    private static final Font JUNGLE_FONT_LARGE = new Font("Comic Sans MS", Font.BOLD, 16);
    private static final Font JUNGLE_FONT_MEDIUM = new Font("Comic Sans MS", Font.BOLD, 14);
    private static final Font JUNGLE_FONT_SMALL = new Font("Comic Sans MS", Font.PLAIN, 12);
    private static final Font JUNGLE_FONT_TINY = new Font("Comic Sans MS", Font.PLAIN, 10);
    private static final Font JUNGLE_FONT_BUTTON = new Font("Comic Sans MS", Font.BOLD, 12);
    
    public GameGUI(AntColonyGame game) {
        this.game = game;
        setupUI();
        startAnimation();
    }
    
    private void startAnimation() {
        // Update animation at ~30 FPS for smooth motion
        animationTimer = new Timer(33, e -> {
            if (mainDisplayPanel != null) {
                mainDisplayPanel.repaint();
            }
        });
        animationTimer.start();
    }
    
    private void setupUI() {
        setTitle("Ant Colony Manager - Allee Effect Model");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false); // Fixed resolution
        
        // Set background color for the main content pane
        getContentPane().setBackground(new Color(0xEB9B6E)); // #EB9B6E
        
        // Add padding/margins around the entire content
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left panel - News bar with status
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false); // Transparent to show background color
        
        // Status info at top of left panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Colony Status",
                0, 0, JUNGLE_FONT_MEDIUM
            )
        ));
        statusLabel = new JLabel(String.format("<html><b>Day %d</b><br>Population: %.2f ants</html>", 
            game.getCurrentDay(), game.getPopulation()));
        statusLabel.setFont(JUNGLE_FONT_MEDIUM);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        paramLabel = new JLabel(String.format("<html>r=%.3f<br>K=%.2f<br>A=%.2f</html>", 
            game.getR(), game.getK(), game.getA()));
        paramLabel.setFont(JUNGLE_FONT_SMALL);
        paramLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        statusPanel.add(statusLabel);
        statusPanel.add(paramLabel);
        
        // News bar - custom panel with background image
        NewsPanel newsPanel = new NewsPanel();
        newsPanel.setBackground(new Color(0xEB9B6E)); // Set background color for transparency
        // Remove the border completely - no gray line
        newsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        newsBar = newsPanel.getTextArea();
        newsBar.setEditable(false);
        newsBar.setLineWrap(true);
        newsBar.setWrapStyleWord(true);
        newsBar.setFont(JUNGLE_FONT_SMALL);
        newsBar.setText("Welcome to Ant Colony Manager!\n\nPress 'New Day' to begin...");
        newsBar.setOpaque(false); // Make text area transparent so background shows
        newsBar.setForeground(Color.BLACK);
        
        // Wrap in scroll pane but hide the scrollbar
        JScrollPane newsScrollPane = new JScrollPane(newsBar);
        newsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        newsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newsScrollPane.setOpaque(false);
        newsScrollPane.getViewport().setOpaque(false);
        newsScrollPane.setBorder(BorderFactory.createEmptyBorder(36, 40, 11, 30)); // top, left, bottom, right
        
        newsPanel.add(newsScrollPane, BorderLayout.CENTER);
        
        // Create scroll buttons panel at bottom with image buttons
        JPanel scrollButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        scrollButtonPanel.setOpaque(false);
        scrollButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 45, 0)); // 45px from bottom
        
        // Load button images
        JButton scrollUpButton = createImageButton("upbutton.png", "▲");
        scrollUpButton.setPreferredSize(new Dimension(67, 44));
        scrollUpButton.addActionListener(e -> {
            // Scroll up in the scroll pane
            JScrollBar verticalBar = newsScrollPane.getVerticalScrollBar();
            int value = verticalBar.getValue();
            verticalBar.setValue(value - 30); // Scroll up by 30 pixels
        });
        
        JButton scrollDownButton = createImageButton("downbutton.png", "▼");
        scrollDownButton.setPreferredSize(new Dimension(67, 44));
        scrollDownButton.addActionListener(e -> {
            // Scroll down in the scroll pane
            JScrollBar verticalBar = newsScrollPane.getVerticalScrollBar();
            int value = verticalBar.getValue();
            verticalBar.setValue(value + 30); // Scroll down by 30 pixels
        });
        
        scrollButtonPanel.add(scrollUpButton);
        scrollButtonPanel.add(scrollDownButton);
        
        newsPanel.add(scrollButtonPanel, BorderLayout.SOUTH);
        
        leftPanel.add(statusPanel, BorderLayout.NORTH);
        leftPanel.add(newsPanel, BorderLayout.CENTER);
        
        // Control buttons at bottom of left panel
        JPanel controlPanel = new JPanel(new GridLayout(6, 1, 5, 8)); // Increased vertical gap
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Actions",
                0, 0, JUNGLE_FONT_MEDIUM
            )
        ));
        controlPanel.setBackground(new Color(0xEB9B6E)); // Match background color
        
        JButton newDayButton = new JButton("New Day");
        newDayButton.setFont(JUNGLE_FONT_BUTTON);
        newDayButton.setBackground(new Color(100, 200, 100));
        newDayButton.addActionListener(e -> game.newDay());
        
        JButton layMoreEggsButton = new JButton(String.format("<html><center>Lay More Eggs<br>(%+.0f%% r)</center></html>", 
            (game.getLayMoreEggsRMultiplier() - 1) * 100));
        layMoreEggsButton.setFont(JUNGLE_FONT_TINY);
        layMoreEggsButton.addActionListener(e -> game.layMoreEggs());
        
        JButton layLessEggsButton = new JButton(String.format("<html><center>Lay Less Eggs<br>(%+.0f%% r)</center></html>", 
            (game.getLayLessEggsRMultiplier() - 1) * 100));
        layLessEggsButton.setFont(JUNGLE_FONT_TINY);
        layLessEggsButton.addActionListener(e -> game.layLessEggs());
        
        JButton buildChambersButton = new JButton(String.format("<html><center>Build Chambers<br>(%+.0f%% K, %+.0f%% A)</center></html>", 
            (game.getBuildChambersKMultiplier() - 1) * 100, (game.getBuildChambersAMultiplier() - 1) * 100));
        buildChambersButton.setFont(JUNGLE_FONT_TINY);
        buildChambersButton.addActionListener(e -> game.buildChambers());
        
        JButton destroyChambersButton = new JButton(String.format("<html><center>Destroy Chambers<br>(%+.0f%% K, %+.0f%% A)</center></html>", 
            (game.getDestroyChambersKMultiplier() - 1) * 100, (game.getDestroyChambersAMultiplier() - 1) * 100));
        destroyChambersButton.setFont(JUNGLE_FONT_TINY);
        destroyChambersButton.addActionListener(e -> game.destroyChambers());
        
        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(JUNGLE_FONT_BUTTON);
        resetButton.addActionListener(e -> game.resetGame());
        
        controlPanel.add(newDayButton);
        controlPanel.add(layMoreEggsButton);
        controlPanel.add(layLessEggsButton);
        controlPanel.add(buildChambersButton);
        controlPanel.add(destroyChambersButton);
        controlPanel.add(resetButton);
        
        leftPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        
        // Center area - main display and graphs side by side
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false); // Transparent to show background
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Main display panel (left side)
        mainDisplayPanel = new MainDisplayPanel();
        mainDisplayPanel.setPreferredSize(new Dimension(600, 600));
        mainDisplayPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            mainDisplayPanel.getBorder()
        ));
        centerPanel.add(mainDisplayPanel, BorderLayout.CENTER);
        
        // Graphs panel (right side) - 2x2 grid, smaller
        JPanel graphsPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // Increased gap between graphs
        graphsPanel.setPreferredSize(new Dimension(600, 600));
        graphsPanel.setOpaque(false); // Transparent to show background
        graphsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        historyPanel = new GraphPanel("Population History", Color.BLUE);
        projectionPanel = new GraphPanel("Population Projection", Color.GREEN);
        lyapunovPanel = new GraphPanel("Lyapunov Exponent", Color.RED);
        bifurcationPanel = new GraphPanel("Bifurcation Diagram (r)", Color.MAGENTA);
        
        graphsPanel.add(historyPanel);
        graphsPanel.add(projectionPanel);
        graphsPanel.add(lyapunovPanel);
        graphsPanel.add(bifurcationPanel);
        
        centerPanel.add(graphsPanel, BorderLayout.EAST);
        
        // Create a container for center panel and technology section
        JPanel mainContentPanel = new JPanel(new BorderLayout(5, 10)); // Increased vertical gap
        mainContentPanel.setOpaque(false); // Transparent to show background
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Technology section placeholder at bottom
        JPanel technologyPanel = new JPanel(new BorderLayout());
        technologyPanel.setPreferredSize(new Dimension(0, 120));
        technologyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Technology Tree (Coming Soon)",
                0, 0, JUNGLE_FONT_MEDIUM
            )
        ));
        technologyPanel.setBackground(new Color(240, 240, 240));
        
        JLabel techPlaceholder = new JLabel("Technology upgrades will appear here", SwingConstants.CENTER);
        techPlaceholder.setFont(JUNGLE_FONT_MEDIUM);
        techPlaceholder.setForeground(Color.GRAY);
        technologyPanel.add(techPlaceholder, BorderLayout.CENTER);
        
        mainContentPanel.add(technologyPanel, BorderLayout.SOUTH);
        
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Set fixed resolution - exact size
        setSize(1400, 800);
        setResizable(false);
        setLocationRelativeTo(null);
        
        // Add component listener to print dimensions once everything is laid out
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Wait a bit for layout to complete, then print dimensions
                javax.swing.SwingUtilities.invokeLater(() -> {
                    printPanelDimensions(leftPanel, statusPanel, controlPanel, 
                                       mainDisplayPanel, technologyPanel, 
                                       newsPanel, graphsPanel);
                    // Remove listener after first print
                    removeComponentListener(this);
                });
            }
        });
    }
    
    private void printPanelDimensions(JPanel leftPanel, JPanel statusPanel, 
                                     JPanel controlPanel, MainDisplayPanel mainDisplayPanel,
                                     JPanel technologyPanel, NewsPanel newsPanel,
                                     JPanel graphsPanel) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║          PANEL DIMENSIONS DEBUG INFO               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        // Main window
        System.out.println("\n📐 MAIN WINDOW:");
        System.out.println("   Size: " + getWidth() + " x " + getHeight() + " pixels");
        System.out.println("   Content Pane: " + getContentPane().getWidth() + " x " + getContentPane().getHeight() + " pixels");
        
        // Left panel (contains status, news, and controls)
        System.out.println("\n📋 LEFT PANEL (Full):");
        System.out.println("   Size: " + leftPanel.getWidth() + " x " + leftPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + leftPanel.getX() + ", " + leftPanel.getY() + ")");
        
        // Colony Status panel
        System.out.println("\n👑 COLONY STATUS PANEL:");
        System.out.println("   Size: " + statusPanel.getWidth() + " x " + statusPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + statusPanel.getX() + ", " + statusPanel.getY() + ")");
        
        // News Panel
        System.out.println("\n📰 NEWS PANEL:");
        System.out.println("   Size: " + newsPanel.getWidth() + " x " + newsPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + newsPanel.getX() + ", " + newsPanel.getY() + ")");
        
        // Actions/Control panel
        System.out.println("\n🎮 ACTIONS PANEL:");
        System.out.println("   Size: " + controlPanel.getWidth() + " x " + controlPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + controlPanel.getX() + ", " + controlPanel.getY() + ")");
        
        // Individual buttons in control panel
        System.out.println("\n🔘 BUTTONS IN ACTIONS PANEL:");
        java.awt.Component[] buttons = controlPanel.getComponents();
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] instanceof JButton) {
                JButton btn = (JButton) buttons[i];
                System.out.println("   Button " + (i+1) + ": " + btn.getWidth() + " x " + btn.getHeight() + " pixels");
            }
        }
        
        // Main canvas (ant display)
        System.out.println("\n🐜 MAIN CANVAS (Ant Display):");
        System.out.println("   Size: " + mainDisplayPanel.getWidth() + " x " + mainDisplayPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + mainDisplayPanel.getX() + ", " + mainDisplayPanel.getY() + ")");
        
        // Graphs panel
        System.out.println("\n📊 GRAPHS PANEL (2x2 Grid):");
        System.out.println("   Size: " + graphsPanel.getWidth() + " x " + graphsPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + graphsPanel.getX() + ", " + graphsPanel.getY() + ")");
        
        // Individual graphs
        System.out.println("\n📈 INDIVIDUAL GRAPHS:");
        System.out.println("   History: " + historyPanel.getWidth() + " x " + historyPanel.getHeight() + " pixels");
        System.out.println("   Projection: " + projectionPanel.getWidth() + " x " + projectionPanel.getHeight() + " pixels");
        System.out.println("   Lyapunov: " + lyapunovPanel.getWidth() + " x " + lyapunovPanel.getHeight() + " pixels");
        System.out.println("   Bifurcation: " + bifurcationPanel.getWidth() + " x " + bifurcationPanel.getHeight() + " pixels");
        
        // Technology panel
        System.out.println("\n🔬 TECHNOLOGY TREE PANEL:");
        System.out.println("   Size: " + technologyPanel.getWidth() + " x " + technologyPanel.getHeight() + " pixels");
        System.out.println("   Location: (" + technologyPanel.getX() + ", " + technologyPanel.getY() + ")");
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              END OF DIMENSIONS DEBUG               ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
    
    // Helper method to create image buttons with fallback text
    private JButton createImageButton(String imagePath, String fallbackText) {
        String[] paths = {
            "PopulationDynamicsSimulator/src/main/res/" + imagePath,
            "src/main/res/" + imagePath,
            "res/" + imagePath,
            "../res/" + imagePath,
            "../../res/" + imagePath,
            imagePath
        };
        
        BufferedImage buttonImage = null;
        for (String path : paths) {
            try {
                File file = new File(path);
                if (file.exists() && file.canRead()) {
                    buttonImage = ImageIO.read(file);
                    if (buttonImage != null) {
                        System.out.println("Loaded " + imagePath + " from: " + file.getAbsolutePath());
                        break;
                    }
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        JButton button;
        if (buttonImage != null) {
            // Create button with image icon
            ImageIcon icon = new ImageIcon(buttonImage);
            button = new JButton(icon);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        } else {
            // Fallback to text button
            System.err.println("Could not find " + imagePath + " - using text fallback");
            button = new JButton(fallbackText);
            button.setFont(JUNGLE_FONT_LARGE);
        }
        
        return button;
    }
    
    public void updateStatus() {
        statusLabel.setText(String.format("<html><b>Day %d</b><br>Population: %.2f ants</html>", 
            game.getCurrentDay(), game.getPopulation()));
        paramLabel.setText(String.format("<html>r=%.3f<br>K=%.2f<br>A=%.2f</html>", 
            game.getR(), game.getK(), game.getA()));
        
        // Update ant population in main display
        int antCount = (int) Math.round(game.getPopulation());
        mainDisplayPanel.updateAnts(antCount);
    }
    
    public void addNewsMessage(String message) {
        // Add new message to the top of the news bar
        String currentText = newsBar.getText();
        String dayPrefix = "Day " + game.getCurrentDay() + ": ";
        newsBar.setText(dayPrefix + message + "\n\n" + currentText);
        
        // Scroll to top to show newest message
        newsBar.setCaretPosition(0);
    }
    
    public void updateHistoryGraph(List<AntColonyGame.Point2D> data) {
        historyPanel.setData(data);
    }
    
    public void updateProjectionGraph(List<AntColonyGame.Point2D> data) {
        projectionPanel.setData(data, true);
    }
    
    public void updateLyapunovGraph(List<AntColonyGame.Point2D> data) {
        lyapunovPanel.setData(data);
    }
    
    public void updateBifurcationGraph(List<AntColonyGame.Point2D> data) {
        bifurcationPanel.setData(data);
    }
    
    @Override
    public void dispose() {
        // Stop animation timer when window closes
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }
    
    // Main display panel that shows queen.png
    class MainDisplayPanel extends JPanel {
        private BufferedImage queenImage;
        private AntRenderer antRenderer;
        
        // Configurable display parameters
        private double imageScaleFactor = 0.2;  // Scale image to 30% of original size
        private int imagePaddingX = 50;         // Horizontal padding from edges
        private int imagePaddingY = 50;         // Vertical padding from edges
        
        public MainDisplayPanel() {
            setBackground(new Color(245, 222, 179)); // Wheat color background
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            antRenderer = new AntRenderer();
            loadImage();
        }
        
        // Update ant population to match game population
        public void updateAnts(int population) {
            int queenCenterX = getWidth() / 2;
            int queenCenterY = getHeight() / 2;
            antRenderer.updateAntPopulation(population, getWidth(), getHeight(), 
                                          queenCenterX, queenCenterY);
            repaint();
        }
        
        // Setters for configurable parameters
        public void setImageScaleFactor(double scale) {
            this.imageScaleFactor = scale;
            repaint();
        }
        
        public void setImagePadding(int paddingX, int paddingY) {
            this.imagePaddingX = paddingX;
            this.imagePaddingY = paddingY;
            repaint();
        }
        
        public double getImageScaleFactor() {
            return imageScaleFactor;
        }
        
        private void loadImage() {
            queenImage = null;
            
            // Print working directory for debugging
            String workingDir = System.getProperty("user.dir");
            System.out.println("=== IMAGE LOADING DEBUG ===");
            System.out.println("Working directory: " + workingDir);
            
            // List of paths to try (in order of preference)
            String[] paths = {
                "PopulationDynamicsSimulator/src/main/res/queen.png",
                "src/main/res/queen.png",
                "res/queen.png",
                "../res/queen.png",
                "../../res/queen.png",
                "../../../res/queen.png",
                "../../../../res/queen.png",
                "main/res/queen.png",
                workingDir + "/PopulationDynamicsSimulator/src/main/res/queen.png",
                workingDir + "/res/queen.png",
                workingDir + "/../res/queen.png",
                "queen.png"
            };
            
            for (String path : paths) {
                try {
                    File file = new File(path);
                    if (file.exists() && file.canRead()) {
                        queenImage = ImageIO.read(file);
                        if (queenImage != null) {
                            System.out.println("SUCCESS! Loaded from: " + file.getAbsolutePath());
                            System.out.println("Image size: " + queenImage.getWidth() + "x" + queenImage.getHeight());
                            return;
                        }
                    }
                } catch (Exception e) {
                    // Continue to next path
                }
            }
            
            // If we got here, nothing worked - print detailed info
            System.err.println("FAILED to find queen.png");
            System.err.println("Tried these absolute paths:");
            for (String path : paths) {
                File f = new File(path);
                System.err.println("  " + f.getAbsolutePath() + " - exists: " + f.exists());
            }
            
            // Also try to list what's actually in the res directory if it exists
            File resDir = new File("PopulationDynamicsSimulator/src/main/res");
            if (resDir.exists() && resDir.isDirectory()) {
                System.err.println("\nContents of PopulationDynamicsSimulator/src/main/res/ directory:");
                String[] files = resDir.list();
                if (files != null) {
                    for (String f : files) {
                        System.err.println("  - " + f);
                    }
                }
            } else {
                System.err.println("\nPopulationDynamicsSimulator/src/main/res/ directory does not exist at: " + resDir.getAbsolutePath());
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            // Draw ants first (so they appear behind the queen)
            antRenderer.renderAnts(g2);
            
            if (queenImage != null) {
                // Calculate scaled dimensions
                int scaledWidth = (int) (queenImage.getWidth() * imageScaleFactor);
                int scaledHeight = (int) (queenImage.getHeight() * imageScaleFactor);
                
                // Center the image
                int x = (getWidth() - scaledWidth) / 2;
                int y = (getHeight() - scaledHeight) / 2;
                
                // Draw scaled image centered
                g2.drawImage(queenImage, x, y, scaledWidth, scaledHeight, null);
            } else {
                // Fallback if image not found
                g2.setColor(Color.GRAY);
                g2.setFont(JUNGLE_FONT_LARGE);
                String msg = "Place queen.png in res/ folder";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(msg)) / 2;
                int y = getHeight() / 2;
                g2.drawString(msg, x, y);
            }
        }
    }
    
    // Graph panel class
    class GraphPanel extends JPanel {
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
            
            // Add click listener to open graph in popup window
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    openGraphWindow();
                }
            });
            
            // Change cursor to hand when hovering
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        private void openGraphWindow() {
            JDialog graphDialog = new JDialog(GameGUI.this, title, false);
            graphDialog.setSize(800, 600);
            graphDialog.setLocationRelativeTo(GameGUI.this);
            
            ZoomableGraphPanel zoomablePanel = new ZoomableGraphPanel(title, plotColor, data, markFirstPoint);
            graphDialog.add(zoomablePanel);
            
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
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int margin = 30;
            
            // Draw title with jungle font
            g2.setColor(Color.BLACK);
            g2.setFont(JUNGLE_FONT_SMALL);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (width - fm.stringWidth(title)) / 2, 15);
            
            if (data == null || data.isEmpty()) return;
            
            // Find data bounds
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            
            for (AntColonyGame.Point2D p : data) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            
            // Force first quadrant only for population graphs
            if (title.contains("History") || title.contains("Projection")) {
                minX = Math.max(0, minX);
                minY = Math.max(0, minY);
            }
            
            // Add padding to bounds
            double rangeX = maxX - minX;
            double rangeY = maxY - minY;
            if (rangeX < 0.001) rangeX = 1;
            if (rangeY < 0.001) rangeY = 1;
            
            if (title.contains("History") || title.contains("Projection")) {
                minX = 0;
            } else {
                minX -= rangeX * 0.05;
            }
            maxX += rangeX * 0.05;
            
            if (title.contains("History") || title.contains("Projection")) {
                minY = 0;
            } else {
                minY -= rangeY * 0.05;
            }
            maxY += rangeY * 0.05;
            
            // Draw axes
            g2.setColor(Color.GRAY);
            g2.drawLine(margin, height - margin, width - margin, height - margin);
            g2.drawLine(margin, margin, margin, height - margin);
            
            // Draw axis labels with jungle font
            g2.setFont(JUNGLE_FONT_TINY);
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
            
            // Draw "you are here" marker for projection
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
    
    // Custom panel for news bar with background image
    class NewsPanel extends JPanel {
        private BufferedImage backgroundImage;
        private JTextArea textArea;
        
        public NewsPanel() {
            setLayout(new BorderLayout());
            textArea = new JTextArea();
            loadBackgroundImage();
        }
        
        public JTextArea getTextArea() {
            return textArea;
        }
        
        private void loadBackgroundImage() {
            try {
                String[] paths = {
                    "PopulationDynamicsSimulator/src/main/res/newspanel.png",
                    "src/main/res/newspanel.png",
                    "res/newspanel.png",
                    "../res/newspanel.png",
                    "../../res/newspanel.png",
                    "newspanel.png"
                };
                
                for (String path : paths) {
                    File file = new File(path);
                    if (file.exists() && file.canRead()) {
                        backgroundImage = ImageIO.read(file);
                        if (backgroundImage != null) {
                            System.out.println("Loaded newspanel.png from: " + file.getAbsolutePath());
                            return;
                        }
                    }
                }
                
                System.err.println("Could not find newspanel.png - using default background");
                backgroundImage = null;
                
            } catch (Exception e) {
                System.err.println("Error loading newspanel.png: " + e.getMessage());
                backgroundImage = null;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (backgroundImage != null) {
                Graphics2D g2 = (Graphics2D) g;
                // Draw background image stretched to fit panel
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
    
    // Zoomable and pannable graph panel for popup windows
    class ZoomableGraphPanel extends JPanel {
        private String title;
        private Color plotColor;
        private List<AntColonyGame.Point2D> data;
        private boolean markFirstPoint;
        
        // Zoom and pan state
        private double zoomLevel = 1.0;
        private double panX = 0;
        private double panY = 0;
        private Point lastMousePos;
        
        public ZoomableGraphPanel(String title, Color plotColor, List<AntColonyGame.Point2D> data, boolean markFirstPoint) {
            this.title = title;
            this.plotColor = plotColor;
            this.data = data;
            this.markFirstPoint = markFirstPoint;
            setBackground(Color.WHITE);
            
            // Mouse wheel for zooming
            addMouseWheelListener(e -> {
                double oldZoom = zoomLevel;
                if (e.getWheelRotation() < 0) {
                    zoomLevel *= 1.1; // Zoom in
                } else {
                    zoomLevel *= 0.9; // Zoom out
                }
                zoomLevel = Math.max(0.1, Math.min(zoomLevel, 10.0)); // Limit zoom
                repaint();
            });
            
            // Mouse dragging for panning
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    lastMousePos = e.getPoint();
                }
            });
            
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    if (lastMousePos != null) {
                        // Reduced sensitivity - divide by larger number to slow down panning
                        panX += (e.getX() - lastMousePos.x) / (zoomLevel * 5.0);
                        panY -= (e.getY() - lastMousePos.y) / (zoomLevel * 5.0); // Inverted Y
                        lastMousePos = e.getPoint();
                        repaint();
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int margin = 60;
            
            // Draw title with jungle font
            g2.setColor(Color.BLACK);
            g2.setFont(JUNGLE_FONT_TITLE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, (width - fm.stringWidth(title)) / 2, 30);
            
            // Draw zoom level indicator with jungle font
            g2.setFont(JUNGLE_FONT_SMALL);
            g2.drawString(String.format("Zoom: %.1fx (Scroll to zoom, Drag to pan)", zoomLevel), 10, height - 10);
            
            if (data == null || data.isEmpty()) return;
            
            // Find data bounds
            double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
            double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
            
            for (AntColonyGame.Point2D p : data) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            
            // Force first quadrant only for population graphs
            if (title.contains("History") || title.contains("Projection")) {
                minX = Math.max(0, minX);
                minY = Math.max(0, minY);
            }
            
            // Add padding to bounds
            double rangeX = maxX - minX;
            double rangeY = maxY - minY;
            if (rangeX < 0.001) rangeX = 1;
            if (rangeY < 0.001) rangeY = 1;
            
            if (title.contains("History") || title.contains("Projection")) {
                minX = 0;
            } else {
                minX -= rangeX * 0.05;
            }
            maxX += rangeX * 0.05;
            
            if (title.contains("History") || title.contains("Projection")) {
                minY = 0;
            } else {
                minY -= rangeY * 0.05;
            }
            maxY += rangeY * 0.05;
            
            // Apply zoom and pan
            double centerX = (minX + maxX) / 2;
            double centerY = (minY + maxY) / 2;
            double zoomedRangeX = rangeX / zoomLevel;
            double zoomedRangeY = rangeY / zoomLevel;
            
            minX = centerX - zoomedRangeX / 2 - panX;
            maxX = centerX + zoomedRangeX / 2 - panX;
            minY = centerY - zoomedRangeY / 2 - panY;
            maxY = centerY + zoomedRangeY / 2 - panY;
            
            // Draw axes
            g2.setColor(Color.GRAY);
            g2.drawLine(margin, height - margin, width - margin, height - margin);
            g2.drawLine(margin, margin, margin, height - margin);
            
            // Draw axis labels with jungle font
            g2.setFont(JUNGLE_FONT_SMALL);
            g2.drawString(String.format("%.2f", minX), margin - 30, height - margin + 20);
            g2.drawString(String.format("%.2f", maxX), width - margin - 30, height - margin + 20);
            g2.drawString(String.format("%.2f", minY), 10, height - margin);
            g2.drawString(String.format("%.2f", maxY), 10, margin + 10);
            
            // Draw data
            g2.setColor(plotColor);
            g2.setStroke(new BasicStroke(2));
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
                    g2.fillOval(x1 - 2, y1 - 2, 4, 4);
                } else {
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
            
            if (title.contains("Bifurcation") && !data.isEmpty()) {
                AntColonyGame.Point2D p = data.get(data.size() - 1);
                int x = margin + (int) ((p.x - minX) / (maxX - minX) * plotWidth);
                int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
                g2.fillOval(x - 2, y - 2, 4, 4);
            }
            
            // Draw "you are here" marker for projection
            if (markFirstPoint && !data.isEmpty()) {
                AntColonyGame.Point2D p = data.get(0);
                int x = margin + (int) ((p.x - minX) / (maxX - minX) * plotWidth);
                int y = height - margin - (int) ((p.y - minY) / (maxY - minY) * plotHeight);
                
                g2.setColor(Color.RED);
                g2.fillOval(x - 8, y - 8, 16, 16);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(x - 8, y - 8, 16, 16);
            }
        }
    }
}