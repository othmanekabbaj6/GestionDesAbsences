package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchoolYearDAO {


    public List<SchoolYear> getAll() throws SQLException {
        List<SchoolYear> schoolYears = new ArrayList<>();

        String sql = """
            SELECT sy.id AS sy_id, sy.year_level,
                   m.id AS m_id, m.name AS m_name
            FROM school_years sy
            JOIN majors m ON sy.major_id = m.id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Major major = new Major(
                        rs.getInt("m_id"),
                        rs.getString("m_name"),
                        null
                );

                SchoolYear sy = new SchoolYear();
                sy.setId(rs.getInt("sy_id"));
                sy.setYearLevel(rs.getString("year_level"));
                sy.setMajor(major);

                schoolYears.add(sy);
            }
        }

        return schoolYears;
    }


    public void add(SchoolYear schoolYear) throws SQLException {
        String sql = "INSERT INTO school_years (major_id, year_level) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, schoolYear.getMajor().getId());
            stmt.setString(2, schoolYear.getYearLevel());
            stmt.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM school_years WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }


    public List<Major> getAllMajors() throws SQLException {
        List<Major> majors = new ArrayList<>();

        String sql = "SELECT id, name FROM majors";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                majors.add(new Major(
                        rs.getInt("id"),
                        rs.getString("name"),
                        null
                ));
            }
        }

        return majors;
    }


    public List<SchoolYear> getByMajor(int majorId) throws SQLException {
        List<SchoolYear> schoolYears = new ArrayList<>();

        String sql = "SELECT id, year_level FROM school_years WHERE major_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, majorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SchoolYear sy = new SchoolYear();
                    sy.setId(rs.getInt("id"));
                    sy.setYearLevel(rs.getString("year_level"));
                    sy.setMajor(new Major(majorId, null, null));
                    schoolYears.add(sy);
                }
            }
        }

        return schoolYears;
    }


    public String previewDeleteMessage(int schoolYearId) {
        StringBuilder sb = new StringBuilder();

        try (Connection conn = DatabaseConnection.getConnection()) {

            String yearLevel = "X";
            int majorId = -1;

            String sySql = "SELECT year_level, major_id FROM school_years WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sySql)) {
                stmt.setInt(1, schoolYearId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    yearLevel = rs.getString("year_level");
                    majorId = rs.getInt("major_id");
                }
            }

            String majorName = "Unknown";
            String majorSql = "SELECT name FROM majors WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(majorSql)) {
                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) majorName = rs.getString("name");
            }

            sb.append("Deleting School Year: ")
                    .append(yearLevel)
                    .append(" (Major: ")
                    .append(majorName)
                    .append(")\n\n");

            appendList(sb, conn,
                    "Students:\n",
                    "SELECT first_name, last_name FROM students WHERE school_year_id = ?",
                    schoolYearId,
                    rs -> " - " + rs.getString("first_name") + " " + rs.getString("last_name"));

            appendList(sb, conn,
                    "Classes:\n",
                    "SELECT class_name FROM class_entities WHERE school_year_id = ?",
                    schoolYearId,
                    rs -> " - " + rs.getString("class_name"));

            appendList(sb, conn,
                    "Modules:\n",
                    "SELECT module_name FROM modules WHERE school_year_id = ?",
                    schoolYearId,
                    rs -> " - " + rs.getString("module_name"));

        } catch (SQLException e) {
            e.printStackTrace();
            sb.append("Error retrieving dependent rows.\n");
        }

        return sb.toString();
    }


    public void deleteCascade(int schoolYearId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                execute(conn,
                        "DELETE FROM absences WHERE student_id IN (SELECT id FROM students WHERE school_year_id = ?)",
                        schoolYearId);

                execute(conn,
                        "DELETE FROM students WHERE school_year_id = ?",
                        schoolYearId);

                execute(conn,
                        "DELETE FROM modules WHERE school_year_id = ?",
                        schoolYearId);

                execute(conn,
                        "DELETE FROM class_entities WHERE school_year_id = ?",
                        schoolYearId);

                execute(conn,
                        "DELETE FROM school_years WHERE id = ?",
                        schoolYearId);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }


    private void execute(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private interface RowFormatter {
        String format(ResultSet rs) throws SQLException;
    }

    private void appendList(StringBuilder sb, Connection conn, String title,
                            String sql, int id, RowFormatter formatter) throws SQLException {
        sb.append(title);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) sb.append(formatter.format(rs)).append("\n");
        }
    }
}
