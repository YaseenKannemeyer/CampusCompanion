/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class SubjectDomain {

    private String SubjectCode;
    private String SubjectName;
    private int YearLevel; // should be int, not String

    public SubjectDomain() {
    }

    public SubjectDomain(String SubjectCode, String SubjectName, int YearLevel) {
        this.SubjectCode = SubjectCode;
        this.SubjectName = SubjectName;
        this.YearLevel = YearLevel;
    }

    public String getSubjectCode() {
        return SubjectCode;
    }

    public void setSubjectCode(String SubjectCode) {
        this.SubjectCode = SubjectCode;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String SubjectName) {
        this.SubjectName = SubjectName;
    }

    public int getYearLevel() {
        return YearLevel;
    }

    public void setYearLevel(int YearLevel) {
        this.YearLevel = YearLevel;
    }
}
