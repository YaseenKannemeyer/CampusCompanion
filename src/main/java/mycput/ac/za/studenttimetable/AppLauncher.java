package mycput.ac.za.studenttimetable;

import com.formdev.flatlaf.FlatLightLaf;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) {
        // Load GIF splash
        Image gif = new Image(getClass().getResource("/Vid/AUTO.gif").toString());
        ImageView splashView = new ImageView(gif);

        // Scale proportionally to fit 1000x700
        splashView.setFitWidth(1000);
        splashView.setFitHeight(700);
        splashView.setPreserveRatio(true);
        splashView.setSmooth(true);

        StackPane root = new StackPane(splashView);
        Scene scene = new Scene(root, 1000, 700, Color.BLACK);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Wait 5 seconds, then fade out
        PauseTransition displayTime = new PauseTransition(Duration.seconds(5));
        displayTime.setOnFinished(e -> fadeOut(stage, splashView));
        displayTime.play();
    }

    private void fadeOut(Stage stage, ImageView splashView) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), splashView);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            stage.close();
            launchSwingDirectly();
        });
        fade.play();
    }

    private void launchSwingDirectly() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {
                System.err.println("FlatLaf failed: " + e.getMessage());
            }

            new StudentTimeTable();
            Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::shutdown));
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
