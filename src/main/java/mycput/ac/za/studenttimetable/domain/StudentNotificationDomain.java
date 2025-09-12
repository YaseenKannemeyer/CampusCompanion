/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class StudentNotificationDomain {
    
    private int notificationID;     // INT in SQL
    private String studentID;       // VARCHAR(50) in SQL

    public StudentNotificationDomain() {}

    public StudentNotificationDomain(int notificationID, String studentID) {
        this.notificationID = notificationID;
        this.studentID = studentID;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
    
}
