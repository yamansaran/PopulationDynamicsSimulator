package PopulationDynamicsSimulator.src.main.java;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Centralized image loading utility.
 */
public class ImageLoader {
    
    private static final String[] PATHS = {
        "PopulationDynamicsSimulator/src/main/res/",
        "src/main/res/",
        "res/",
        "../res/",
        "../../res/",
        ""
    };
    
    public static BufferedImage load(String imageName) {
        return load(imageName, true);
    }
    
    public static BufferedImage load(String imageName, boolean logSuccess) {
        for (String basePath : PATHS) {
            try {
                File file = new File(basePath + imageName);
                if (file.exists() && file.canRead()) {
                    BufferedImage image = ImageIO.read(file);
                    if (image != null) {
                        if (logSuccess) {
                            System.out.println("Loaded " + imageName + " from: " + file.getAbsolutePath());
                        }
                        return image;
                    }
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        System.err.println("Could not find " + imageName);
        return null;
    }
}
