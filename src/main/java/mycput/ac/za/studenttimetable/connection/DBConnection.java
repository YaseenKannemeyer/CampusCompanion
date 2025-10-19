package mycput.ac.za.studenttimetable.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBConnection {

    // Relative path to the database folder
    private static final String DB_FOLDER = "db";
    private static final String DB_NAME = "Project2FinalDB";
    private static final String USERNAME = "Project2FinalDB";  // your DB username
    private static final String PASSWORD = "Project2FinalDB";  // your DB password

    public static Connection derbyConnection() throws SQLException {
        try {
            // Get current working directory dynamically
            String appDir = new File(".").getCanonicalPath();

            // Full path to the database
            String dbPath = appDir + File.separator + DB_FOLDER + File.separator + DB_NAME;

            // Set Derby system home to the db folder
            System.setProperty("derby.system.home", appDir + File.separator + DB_FOLDER);

            // Embedded connection (no network port)
            String url = "jdbc:derby:" + dbPath + ";create=false";

            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (Exception e) {
            throw new SQLException("Failed to connect to Derby database", e);
        }
    }

    public static void shutdown() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if ("XJ015".equals(e.getSQLState())) {
                System.out.println("Derby shutdown successfully.");
            } else {
                e.printStackTrace();
            }
        }
    }
}
