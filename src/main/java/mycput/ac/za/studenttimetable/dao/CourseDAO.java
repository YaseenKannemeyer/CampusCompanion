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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.CourseDomain;

public class CourseDAO {

    private Connection con;

    public CourseDAO() {
        try {
            this.con = DBConnection.derbyConnection();
            System.out.println("✅ Database connection successful for CourseDAO!");
        } catch (SQLException e) {
            System.out.println("❌ CONNECTION FAILED in CourseDAO: " + e.getMessage());
        }
    }

}
    

