/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;
import java.sql.Date;
import java.sql.Time;


/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class TimetableDomain {
    

    private int timeTableID;
    private String subjectCode;
    private String groupID;
    private String lecturerID;
    private String roomID;
    private String classType;
    private Date classDate;
    private Time startTime;
    private Time endTime;

    // Default constructor
    public TimetableDomain() {}

    // Full constructor
    public TimetableDomain(int timeTableID, String subjectCode, String groupID, String lecturerID,
                           String roomID, String classType, Date classDate, Time startTime, Time endTime) {
        this.timeTableID = timeTableID;
        this.subjectCode = subjectCode;
        this.groupID = groupID;
        this.lecturerID = lecturerID;
        this.roomID = roomID;
        this.classType = classType;
        this.classDate = classDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters
    public int getTimeTableID() {
        return timeTableID;
    }

    public void setTimeTableID(int timeTableID) {
        this.timeTableID = timeTableID;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(String lecturerID) {
        this.lecturerID = lecturerID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Date getClassDate() {
        return classDate;
    }

    public void setClassDate(Date classDate) {
        this.classDate = classDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TimeTableDomain{" +
                "timeTableID=" + timeTableID +
                ", subjectCode='" + subjectCode + '\'' +
                ", groupID='" + groupID + '\'' +
                ", lecturerID='" + lecturerID + '\'' +
                ", roomID='" + roomID + '\'' +
                ", classType='" + classType + '\'' +
                ", classDate=" + classDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}

    
