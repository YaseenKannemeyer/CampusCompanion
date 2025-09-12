/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

import java.util.Objects;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class GroupNotificationsDomain {
    
    private int notificationId;     // matches INT
    private String groupId;         // matches VARCHAR(50)

    // Default constructor
    public GroupNotificationsDomain() {}

    // Full constructor
    public GroupNotificationsDomain(int notificationId, String groupId) {
        this.notificationId = notificationId;
        this.groupId = groupId;
    }

    // Getters and setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    // Debug/logging
    @Override
    public String toString() {
        return "GroupNotificationDomain{" +
               "notificationId=" + notificationId +
               ", groupId='" + groupId + '\'' +
               '}';
    }

    // For equality checks
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupNotificationsDomain)) return false;
        GroupNotificationsDomain that = (GroupNotificationsDomain) o;
        return notificationId == that.notificationId &&
               Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, groupId);
    }
}
    
