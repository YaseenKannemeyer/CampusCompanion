/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.connection;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
import java.io.IOException;

public class PythonServerLauncher {

    private Process pythonProcess;

    public void startServer() {
        try {
            // Full path to Python executable & script
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "/Users/mogamatyaseenkannemeyer/Documents/CPUT 3rd YEAR/PRT2/StudentTimeTable/chatterbot_server_flask.py"
            );

            pb.inheritIO(); // Show Flask output in console
            pythonProcess = pb.start();
            System.out.println("Python chatbot server started.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to start Python server.");
        }
    }

    public void stopServer() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            pythonProcess.destroy();
            System.out.println("Python chatbot server stopped.");
        }
    }
}