/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.dao;

import java.sql.Connection;
import java.sql.SQLException;
import mycput.ac.za.studenttimetable.connection.DBConnection;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class SubjectCourseDAO {
    private Connection con;
    
    public SubjectCourseDAO() {
        try {
            this.con = DBConnection.derbyConnection();
            System.out.println("Course_Subject DB connection OK!");
        } catch (SQLException e) {
            System.out.println("Course_Subject connection FAILED! " + e.getMessage());
        }
    }
    
}
