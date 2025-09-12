/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class TermDefinitionDomain {

    private int TermID;
    private String SubjectCode;     // FK reference to UserAccount
    private int TermNumber;
    private String TermName;
    private float Weight;

    // Default constructor
    public TermDefinitionDomain() {
    }

    // Full constructor
    public TermDefinitionDomain(int TermID, String SubjectCode, int TermNumber, String TermName, float Weight) {
        this.TermID = TermID;
        this.SubjectCode = SubjectCode;
        this.TermNumber = TermNumber;
        this.TermName = TermName;
        this.Weight = Weight;

    }

    public int getTermID() {
        return TermID;
    }

    public void setTermID(int TermID) {
        this.TermID = TermID;
    }

    public String getSubjectCode() {
        return SubjectCode;
    }

    public void setSubjectCode(String SubjectCode) {
        this.SubjectCode = SubjectCode;
    }

    public int getTermNumber() {
        return TermNumber;
    }

    public void setTermNumber(int TermNumber) {
        this.TermNumber = TermNumber;
    }

    public String getTermName() {
        return TermName;
    }

    public void setTermName(String TermName) {
        this.TermName = TermName;
    }

    public float getWeight() {
        return Weight;
    }

    public void setWeight(float Weight) {
        this.Weight = Weight;
    }

}
