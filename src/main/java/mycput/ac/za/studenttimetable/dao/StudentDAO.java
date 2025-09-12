package mycput.ac.za.studenttimetable.dao;

import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.JOptionPane;

public class StudentDAO {

    private final Connection con;

    public StudentDAO() throws SQLException {
        this.con = DBConnection.derbyConnection();
    }

    // =========================
    // PASSWORD HASHING
    // =========================
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // =========================
    // SIGNUP
    // =========================
    public boolean saveStudent(StudentDomain student, String password) {
        String insertUserSQL = "INSERT INTO UserAccount (UserID, Email, PasswordHash, Role) VALUES (?, ?, ?, ?)";
        String insertStudentSQL = "INSERT INTO Student (StudentID, UserID, GroupID, FirstName, LastName, PhoneNumber, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            con.setAutoCommit(false);

            // Check if StudentID already exists
            String checkSQL = "SELECT COUNT(*) FROM Student WHERE StudentID=?";
            try (PreparedStatement ps = con.prepareStatement(checkSQL)) {
                ps.setString(1, student.getStudentID());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(null,
                            "StudentID already exists: " + student.getStudentID(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Generate next UserID
            String nextUserID = "U001";
            String maxIdSQL = "SELECT MAX(UserID) AS maxId FROM UserAccount";
            try (PreparedStatement ps = con.prepareStatement(maxIdSQL);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getString("maxId") != null) {
                    String maxId = rs.getString("maxId");
                    int num = Integer.parseInt(maxId.substring(1)) + 1;
                    nextUserID = String.format("U%03d", num);
                }
            }

            // Insert into UserAccount
            try (PreparedStatement userStmt = con.prepareStatement(insertUserSQL)) {
                userStmt.setString(1, nextUserID);
                userStmt.setString(2, student.getEmail());
                userStmt.setString(3, hashPassword(password));
                userStmt.setString(4, "STUDENT");
                userStmt.executeUpdate();
            }

            // Insert into Student
            try (PreparedStatement studentStmt = con.prepareStatement(insertStudentSQL)) {
                studentStmt.setString(1, student.getStudentID());
                studentStmt.setString(2, nextUserID);
                studentStmt.setString(3, student.getGroupID());
                studentStmt.setString(4, student.getFirstName());
                studentStmt.setString(5, student.getLastName());
                studentStmt.setString(6, student.getPhoneNumber());
                studentStmt.setString(7, student.getEmail());
                studentStmt.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }

            StringBuilder sb = new StringBuilder();
            sb.append("SQLState: ").append(e.getSQLState()).append("\n");
            sb.append("ErrorCode: ").append(e.getErrorCode()).append("\n");
            sb.append("Message: ").append(e.getMessage()).append("\n");
            JOptionPane.showMessageDialog(null, sb.toString(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // =========================
    // LOGIN: verify and fetch student
    // =========================
    public StudentDomain loginStudent(String email, String password) throws SQLException {
        String sql = "SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName, s.PhoneNumber, s.Email, u.PasswordHash " +
                     "FROM UserAccount u " +
                     "JOIN Student s ON u.UserID = s.UserID " +
                     "WHERE u.Email=? AND u.Role='STUDENT'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    if (hashPassword(password).equals(storedHash)) {
                        // Successful login
                        return new StudentDomain(
                                rs.getString("StudentID"),
                                rs.getString("UserID"),
                                rs.getString("GroupID"),
                                rs.getString("FirstName"),
                                rs.getString("LastName"),
                                rs.getString("PhoneNumber"),
                                rs.getString("Email")
                        );
                    } else {
                        System.out.println("Password mismatch for: " + email);
                    }
                } else {
                    System.out.println("No user found for email: " + email);
                }
            }
        }
        return null;
    }

    // =========================
    // GET STUDENT GROUP
    // =========================
    public String getStudentGroupByEmail(String email) throws SQLException {
        String group = null;
        String sql = "SELECT GroupID FROM Student WHERE Email=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    group = rs.getString("GroupID");
                }
            }
        }
        return group;
    }
}
