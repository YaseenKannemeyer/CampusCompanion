/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class Session {
    
    private static String studentId;
    private static String studentGroup;

    public static void setStudent(String id, String group) {
        studentId = id;
        studentGroup = group;
    }

    public static String getStudentId() { return studentId; }
    public static String getStudentGroup() { return studentGroup; }
    
}
