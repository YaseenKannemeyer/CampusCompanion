package mycput.ac.za.studenttimetable.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_PATH = "/Users/mogamatyaseenkannemeyer/Documents/Derby/Project2FinalDB";
    private static final String USERNAME = "Project2FinalDB";  // your DB username
    private static final String PASSWORD = "Project2FinalDB";  // your DB password

    public static Connection derbyConnection() throws SQLException {
        // Tell Derby where your databases are stored
        System.setProperty("derby.system.home", "/Users/mogamatyaseenkannemeyer/Documents/Derby");

        // Embedded connection (no network port)
        String url = "jdbc:derby:" + DB_PATH + ";create=false";

        return DriverManager.getConnection(url, USERNAME, PASSWORD);
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
