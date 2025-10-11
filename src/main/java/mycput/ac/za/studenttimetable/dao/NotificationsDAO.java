/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.NotificationsDomain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class NotificationsDAO {
   
    // Insert a notification and return the generated NotificationID as a String
    public String createNotification(NotificationsDomain notif) throws SQLException {
        String sql = "INSERT INTO Notifications (AdminID, Title, Body) VALUES (?, ?, ?)";
        String generatedId = null;

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, notif.getAdminID());
            stmt.setString(2, notif.getTitle());
            stmt.setString(3, notif.getBody());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating notification failed, no rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs != null && rs.next()) {
                    generatedId = String.valueOf(rs.getInt(1));
                }
            }

            if (generatedId == null) {
                throw new SQLException("Failed to obtain generated NotificationID from DB.");
            }
        }

        return generatedId;
    }

    // Fetch a notification by ID
    public NotificationsDomain getNotificationByID(String notificationID) throws SQLException {
        String sql = "SELECT NotificationID, AdminID, Title, Body, CreatedAt FROM Notifications WHERE NotificationID = ?";
        NotificationsDomain notif = null;

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(notificationID));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    notif = new NotificationsDomain(
                            String.valueOf(rs.getInt("NotificationID")),
                            rs.getString("AdminID"),
                            rs.getString("Title"),
                            rs.getString("Body"),
                            rs.getTimestamp("CreatedAt")
                    );
                }
            }
        }
        return notif;
    }

    // Fetch all notifications
    public List<NotificationsDomain> getAllNotifications() {
        List<NotificationsDomain> notifications = new ArrayList<>();
        String sql = "SELECT NotificationID, AdminID, Title, Body, CreatedAt FROM Notifications ORDER BY CreatedAt DESC";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                notifications.add(new NotificationsDomain(
                        String.valueOf(rs.getInt("NotificationID")),
                        rs.getString("AdminID"),
                        rs.getString("Title"),
                        rs.getString("Body"),
                        rs.getTimestamp("CreatedAt")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }    
}
