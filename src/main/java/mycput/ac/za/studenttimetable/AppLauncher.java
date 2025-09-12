package mycput.ac.za.studenttimetable;

import com.formdev.flatlaf.FlatLightLaf;
import mycput.ac.za.studenttimetable.connection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AppLauncher {

    public static void main(String[] args) {
        // Set Look & Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("FlatLaf failed: " + e.getMessage());
        }

        // Launch splash screen
        SwingUtilities.invokeLater(AppLauncher::showSplashScreen);
    }

    private static void showSplashScreen() {
        // Load GIF from resources
        ImageIcon originalIcon = new ImageIcon(AppLauncher.class.getResource("/Vid/AUTO.gif"));

        // Get original image dimensions
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Desired maximum size
        int maxWidth = 1000;
        int maxHeight = 700;

        // Calculate scaling factor to maintain aspect ratio
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio); // fit inside 1000x700

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        // Scale the image
        Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel splashLabel = new JLabel(scaledIcon);
        splashLabel.setHorizontalAlignment(SwingConstants.CENTER);
        splashLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Create splash JFrame
        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.getContentPane().add(splashLabel);
        splashFrame.pack();

        // Center on screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - splashFrame.getWidth()) / 2;
        int y = (screenSize.height - splashFrame.getHeight()) / 2;
        splashFrame.setLocation(x, y);

        splashFrame.setVisible(true);

        // Close splash after 5 seconds
        Timer timer = new Timer(5500, e -> {
            splashFrame.dispose();
            launchMainApp();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void launchMainApp() {
        SwingUtilities.invokeLater(() -> {
            StudentTimeTable frame = new StudentTimeTable();

            // Shutdown Derby only when main window closes
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    DBConnection.shutdown();
                    System.exit(0);
                }
            });

            frame.setVisible(true);
        });
    }
}
