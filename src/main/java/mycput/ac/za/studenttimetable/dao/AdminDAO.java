package mycput.ac.za.studenttimetable.dao;

import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.AdminDomain;

public class AdminDAO {

    private final Connection con;

    public AdminDAO() throws SQLException {
        this.con = DBConnection.derbyConnection();
    }

    // =========================
    // PASSWORD HASHING (same as StudentDAO)
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
    // LOGIN (verify admin credentials)
    // =========================
    public AdminDomain loginAdmin(String email, String password) throws SQLException {
        String sql = """
            SELECT a.AdminID, a.UserID, a.FirstName, a.LastName, a.PhoneNumber,
                   u.Email, u.PasswordHash
            FROM UserAccount u
            JOIN Admin a ON u.UserID = a.UserID
            WHERE u.Email = ? AND u.Role = 'ADMIN'
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
String inputHash = hashPassword(password);

// Accept either hashed or plain password (temporary)
if (inputHash.equals(storedHash) || password.equals(storedHash)) {
    return new AdminDomain(
        rs.getString("AdminID"),
        rs.getString("UserID"),
        rs.getString("FirstName"),
        rs.getString("LastName"),
        rs.getString("Email"),
        rs.getString("PhoneNumber")
    );
}
                }
            }
        }
        return null; // invalid credentials
    }

    // =========================
    // REGISTER NEW ADMIN
    // =========================
    public boolean registerAdmin(String firstName, String lastName, String email, String phone, String password) throws SQLException {
        String userId = "U" + UUID.randomUUID().toString().substring(0, 8);
        String adminId = "A" + UUID.randomUUID().toString().substring(0, 8);
        String hashedPassword = hashPassword(password);

        String insertUser = "INSERT INTO UserAccount (UserID, Email, PasswordHash, Role) VALUES (?, ?, ?, 'ADMIN')";
        String insertAdmin = "INSERT INTO Admin (AdminID, UserID, FirstName, LastName, PhoneNumber) VALUES (?, ?, ?, ?, ?)";

        con.setAutoCommit(false);
        try (
            PreparedStatement psUser = con.prepareStatement(insertUser);
            PreparedStatement psAdmin = con.prepareStatement(insertAdmin)
        ) {
            // Insert into UserAccount
            psUser.setString(1, userId);
            psUser.setString(2, email);
            psUser.setString(3, hashedPassword);
            psUser.executeUpdate();

            // Insert into Admin
            psAdmin.setString(1, adminId);
            psAdmin.setString(2, userId);
            psAdmin.setString(3, firstName);
            psAdmin.setString(4, lastName);
            psAdmin.setString(5, phone);
            psAdmin.executeUpdate();

            con.commit();
            return true;

        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // =========================
    // CHECK PASSWORD (utility)
    // =========================
    public boolean checkPassword(String adminId, String password) throws SQLException {
        String sql = """
            SELECT u.PasswordHash
            FROM UserAccount u
            JOIN Admin a ON u.UserID = a.UserID
            WHERE a.AdminID = ?
        """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("PasswordHash");
                    return storedHash.equals(hashPassword(password));
                }
            }
        }
        return false;
    }
    
    
}
