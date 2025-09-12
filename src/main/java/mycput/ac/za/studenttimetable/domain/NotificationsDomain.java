/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

import java.sql.Timestamp;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class NotificationsDomain {

    private String notificationID;
    private String adminID;
    private String title;
    private String body;
    private Timestamp createdAt;

    public NotificationsDomain() {}

    public NotificationsDomain(String notificationID, String adminID, String title, String body, Timestamp createdAt) {
        this.notificationID = notificationID;
        this.adminID = adminID;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationsDomain{" +
                "notificationID='" + notificationID + '\'' +
                ", adminID='" + adminID + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
