package mycput.ac.za.studenttimetable;

import com.formdev.flatlaf.FlatLightLaf;
import mycput.ac.za.studenttimetable.connection.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import mycput.ac.za.studenttimetable.connection.PythonServerLauncher;

public class AppLauncher {

    private static PythonServerLauncher pythonLauncher;

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
        String gifPath = AppLauncher.class.getResource("/Vid/AUTO.gif").toString();
        String html = String.format(
            "<html><center><img src='%s' width='%d' height='%d'></center></html>",
            gifPath, 800, 450
        );

        JLabel splashLabel = new JLabel(html, SwingConstants.CENTER);
        splashLabel.setOpaque(true);
        splashLabel.setBackground(Color.BLACK);

        JFrame splashFrame = new JFrame();
        splashFrame.setUndecorated(true);
        splashFrame.add(splashLabel);
        splashFrame.setSize(800, 450);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);

        // Close splash after 3 seconds
        Timer timer = new Timer(3000, e -> {
            splashFrame.dispose();
            launchMainApp();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static void launchMainApp() {
        SwingUtilities.invokeLater(() -> {
            // 1️⃣ Start Python server
            pythonLauncher = new PythonServerLauncher();
            pythonLauncher.startServer();

            // 2️⃣ Launch main JFrame
            StudentTimeTable frame = new StudentTimeTable();

            // 3️⃣ Handle shutdown
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Stop Python server
                    if (pythonLauncher != null) pythonLauncher.stopServer();

                    // Shutdown Derby
                    DBConnection.shutdown();
                    System.exit(0);
                }
            });

            frame.setVisible(true);
        });
    }
}
