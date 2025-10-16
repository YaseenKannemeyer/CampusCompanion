/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycput.ac.za.studenttimetable.dao;


/**
 *
 * @author mogamatyaseenkannemeyer
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mycput.ac.za.studenttimetable.connection.DBConnection;

public class CrudHelper {

    // Load data from table
    public static List<String[]> loadTable(String tableName, String[] columns) throws SQLException {
        List<String[]> data = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = DBConnection.derbyConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    row[i] = rs.getString(columns[i]);
                }
                data.add(row);
            }
        }
        return data;
    }

    // Insert a new record
    public static void insertRecord(String tableName, String[] columns, String[] values) throws SQLException {
        if (columns.length != values.length) throw new IllegalArgumentException("Columns and values length mismatch");

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        sql.append(String.join(", ", columns));
        sql.append(") VALUES (");
        sql.append("?,".repeat(values.length));
        sql.setLength(sql.length() - 1); // remove last comma
        sql.append(")");

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            ps.executeUpdate();
        }
    }

    // Update an existing record by key
    public static void updateRecord(String tableName, String keyColumn, String keyValue, String[] columns, String[] values) throws SQLException {
        if (columns.length != values.length) throw new IllegalArgumentException("Columns and values length mismatch");

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]).append("=?");
            if (i < columns.length - 1) sql.append(", ");
        }
        sql.append(" WHERE ").append(keyColumn).append("=?");

        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < values.length; i++) ps.setString(i + 1, values[i]);
            ps.setString(values.length + 1, keyValue);
            ps.executeUpdate();
        }
    }

    // Delete a record by key
    public static void deleteRecord(String tableName, String keyColumn, String keyValue) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + keyColumn + "=?";
        try (Connection conn = DBConnection.derbyConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, keyValue);
            ps.executeUpdate();
        }
    }
    
    public static String getLastUserID() throws SQLException {
    String sql = "SELECT UserID FROM UserAccount ORDER BY UserID DESC LIMIT 1";
    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        if (rs.next()) return rs.getString("UserID");
    }
    return "U000";
}

}