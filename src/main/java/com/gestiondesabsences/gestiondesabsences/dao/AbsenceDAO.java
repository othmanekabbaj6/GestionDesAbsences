package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.Absence;
import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbsenceDAO {

    // Fetch all absences with linked Student and Module
    public List<Absence> getAll() throws SQLException {
        List<Absence> absences = new ArrayList<>();

        String sql = """
            SELECT a.id, a.date,
                   s.id AS student_id, s.first_name, s.last_name, s.class_id, s.major_id, s.school_year_id,
                   m.id AS module_id, m.module_name, m.major_id AS module_major_id, m.school_year_id AS module_year_id
            FROM absences a
            JOIN students s ON a.student_id = s.id
            JOIN modules m ON a.module_id = m.id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            StudentDAO studentDAO = new StudentDAO();
            ModuleDAO moduleDAO = new ModuleDAO();

            while (rs.next()) {
                Student student = studentDAO.getStudentById(rs.getInt("student_id"));
                Module module = moduleDAO.getModuleById(rs.getInt("module_id"));

                Absence absence = new Absence();
                absence.setId(rs.getInt("id"));
                absence.setDate(rs.getDate("date").toLocalDate());
                absence.setStudent(student);
                absence.setModule(module);

                absences.add(absence);
            }
        }

        return absences;
    }

    // Add absence
    public void add(Absence absence) throws SQLException {
        String sql = "INSERT INTO absences (date, student_id, module_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(absence.getDate()));
            stmt.setInt(2, absence.getStudent().getId());
            stmt.setInt(3, absence.getModule().getId());
            stmt.executeUpdate();
        }
    }

    // Update absence
    public void update(Absence absence) throws SQLException {
        String sql = "UPDATE absences SET date=?, student_id=?, module_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(absence.getDate()));
            stmt.setInt(2, absence.getStudent().getId());
            stmt.setInt(3, absence.getModule().getId());
            stmt.setInt(4, absence.getId());
            stmt.executeUpdate();
        }
    }

    // Delete absence
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM absences WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Get absence by id
    public Absence getById(int id) throws SQLException {
        String sql = "SELECT * FROM absences WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Absence absence = new Absence();
                absence.setId(rs.getInt("id"));
                absence.setDate(rs.getDate("date").toLocalDate());

                StudentDAO studentDAO = new StudentDAO();
                ModuleDAO moduleDAO = new ModuleDAO();

                absence.setStudent(studentDAO.getStudentById(rs.getInt("student_id")));
                absence.setModule(moduleDAO.getModuleById(rs.getInt("module_id")));

                return absence;
            }
        }
        return null;
    }


    public Map<String, Integer> getAbsenceStatsByStudent(int studentId) throws SQLException {

        Map<String, Integer> stats = new LinkedHashMap<>();

        String sql = """
        SELECT m.module_name AS module_name, COUNT(a.id) AS absence_count
        FROM absences a
        JOIN modules m ON a.module_id = m.id
        WHERE a.student_id = ?
        GROUP BY m.module_name
        ORDER BY absence_count DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stats.put(
                        rs.getString("module_name"),
                        rs.getInt("absence_count")
                );
            }
        }
        return stats;
    }
}
