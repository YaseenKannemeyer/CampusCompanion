package mycput.ac.za.studenttimetable.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.SubjectDomain;

/**
 * Handles all CRUD operations for the Subject table.
 * Table structure:
 *   Subject (
 *       SubjectCode VARCHAR(10) PRIMARY KEY,
 *       SubjectName VARCHAR(100) NOT NULL,
 *       YearLevel   INT NOT NULL
 *   )
 */
public class SubjectDAO {

    private final Connection con;

    public SubjectDAO() {
        Connection tempCon = null;
        try {
            tempCon = DBConnection.derbyConnection();
            System.out.println("[SubjectDAO] Database connection successful!");
        } catch (SQLException e) {
            System.err.println("[SubjectDAO] Connection failed: " + e.getMessage());
        }
        this.con = tempCon;
    }

    // ----------------------------- INSERT -----------------------------
    public void insert(SubjectDomain subject) throws SQLException {
        String sql = "INSERT INTO Subject (SubjectCode, SubjectName, YearLevel) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.getYearLevel());
            ps.executeUpdate();
            System.out.println("[SubjectDAO] Inserted subject: " + subject.getSubjectName());
        }
    }

    // ----------------------------- UPDATE -----------------------------
    public void update(SubjectDomain subject) throws SQLException {
        String sql = "UPDATE Subject SET SubjectName = ?, YearLevel = ? WHERE SubjectCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectName());
            ps.setInt(2, subject.getYearLevel());
            ps.setString(3, subject.getSubjectCode());
            int rows = ps.executeUpdate();
            System.out.println("[SubjectDAO] Updated " + rows + " row(s) for " + subject.getSubjectCode());
        }
    }

    // ----------------------------- DELETE -----------------------------
    public void delete(String subjectCode) throws SQLException {
        String sql = "DELETE FROM Subject WHERE SubjectCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectCode);
            int rows = ps.executeUpdate();
            System.out.println("[SubjectDAO] Deleted " + rows + " subject(s) with code " + subjectCode);
        }
    }

    // ----------------------------- SELECT ONE -----------------------------
    public SubjectDomain getByCode(String subjectCode) throws SQLException {
        String sql = "SELECT SubjectCode, SubjectName, YearLevel FROM Subject WHERE SubjectCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SubjectDomain(
                            rs.getString("SubjectCode"),
                            rs.getString("SubjectName"),
                            rs.getInt("YearLevel")
                    );
                }
            }
        }
        return null;
    }

    // ----------------------------- SELECT ALL -----------------------------
    public List<SubjectDomain> getAllSubjects() throws SQLException {
        List<SubjectDomain> subjects = new ArrayList<>();
        String sql = "SELECT SubjectCode, SubjectName, YearLevel FROM Subject ORDER BY SubjectName ASC";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                subjects.add(new SubjectDomain(
                        rs.getString("SubjectCode"),
                        rs.getString("SubjectName"),
                        rs.getInt("YearLevel")
                ));
            }
        }
        return subjects;
    }

    // ----------------------------- UTIL -----------------------------
    public boolean exists(String subjectCode) throws SQLException {
        String sql = "SELECT 1 FROM Subject WHERE SubjectCode = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
