package mycput.ac.za.studenttimetable.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import mycput.ac.za.studenttimetable.connection.DBConnection;

public class LecturerDAO {

    private Connection con;

    public LecturerDAO() {
        try {
            this.con = DBConnection.derbyConnection();
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("CONNECTION FAILED!!! " + e.getMessage());
        }
    }

   
 }

