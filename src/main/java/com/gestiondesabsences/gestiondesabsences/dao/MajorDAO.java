package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {


    public List<Major> findAll() {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT id, name FROM majors ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Major major = new Major();
                major.setId(rs.getInt("id"));
                major.setName(rs.getString("name"));
                major.setSchoolYears(null); // lazy load
                majors.add(major);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return majors;
    }


    public boolean exists(String name) {
        String sql = "SELECT COUNT(*) FROM majors WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean insert(Major major) {
        if (exists(major.getName())) return false; // Duplicate

        String sql = "INSERT INTO majors (name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, major.getName());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public String previewDeleteMessage(int majorId) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {

            String majorName = "X";
            String majorSql = "SELECT name FROM majors WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(majorSql)) {
                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) majorName = rs.getString("name");
            }

            sb.append("Dependent rows for Major: ").append(majorName).append(" ===\n");

            // School years
            String sySql = "SELECT year_level FROM school_years WHERE major_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sySql)) {
                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                sb.append("SchoolYears:\n");
                while (rs.next()) sb.append(" - ").append(rs.getString("year_level")).append("\n");
            }

            // Modules
            String modSql = "SELECT module_name FROM modules WHERE major_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(modSql)) {
                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                sb.append("Modules:\n");
                while (rs.next()) sb.append(" - ").append(rs.getString("module_name")).append("\n");
            }

            // Classes
            String classSql = "SELECT ce.class_name, sy.year_level " +
                    "FROM class_entities ce " +
                    "JOIN school_years sy ON ce.school_year_id = sy.id " +
                    "WHERE sy.major_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(classSql)) {
                stmt.setInt(1, majorId);
                ResultSet rs = stmt.executeQuery();
                sb.append("Classes:\n");
                while (rs.next()) {
                    sb.append(" - ").append(rs.getString("class_name"))
                            .append(" (Year: ").append(rs.getString("year_level")).append(")\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sb.append("Error retrieving dependent rows.\n");
        }

        return sb.toString();
    }


    public void deleteCascade(int majorId) {
        String deleteAbsences = "DELETE FROM absences WHERE student_id IN (SELECT id FROM students WHERE major_id = ?) "
                + "OR module_id IN (SELECT id FROM modules WHERE major_id = ?)";
        String deleteStudents = "DELETE FROM students WHERE major_id = ?";
        String deleteModules = "DELETE FROM modules WHERE major_id = ?";
        String deleteClasses = "DELETE FROM class_entities WHERE school_year_id IN (SELECT id FROM school_years WHERE major_id = ?)";
        String deleteSchoolYears = "DELETE FROM school_years WHERE major_id = ?";
        String deleteMajor = "DELETE FROM majors WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement psAbsences = conn.prepareStatement(deleteAbsences);
                    PreparedStatement psStudents = conn.prepareStatement(deleteStudents);
                    PreparedStatement psModules = conn.prepareStatement(deleteModules);
                    PreparedStatement psClasses = conn.prepareStatement(deleteClasses);
                    PreparedStatement psSchoolYears = conn.prepareStatement(deleteSchoolYears);
                    PreparedStatement psMajor = conn.prepareStatement(deleteMajor)
            ) {
                psAbsences.setInt(1, majorId);
                psAbsences.setInt(2, majorId);
                psAbsences.executeUpdate();

                psStudents.setInt(1, majorId);
                psStudents.executeUpdate();

                psModules.setInt(1, majorId);
                psModules.executeUpdate();

                psClasses.setInt(1, majorId);
                psClasses.executeUpdate();

                psSchoolYears.setInt(1, majorId);
                psSchoolYears.executeUpdate();

                psMajor.setInt(1, majorId);
                psMajor.executeUpdate();

                conn.commit();
                System.out.println("Major and all dependent rows deleted successfully.");

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Deletion failed, transaction rolled back.");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
