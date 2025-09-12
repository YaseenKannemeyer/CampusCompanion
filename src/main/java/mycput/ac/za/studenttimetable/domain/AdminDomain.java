/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.domain;

/**
 *
 * @author mogamatyaseenkannemeyer
 */
public class AdminDomain {

    private String adminID;
    private String userID;     // FK reference to UserAccount
    private String firstName;
    private String lastName;
    private String phoneNumber;

    // Default constructor
    public AdminDomain() {
    }

    // Full constructor
    public AdminDomain(String adminID, String userID, String firstName, String lastName, String phoneNumber) {
        this.adminID = adminID;
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    @Override
    public String toString() {
        return "AdminDomain{"
                + "adminID='" + adminID + '\''
                + ", userID='" + userID + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + '}';
    }

}
