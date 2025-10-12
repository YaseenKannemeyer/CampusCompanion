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
        String gifPath = AppLauncher.class.getResource("/Vid/AUTO.gif").toString();

        // Use HTML scaling to preserve animation and quality
        String html = String.format(
            "<html><center><img src='%s' width='%d' height='%d'></center></html>",
            gifPath, 800, 450
        );

        JLabel splashLabel = new JLabel(html, SwingConstants.CENTER);
        splashLabel.setOpaque(true);
        splashLabel.setBackground(Color.BLACK);

        // Create splash JFrame
        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.add(splashLabel);
        splashFrame.setSize(800, 450);
        splashFrame.setLocationRelativeTo(null); // Center on screen
        splashFrame.setVisible(true);

        // Close splash after 5.5 seconds
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
