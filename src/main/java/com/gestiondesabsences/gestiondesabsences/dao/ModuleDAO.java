package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleDAO {

    // Fetch all modules
    public List<Module> getAllModules() throws SQLException {
        List<Module> modules = new ArrayList<>();
        String sql = """
            SELECT m.id, m.module_name, m.major_id, m.school_year_id,
                   maj.name as major_name, sy.year_level
            FROM modules m
            JOIN majors maj ON m.major_id = maj.id
            JOIN school_years sy ON m.school_year_id = sy.id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Major major = new Major();
                major.setId(rs.getInt("major_id"));
                major.setName(rs.getString("major_name"));
                major.setSchoolYears(null);

                SchoolYear year = new SchoolYear();
                year.setId(rs.getInt("school_year_id"));
                year.setYearLevel(rs.getString("year_level"));
                year.setMajor(major);
                year.setStudents(null);
                year.setModules(null);
                year.setClasses(null);

                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setModuleName(rs.getString("module_name"));
                module.setMajor(major);
                module.setSchoolYear(year);

                modules.add(module);
            }
        }

        return modules;
    }

    // Add a module
    public void addModule(Module module) throws SQLException {
        String sql = "INSERT INTO modules (module_name, major_id, school_year_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, module.getModuleName());
            stmt.setInt(2, module.getMajor().getId());
            stmt.setInt(3, module.getSchoolYear().getId());
            stmt.executeUpdate();
        }
    }

    public void deleteModuleCascade(int moduleId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete absences related to this module
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM absences WHERE module_id = ?"
                )) {
                    stmt.setInt(1, moduleId);
                    stmt.executeUpdate();
                }

                // Delete the module itself
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM modules WHERE id = ?"
                )) {
                    stmt.setInt(1, moduleId);
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Get a single module by ID
    public Module getModuleById(int id) throws SQLException {
        String sql = """
        SELECT m.id, m.module_name, m.major_id, m.school_year_id,
               maj.name as major_name, sy.year_level
        FROM modules m
        JOIN majors maj ON m.major_id = maj.id
        JOIN school_years sy ON m.school_year_id = sy.id
        WHERE m.id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Major major = new Major();
                major.setId(rs.getInt("major_id"));
                major.setName(rs.getString("major_name"));
                major.setSchoolYears(null);

                SchoolYear year = new SchoolYear();
                year.setId(rs.getInt("school_year_id"));
                year.setYearLevel(rs.getString("year_level"));
                year.setMajor(major);
                year.setStudents(null);
                year.setModules(null);
                year.setClasses(null);

                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setModuleName(rs.getString("module_name"));
                module.setMajor(major);
                module.setSchoolYear(year);

                return module;
            }
        }
        return null;
    }


    public List<Module> getModulesByStudentId(int studentId) {
        List<Module> modules = new ArrayList<>();

        String sql = """
        SELECT m.id, m.module_name, m.major_id, m.school_year_id
        FROM modules m
        JOIN students s ON s.id = ?
        WHERE m.major_id = s.major_id
          AND m.school_year_id = s.school_year_id
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setModuleName(rs.getString("module_name"));

                // OPTIONAL: load Major & SchoolYear if you already do this elsewhere
                modules.add(module);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return modules;


    }

    public List<Module> getModulesByMajorAndYear(int majorId, int yearId) throws SQLException {
        List<Module> modules = new ArrayList<>();

        String sql = """
        SELECT m.id, m.module_name,
               maj.id AS major_id, maj.name AS major_name,
               sy.id AS year_id, sy.year_level
        FROM modules m
        JOIN majors maj ON m.major_id = maj.id
        JOIN school_years sy ON m.school_year_id = sy.id
        WHERE m.major_id = ?
          AND m.school_year_id = ?
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, majorId);
            stmt.setInt(2, yearId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setModuleName(rs.getString("module_name"));

                Major major = new Major();
                major.setId(rs.getInt("major_id"));
                major.setName(rs.getString("major_name"));
                module.setMajor(major);

                SchoolYear year = new SchoolYear();
                year.setId(rs.getInt("year_id"));
                year.setYearLevel(rs.getString("year_level"));
                module.setSchoolYear(year);

                modules.add(module);
            }
        }
        return modules;
    }

    public int countModules() {
        String sql = "SELECT COUNT(*) FROM modules";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
