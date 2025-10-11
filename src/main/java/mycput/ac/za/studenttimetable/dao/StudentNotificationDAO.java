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
import mycput.ac.za.studenttimetable.domain.NotificationsDomain;
import mycput.ac.za.studenttimetable.domain.StudentNotificationDomain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class StudentNotificationDAO {
   
    // Add a notification for a student (used by admin)
    public boolean addStudentNotification(StudentNotificationDomain studentNotification) {
        String sql = "INSERT INTO StudentNotification (NotificationID, StudentID) VALUES (?, ?)";
        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentNotification.getNotificationID());

stmt.setString(2, studentNotification.getStudentID());


            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch all notifications for a student (direct + group notifications)
    public List<NotificationsDomain> getNotificationsByStudentID(String studentID) {
        List<NotificationsDomain> notifications = new ArrayList<>();
        String sql = """
            SELECT n.* FROM Notifications n
            JOIN StudentNotification sn ON n.NotificationID = sn.NotificationID
            WHERE sn.StudentID = ?
            UNION
            SELECT n.* FROM Notifications n
            JOIN GroupNotification gn ON n.NotificationID = gn.NotificationID
            JOIN Student s ON s.GroupID = gn.GroupID
            WHERE s.StudentID = ?
            ORDER BY CreatedAt DESC
        """;

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            stmt.setString(2, studentID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new NotificationsDomain(
                            String.valueOf(rs.getInt("NotificationID")),
                            rs.getString("AdminID"),
                            rs.getString("Title"),
                            rs.getString("Body"),
                            rs.getTimestamp("CreatedAt")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }

    // Mark a notification as read for a student
    public boolean markNotificationAsRead(String studentID, int notificationID) {
        String sql = "UPDATE StudentNotification SET Read = TRUE WHERE StudentID = ? AND NotificationID = ?";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, studentID);
            stmt.setInt(2, notificationID);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}
