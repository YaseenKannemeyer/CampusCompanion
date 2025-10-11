/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.StudentGroupDomain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class StudentGroupDAO {
    
    // Fetch only GroupIDs for the combo box
    public List<String> getAllGroupIDs() throws SQLException {
        List<String> groupIDs = new ArrayList<>();
        String sql = "SELECT GroupID FROM StudentGroup";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groupIDs.add(rs.getString("GroupID"));
            }
        }
        return groupIDs;
    }

    // Fetch all groups as StudentGroupDomain objects
    public List<StudentGroupDomain> getAllGroups() throws SQLException {
        List<StudentGroupDomain> groups = new ArrayList<>();
        String sql = "SELECT GroupID, CourseID FROM StudentGroup";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                groups.add(new StudentGroupDomain(
                        rs.getString("GroupID"),
                        rs.getString("CourseID")
                ));
            }
        }
        return groups;
    }}