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
public class CourseDomain {
    
    private String courseId;     // Matches CourseID (VARCHAR(10))
    private String courseName;   // Matches CourseName (VARCHAR(100))

    // Default constructor
    public CourseDomain() {}

    // Full constructor
    public CourseDomain(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // Getters and setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    // For logging/debugging
    @Override
    public String toString() {
        return "CourseDomain{" +
               "courseId='" + courseId + '\'' +
               ", courseName='" + courseName + '\'' +
               '}';
    }

    // For comparing objects properly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseDomain)) return false;
        CourseDomain that = (CourseDomain) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(courseName, that.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, courseName);
    }
    
}
