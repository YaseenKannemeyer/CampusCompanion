/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class StudentGroupDomain {

    private String GroupID;   // maps to GroupID (PK)
    private String CourseID;  // maps to CourseID (FK)

    public StudentGroupDomain() {
    }

    public StudentGroupDomain(String GroupID, String CourseID) {
        this.GroupID = GroupID;
        this.CourseID = CourseID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }

    public String getCourseID() {
        return CourseID;
    }

    public void setCourseID(String CourseID) {
        this.CourseID = CourseID;
    }

}
