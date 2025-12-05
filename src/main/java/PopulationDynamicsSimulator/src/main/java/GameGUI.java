package PopulationDynamicsSimulator.src.main.java;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.List;

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
        
        // Technology placeholder
        JPanel technologyPanel = new JPanel(new BorderLayout());
        technologyPanel.setPreferredSize(new Dimension(0, 120));
        technologyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Technology Tree (Coming Soon)",
                0, 0, GameFonts.MEDIUM
            )
        ));
        technologyPanel.setBackground(new Color(240, 240, 240));
        
        JLabel techPlaceholder = new JLabel("Technology upgrades will appear here", SwingConstants.CENTER);
        techPlaceholder.setFont(GameFonts.MEDIUM);
        techPlaceholder.setForeground(Color.GRAY);
        technologyPanel.add(techPlaceholder, BorderLayout.CENTER);
        
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
    
    // ==================== PUBLIC API ====================
    
    public void updateStatus() {
        statusLabel.setText(String.format("<html><b>Day %d</b><br>Population: %.2f ants</html>", 
            game.getCurrentDay(), game.getPopulation()));
        paramLabel.setText(String.format("<html>r=%.3f<br>K=%.2f<br>A=%.2f</html>", 
            game.getR(), game.getK(), game.getA()));
        
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
