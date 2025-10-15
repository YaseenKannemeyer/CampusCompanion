package mycput.ac.za.studenttimetable.dao;

import mycput.ac.za.studenttimetable.connection.DBConnection;
import mycput.ac.za.studenttimetable.domain.TimetableDomain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

  public static List<TimetableDomain> getTimeTableForGroup(String groupID) {
    List<TimetableDomain> timetable = new ArrayList<>();

    String query = """
        SELECT tt.*, s.SubjectName, 
               l.FirstName || ' ' || l.LastName AS LecturerName,
               r.RoomType
        FROM TimeTable tt
        JOIN Subject s ON tt.SubjectCode = s.SubjectCode
        JOIN Lecturer l ON tt.LecturerID = l.LecturerID
        JOIN LectureRoom r ON tt.RoomID = r.RoomID
        WHERE tt.GroupID = ?
        ORDER BY CASE tt.DayOfWeek
                     WHEN 'MONDAY' THEN 1
                     WHEN 'TUESDAY' THEN 2
                     WHEN 'WEDNESDAY' THEN 3
                     WHEN 'THURSDAY' THEN 4
                     WHEN 'FRIDAY' THEN 5
                     ELSE 6
                 END,
                 tt.StartTime
    """;

    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

        pstmt.setString(1, groupID);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            TimetableDomain entry = new TimetableDomain();
            entry.setSubjectCode(rs.getString("SubjectCode"));
            entry.setSubjectName(rs.getString("SubjectName"));
            entry.setLecturerID(rs.getString("LecturerID"));
            entry.setLecturerName(rs.getString("LecturerName"));
            entry.setRoomID(rs.getString("RoomID"));
            entry.setRoomType(rs.getString("RoomType"));
            entry.setClassType(rs.getString("ClassType"));
            entry.setGroupID(rs.getString("GroupID"));
            entry.setDayOfWeek(rs.getString("DayOfWeek"));
            entry.setStartTime(rs.getTime("StartTime"));
            entry.setEndTime(rs.getTime("EndTime"));

            timetable.add(entry);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return timetable;
}

    
    public static void deleteEntry(String groupID, String dayOfWeek, Time startTime) {
    String sql = "DELETE FROM TimeTable WHERE GroupID=? AND DayOfWeek=? AND StartTime=?";
    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, groupID);
        pstmt.setString(2, dayOfWeek);
        pstmt.setTime(3, startTime);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    public static void deleteAllForGroup(String groupID) {
    String sql = "DELETE FROM TimeTable WHERE GroupID=?";
    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, groupID);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public static void saveOrUpdateEntry(TimetableDomain entry) {
    StringBuilder sql = new StringBuilder("INSERT INTO TimeTable(GroupID, DayOfWeek, StartTime");
    StringBuilder values = new StringBuilder("VALUES (?, ?, ?)");
    StringBuilder updates = new StringBuilder("ON DUPLICATE KEY UPDATE ");

    List<Object> params = new ArrayList<>();
    params.add(entry.getGroupID());
    params.add(entry.getDayOfWeek());
    params.add(entry.getStartTime());

    if (entry.getSubjectCode() != null) {
        sql.append(", SubjectCode");
        values.append(", ?");
        updates.append("SubjectCode=VALUES(SubjectCode),");
        params.add(entry.getSubjectCode());
    }
    if (entry.getLecturerID() != null) {
        sql.append(", LecturerID");
        values.append(", ?");
        updates.append("LecturerID=VALUES(LecturerID),");
        params.add(entry.getLecturerID());
    }
    if (entry.getRoomID() != null) {
        sql.append(", RoomID");
        values.append(", ?");
        updates.append("RoomID=VALUES(RoomID),");
        params.add(entry.getRoomID());
    }
    if (entry.getClassType() != null) {
        sql.append(", ClassType");
        values.append(", ?");
        updates.append("ClassType=VALUES(ClassType),");
        params.add(entry.getClassType());
    }
    if (entry.getEndTime() != null) {
        sql.append(", EndTime");
        values.append(", ?");
        updates.append("EndTime=VALUES(EndTime),");
        params.add(entry.getEndTime());
    }

    // Remove trailing comma
    if (updates.charAt(updates.length() - 1) == ',') updates.deleteCharAt(updates.length() - 1);

    sql.append(") ").append(values).append(" ").append(updates);

    try (Connection conn = DBConnection.derbyConnection();
         PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }

        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}




    }

