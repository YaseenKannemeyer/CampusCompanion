package mycput.ac.za.studenttimetable.domain;

public class StudentDomain {

    private String studentID;
    private String userID;
    private String groupID;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String courseName;
    private String groupName;

    public StudentDomain() {
    }

    public StudentDomain(String studentID, String userID, String groupID,
                         String firstName, String lastName,
                         String phoneNumber, String email,
                         String courseName, String groupName) {
        this.studentID = studentID;
        this.userID = userID;
        this.groupID = groupID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.courseName = courseName;
        this.groupName = groupName;
    }

    // Shorter constructor (without course/group name)
    public StudentDomain(String studentID, String userID, String groupID,
                         String firstName, String lastName,
                         String phoneNumber, String email) {
        this(studentID, userID, groupID, firstName, lastName, phoneNumber, email, null, null);
    }

    // Getters and Setters
    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return String.format(
                "Student[ID=%s, Name=%s %s, Email=%s, Group=%s, Course=%s]",
                studentID, firstName, lastName, email, groupName, courseName
        );
    }
}