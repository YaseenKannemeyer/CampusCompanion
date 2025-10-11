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
import mycput.ac.za.studenttimetable.domain.GroupNotificationsDomain;
import mycput.ac.za.studenttimetable.domain.NotificationsDomain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class GroupNotificationsDAO {
    
    
    // Add a notification for a group (used by admin)
    public boolean addGroupNotification(GroupNotificationsDomain groupNotification) {
        String sql = "INSERT INTO GroupNotification (NotificationID, GroupID) VALUES (?, ?)";

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, groupNotification.getNotificationId());
            stmt.setString(2, groupNotification.getGroupId());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch notifications for a specific group
    public List<NotificationsDomain> getNotificationsByGroupID(String groupID) {
        List<NotificationsDomain> notifications = new ArrayList<>();
        String sql = """
            SELECT n.* FROM Notifications n
            JOIN GroupNotification gn ON n.NotificationID = gn.NotificationID
            WHERE gn.GroupID = ?
            ORDER BY CreatedAt DESC
        """;

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, groupID);
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
}
