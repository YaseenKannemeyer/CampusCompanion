/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.dao;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class SubjectDAO {

    private Connection con;

    public SubjectDAO() {
        try {
            this.con = DBConnection.derbyConnection();
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("CONNECTION FAILED!!! " + e.getMessage());
        }
        
        
    }
   
}

  

