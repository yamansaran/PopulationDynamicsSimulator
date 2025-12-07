package PopulationDynamicsSimulator.src.main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Main GUI frame for the Ant Colony Manager game.
 */
public class GameGUI extends JFrame {
    
    private static final Color BACKGROUND_COLOR = new Color(0xEB9B6E);
    
    private AntColonyGame game;
    private Timer animationTimer;
    
    // UI Components
    private JLabel statusLabel;
    private JLabel paramLabel;
    private JLabel techPointsLabel;
    private JTextArea newsBar;
    private GraphPanel historyPanel;
    private GraphPanel projectionPanel;
    private GraphPanel lyapunovPanel;
    private GraphPanel bifurcationPanel;
    private MainDisplayPanel mainDisplayPanel;
    
    public GameGUI(AntColonyGame game) {
        this.game = game;
        setupUI();
        startAnimation();
    }
    
    private void startAnimation() {
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
        setResizable(false);
        
        getContentPane().setBackground(BACKGROUND_COLOR);
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildCenterPanel(), BorderLayout.CENTER);
        
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }
    
    // ==================== PANEL BUILDERS ====================
    
    private JPanel buildLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setOpaque(false);
        
        leftPanel.add(buildStatusPanel(), BorderLayout.NORTH);
        leftPanel.add(buildNewsPanel(), BorderLayout.CENTER);
        leftPanel.add(buildControlPanel(), BorderLayout.SOUTH);
        
        return leftPanel;
    }
    
    private JPanel buildStatusPanel() {
        ColonyStatusPanel statusPanel = new ColonyStatusPanel();
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel statusContentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusContentPanel.setOpaque(false);
        statusContentPanel.setBorder(BorderFactory.createEmptyBorder(7, 8, 7, 8));
        
        statusLabel = new JLabel(String.format("<html><b>Day %d</b><br>Population: %.2f ants</html>", 
            game.getCurrentDay(), game.getPopulation()));
        statusLabel.setFont(GameFonts.MEDIUM);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        paramLabel = new JLabel(String.format("<html>r=%.3f<br>K=%.2f<br>A=%.2f</html>", 
            game.getR(), game.getK(), game.getA()));
        paramLabel.setFont(GameFonts.SMALL);
        paramLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        statusContentPanel.add(statusLabel);
        statusContentPanel.add(paramLabel);
        statusPanel.add(statusContentPanel, BorderLayout.CENTER);
        
        return statusPanel;
    }
    
    private JPanel buildNewsPanel() {
        NewsPanel newsPanel = new NewsPanel();
        newsPanel.setBackground(BACKGROUND_COLOR);
        newsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        newsBar = newsPanel.getTextArea();
        newsBar.setEditable(false);
        newsBar.setLineWrap(true);
        newsBar.setWrapStyleWord(true);
        newsBar.setFont(GameFonts.SMALL);
        newsBar.setText("Welcome to Ant Colony Manager!\n\nPress 'New Day' to begin...");
        newsBar.setOpaque(false);
        newsBar.setForeground(Color.BLACK);
        
        JScrollPane newsScrollPane = new JScrollPane(newsBar);
        newsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        newsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newsScrollPane.setOpaque(false);
        newsScrollPane.getViewport().setOpaque(false);
        newsScrollPane.setBorder(BorderFactory.createEmptyBorder(36, 40, 11, 30));
        
        newsPanel.add(newsScrollPane, BorderLayout.CENTER);
        
        // Scroll buttons
        JPanel scrollButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        scrollButtonPanel.setOpaque(false);
        scrollButtonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 45, 0));
        
        JButton scrollUpButton = createImageButton("upbutton.png", "▲");
        scrollUpButton.setPreferredSize(new Dimension(67, 44));
        scrollUpButton.addActionListener(e -> {
            JScrollBar verticalBar = newsScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getValue() - 30);
        });
        
        JButton scrollDownButton = createImageButton("downbutton.png", "▼");
        scrollDownButton.setPreferredSize(new Dimension(67, 44));
        scrollDownButton.addActionListener(e -> {
            JScrollBar verticalBar = newsScrollPane.getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getValue() + 30);
        });
        
        scrollButtonPanel.add(scrollUpButton);
        scrollButtonPanel.add(scrollDownButton);
        newsPanel.add(scrollButtonPanel, BorderLayout.SOUTH);
        
        return newsPanel;
    }
    
    private JPanel buildControlPanel() {
        ActionsPanel controlPanel = new ActionsPanel();
        controlPanel.setLayout(new BorderLayout(5, 5));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        ActionsLabelPanel actionsLabelPanel = new ActionsLabelPanel();
        actionsLabelPanel.setPreferredSize(new Dimension(240, 40));
        actionsLabelPanel.setOpaque(false);
        controlPanel.add(actionsLabelPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 8));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Action buttons
        JButton newDayButton = createActionButton("New Day", "buttonnewday.png", true);
        newDayButton.addActionListener(e -> game.newDay());
        
        JButton layMoreEggsButton = createActionButton(
            String.format("<html><center>Lay More Eggs<br>(%+.0f%% r)</center></html>", 
            (game.getLayMoreEggsRMultiplier() - 1) * 100), "buttonmoreeggs.png", false);
        layMoreEggsButton.addActionListener(e -> game.layMoreEggs());
        
        JButton layLessEggsButton = createActionButton(
            String.format("<html><center>Lay Less Eggs<br>(%+.0f%% r)</center></html>", 
            (game.getLayLessEggsRMultiplier() - 1) * 100), "buttonlesseggs.png", false);
        layLessEggsButton.addActionListener(e -> game.layLessEggs());
        
        JButton buildChambersButton = createActionButton(
            String.format("<html><center>Build Chambers<br>(%+.0f%% K, %+.0f%% A)</center></html>", 
            (game.getBuildChambersKMultiplier() - 1) * 100, (game.getBuildChambersAMultiplier() - 1) * 100), 
            "buttonmorechambers.png", false);
        buildChambersButton.addActionListener(e -> game.buildChambers());
        
        JButton destroyChambersButton = createActionButton(
            String.format("<html><center>Destroy Chambers<br>(%+.0f%% K, %+.0f%% A)</center></html>", 
            (game.getDestroyChambersKMultiplier() - 1) * 100, (game.getDestroyChambersAMultiplier() - 1) * 100), 
            "buttonlesschambers.png", false);
        destroyChambersButton.addActionListener(e -> game.destroyChambers());
        
        JButton resetButton = createActionButton("Reset Game", "buttonreset.png", true);
        resetButton.addActionListener(e -> game.resetGame());
        
        buttonPanel.add(newDayButton);
        buttonPanel.add(layMoreEggsButton);
        buttonPanel.add(layLessEggsButton);
        buttonPanel.add(buildChambersButton);
        buttonPanel.add(destroyChambersButton);
        buttonPanel.add(resetButton);
        
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        
        return controlPanel;
    }
    
    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Main display panel
        mainDisplayPanel = new MainDisplayPanel();
        mainDisplayPanel.setPreferredSize(new Dimension(600, 600));
        mainDisplayPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            mainDisplayPanel.getBorder()
        ));
        centerPanel.add(mainDisplayPanel, BorderLayout.CENTER);
        
        // Graphs panel
        JPanel graphsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        graphsPanel.setPreferredSize(new Dimension(600, 600));
        graphsPanel.setOpaque(false);
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
        
        // Main content with technology panel
        JPanel mainContentPanel = new JPanel(new BorderLayout(5, 10));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Technology panel with tech points counter (uses TechPanel.png if available)
        JPanel technologyPanel = new JPanel(new BorderLayout());
        technologyPanel.setPreferredSize(new Dimension(0, 120));
        // Remove borders/titles so the background image can cover entire panel
        technologyPanel.setBorder(null);
        // Ensure panel background matches the app default so transparent areas show correctly
        technologyPanel.setBackground(BACKGROUND_COLOR);

        JPanel techPointsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        techPointsPanel.setOpaque(false);
        techPointsLabel = new JLabel(String.format("<html><b>Tech Points: %d</b></html>", 
            game.getTechnologyPoints()));
        techPointsLabel.setFont(GameFonts.LARGE);
        techPointsLabel.setForeground(new Color(0, 0, 0));
        techPointsPanel.add(techPointsLabel);

        // Try to load background image for the technology panel
        BufferedImage techBg = null;
        String[] bgPaths = new String[] {
            "PopulationDynamicsSimulator/src/main/res/TechPanel.png",
            "src/main/res/TechPanel.png",
            "res/TechPanel.png",
            "TechPanel.png"
        };
        for (String p : bgPaths) {
            try {
                File f = new File(p);
                if (f.exists() && f.canRead()) {
                    techBg = ImageIO.read(f);
                    if (techBg != null) break;
                }
            } catch (Exception ex) {
                // ignore and try next path
            }
        }

        // Make the loaded image available as final for use in anonymous inner classes
        final BufferedImage finalTechBg = techBg;

        // Create invisible overlay panel with absolute positioning for buttons
        JPanel buttonOverlay = new JPanel(null) {  // null layout = absolute positioning
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                // Invisible - just for positioning
            }
            
            @Override
            protected void paintChildren(java.awt.Graphics g) {
                // Allow children to render outside panel bounds (no clipping)
                java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                java.awt.Shape originalClip = g2d.getClip();
                g2d.setClip(null);  // Disable clipping
                super.paintChildren(g2d);
                g2d.setClip(originalClip);  // Restore original clip
            }
        };
        buttonOverlay.setOpaque(false);

        // Button 1: Nanny Ants (increases A by 1, costs 1 tech point)
        JButton button1 = createTechButton(
            "techbutton1.png",
            "Nanny Ants",
            () -> {
                if (game.deductTechnologyPoints(-1)) {
                    game.setA(game.getA() + (game.getK() * 0.05) );
                    updateStatus();
                }
            }
        );
        buttonOverlay.add(button1);

        // Button 2: Cull (reduces population by 50%, costs 1 tech point)
        JButton button2 = createTechButton(
            "techbutton2.png",
            "Cull",
            () -> {
                if (game.deductTechnologyPoints(-1)) {
                    game.setX(game.getPopulation() * 0.5);
                    updateStatus();
                }
            }
        );
        buttonOverlay.add(button2);

        // Button 3: Breeding Frenzy (multiplies r by 2.5, costs 1 tech point)
        JButton button3 = createTechButton(
            "techbutton3.png",
            "Breeding Frenzy",
            () -> {
                if (game.deductTechnologyPoints(-1)) {
                    game.setR(game.getR() * 2.5);
                    updateStatus();
                }
            }
        );
        buttonOverlay.add(button3);
        
        // Update button positions when overlay is resized
        buttonOverlay.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = buttonOverlay.getWidth();
                int yPos = -15;  // Positioned higher, allowing top to extend above panel
                button1.setBounds(w - 100 - 55, yPos, 110, 90);  // 150px from right
                button2.setBounds(w - 250 - 55, yPos, 110, 90);  // 300px from right
                button3.setBounds(w - 400 - 55, yPos, 110, 90);  // 450px from right
            }
        });

        if (finalTechBg != null) {
            // Create a panel that paints the background scaled to fill the whole area
            JPanel bgPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(java.awt.Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(finalTechBg, 0, 0, getWidth(), getHeight(), null);
                }
            };
            bgPanel.setOpaque(true);
            bgPanel.setBackground(BACKGROUND_COLOR);
            bgPanel.add(techPointsPanel, BorderLayout.NORTH);
            bgPanel.add(buttonOverlay, BorderLayout.CENTER);
            technologyPanel.add(bgPanel, BorderLayout.CENTER);
        } else {
            // No background image; add the tech points panel to the top-left and buttons overlay
            technologyPanel.add(techPointsPanel, BorderLayout.NORTH);
            technologyPanel.add(buttonOverlay, BorderLayout.CENTER);
        }

        // Debug: print technology panel size when rendered
        technologyPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                System.out.println("Technology Panel Size: " + e.getComponent().getWidth() + "x" + e.getComponent().getHeight() + " pixels");
            }
        });
        
        mainContentPanel.add(technologyPanel, BorderLayout.SOUTH);
        
        return mainContentPanel;
    }
    
    // ==================== HELPER METHODS ====================
    
    private JButton createImageButton(String imagePath, String fallbackText) {
        BufferedImage buttonImage = ImageLoader.load(imagePath, false);
        
        JButton button;
        if (buttonImage != null) {
            ImageIcon icon = new ImageIcon(buttonImage);
            button = new JButton(icon);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
        } else {
            button = new JButton(fallbackText);
            button.setFont(GameFonts.LARGE);
        }
        
        return button;
    }
    
    private JButton createActionButton(String text, String imageName, boolean isLargeFont) {
        BufferedImage buttonImage = ImageLoader.load(imageName, false);
        
        JButton button;
        if (buttonImage != null) {
            ImageIcon icon = new ImageIcon(buttonImage);
            button = new JButton(text, icon);
            button.setHorizontalTextPosition(SwingConstants.LEFT);
            button.setVerticalTextPosition(SwingConstants.CENTER);
            button.setIconTextGap(-buttonImage.getWidth() / 2);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
        } else {
            button = new JButton(text);
            if (imageName.contains("newday")) {
                button.setBackground(new Color(100, 200, 100));
            }
        }
        
        button.setFont(isLargeFont ? GameFonts.BUTTON : GameFonts.TINY);
        return button;
    }
    
    /**
     * Creates a technology button with image background and centered text overlay.
     * Button size: 110x90 pixels.
     */
    private JButton createTechButton(String imageName, String buttonText, Runnable action) {
        BufferedImage buttonImage = ImageLoader.load(imageName, false);
        
        JButton button = new JButton(buttonText) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                // Fill background with button color if no image
                if (buttonImage != null) {
                    g.drawImage(buttonImage, 0, 0, getWidth(), getHeight(), null);
                }
                // Draw centered text (5 pixels lower)
                java.awt.FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(buttonText)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() + 5;
                g.setColor(Color.WHITE);
                g.drawString(buttonText, x, y);
            }
        };
        
        button.setPreferredSize(new Dimension(110, 90));
        button.setMinimumSize(new Dimension(110, 90));
        button.setMaximumSize(new Dimension(110, 90));
        button.setFont(GameFonts.MEDIUM);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                System.err.println("Error executing tech button action: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        return button;
    }
    // ==================== PUBLIC API ====================
    
    public void updateStatus() {
        statusLabel.setText(String.format("<html><b>Day %d</b><br>Population: %.2f ants</html>", 
            game.getCurrentDay(), game.getPopulation()));
        paramLabel.setText(String.format("<html>r=%.3f<br>K=%.2f<br>A=%.2f</html>", 
            game.getR(), game.getK(), game.getA()));
        techPointsLabel.setText(String.format("<html><b>Tech Points: %d</b></html>", 
            game.getTechnologyPoints()));
        
        int antCount = (int) Math.round(game.getPopulation());
        mainDisplayPanel.updateAnts(antCount);
    }
    
    public void addNewsMessage(String message) {
        String currentText = newsBar.getText();
        String dayPrefix = "Day " + game.getCurrentDay() + ": ";
        newsBar.setText(dayPrefix + message + "\n\n" + currentText);
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
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }
}
