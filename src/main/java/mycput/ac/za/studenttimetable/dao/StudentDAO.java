package mycput.ac.za.studenttimetable.dao;

import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

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
            try (PreparedStatement ps = con.prepareStatement(maxIdSQL); ResultSet rs = ps.executeQuery()) {
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
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            StringBuilder sb = new StringBuilder();
            sb.append("SQLState: ").append(e.getSQLState()).append("\n");
            sb.append("ErrorCode: ").append(e.getErrorCode()).append("\n");
            sb.append("Message: ").append(e.getMessage()).append("\n");
            JOptionPane.showMessageDialog(null, sb.toString(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // =========================
    // LOGIN: verify and fetch student
    // =========================
    public StudentDomain loginStudent(String email, String password) throws SQLException {
        String sql = """
            SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName, 
                   s.PhoneNumber, s.Email, u.PasswordHash
            FROM UserAccount u
            JOIN Student s ON u.UserID = s.UserID
            WHERE u.Email=? AND u.Role='STUDENT'
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    if (hashPassword(password).equals(storedHash)) {
                        return new StudentDomain(
                                rs.getString("StudentID"),
                                rs.getString("UserID"),
                                rs.getString("GroupID"),
                                rs.getString("FirstName"),
                                rs.getString("LastName"),
                                rs.getString("PhoneNumber"),
                                rs.getString("Email")
                        );
                    }
                }
            }
        }
        return null;
    }

   // =========================
// GET FULL STUDENT PROFILE
// =========================
public StudentDomain getStudentProfile(String studentId) throws SQLException {
    String sql = """
        SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName,
               s.PhoneNumber, s.Email,
               g.GroupName, c.CourseName
        FROM Student s
        LEFT JOIN StudentGroup sg ON s.GroupID = sg.GroupID
        LEFT JOIN Course c ON sg.CourseID = c.CourseID
        LEFT JOIN StudentGroup g ON s.GroupID = g.GroupID
        LEFT JOIN StudentGroup sg2 ON s.GroupID = sg2.GroupID
        LEFT JOIN StudentGroup g2 ON s.GroupID = g2.GroupID
    """;

    // Simplify: use correct joins
    sql = """
        SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName,
               s.PhoneNumber, s.Email,
               sg.GroupName, c.CourseName
        FROM Student s
        LEFT JOIN StudentGroup sg ON s.GroupID = sg.GroupID
        LEFT JOIN Course c ON sg.CourseID = c.CourseID
        WHERE s.StudentID = ?
    """;

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, studentId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                StudentDomain student = new StudentDomain();
                student.setStudentID(rs.getString("StudentID"));
                student.setUserID(rs.getString("UserID"));
                student.setGroupID(rs.getString("GroupID"));
                student.setFirstName(rs.getString("FirstName"));
                student.setLastName(rs.getString("LastName"));
                student.setPhoneNumber(rs.getString("PhoneNumber"));
                student.setEmail(rs.getString("Email"));
                student.setGroupName(rs.getString("GroupName")); // new
                student.setCourseName(rs.getString("CourseName")); // new
                return student;
            }
        }
    }
    return null;
}


    // =========================
    // GET ALL STUDENTS
    // =========================
    public List<StudentDomain> getAllStudents() throws SQLException {
        List<StudentDomain> students = new ArrayList<>();
        String sql = """
            SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName, 
                   s.PhoneNumber, s.Email, c.CourseName
            FROM Student s
            LEFT JOIN StudentGroup g ON s.GroupID = g.GroupID
            LEFT JOIN Course c ON g.CourseID = c.CourseID
        """;

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StudentDomain student = new StudentDomain(
                        rs.getString("StudentID"),
                        rs.getString("UserID"),
                        rs.getString("GroupID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Email")
                );
                student.setCourseName(rs.getString("CourseName"));
                students.add(student);
            }
        }
        return students;
    }

    // =========================
    // GET STUDENT BY EMAIL
    // =========================
    public StudentDomain getStudentByEmail(String email) throws SQLException {
        String sql = """
            SELECT s.StudentID, s.UserID, s.GroupID, s.FirstName, s.LastName,
                   s.PhoneNumber, s.Email, c.CourseName
            FROM Student s
            LEFT JOIN StudentGroup g ON s.GroupID = g.GroupID
            LEFT JOIN Course c ON g.CourseID = c.CourseID
            WHERE s.Email = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StudentDomain student = new StudentDomain(
                            rs.getString("StudentID"),
                            rs.getString("UserID"),
                            rs.getString("GroupID"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            rs.getString("PhoneNumber"),
                            rs.getString("Email")
                    );
                    student.setCourseName(rs.getString("CourseName"));
                    return student;
                }
            }
        }
        return null;
    }
}
