package mycput.ac.za.studenttimetable.domain;

public class StudentDomain {

    private String StudentID;
    private String UserID;
    private String GroupID;
    private String FirstName;
    private String LastName;
    private String PhoneNumber;
    private String Email;

    public StudentDomain() {
    }

    public StudentDomain(String StudentID, String UserID, String GroupID, 
                         String FirstName, String LastName, 
                         String PhoneNumber, String Email) {
        this.StudentID = StudentID;
        this.UserID = UserID;
        this.GroupID = GroupID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.PhoneNumber = PhoneNumber;
        this.Email = Email;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String StudentID) {
        this.StudentID = StudentID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String GroupID) {
        this.GroupID = GroupID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
}
