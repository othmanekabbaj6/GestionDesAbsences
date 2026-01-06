package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassEntityDAO {

    // Fetch all classes with their school years and majors
    public List<ClassEntity> getAll() {
        List<ClassEntity> classes = new ArrayList<>();
        String query = "SELECT c.id AS class_id, c.class_name, " +
                "sy.id AS year_id, sy.year_level, " +
                "m.id AS major_id, m.name AS major_name " +
                "FROM class_entities c " +
                "JOIN school_years sy ON c.school_year_id = sy.id " +
                "JOIN majors m ON sy.major_id = m.id " +
                "ORDER BY c.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Major major = new Major(rs.getInt("major_id"), rs.getString("major_name"), null);
                SchoolYear year = new SchoolYear(rs.getInt("year_id"), rs.getString("year_level"), major, null, null, null);
                ClassEntity c = new ClassEntity(rs.getInt("class_id"), rs.getString("class_name"), year);
                classes.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classes;
    }

    // Add a new class, returns false if duplicate exists
    public boolean add(ClassEntity c) {
        if (existsClass(c.getClassName(), c.getSchoolYear().getMajor().getId(), c.getSchoolYear().getId())) {
            return false; // Duplicate
        }

        String query = "INSERT INTO class_entities (class_name, school_year_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, c.getClassName());
            ps.setInt(2, c.getSchoolYear().getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if class with same name, major, and school year exists
    public boolean existsClass(String name, int majorId, int yearId) {
        String sql = "SELECT COUNT(*) FROM class_entities c " +
                "JOIN school_years sy ON c.school_year_id = sy.id " +
                "WHERE c.class_name = ? AND sy.id = ? AND sy.major_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, yearId);
            ps.setInt(3, majorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get students for preview before deleting a class
    public List<Student> getStudentsInClass(int classId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT id, first_name, last_name FROM students WHERE class_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student s = new Student();
                s.setId(rs.getInt("id"));
                s.setFirstName(rs.getString("first_name"));
                s.setLastName(rs.getString("last_name"));
                students.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // Cascade delete: absences -> students -> class
    public boolean cascadeDeleteClass(int classId) {
        String deleteAbsences = "DELETE FROM absences WHERE student_id IN (SELECT id FROM students WHERE class_id = ?)";
        String deleteStudents = "DELETE FROM students WHERE class_id = ?";
        String deleteClass = "DELETE FROM class_entities WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psAbs = conn.prepareStatement(deleteAbsences);
                 PreparedStatement psStu = conn.prepareStatement(deleteStudents);
                 PreparedStatement psCls = conn.prepareStatement(deleteClass)) {

                // 1️⃣ Delete absences
                psAbs.setInt(1, classId);
                psAbs.executeUpdate();

                // 2️⃣ Delete students
                psStu.setInt(1, classId);
                psStu.executeUpdate();

                // 3️⃣ Delete class
                psCls.setInt(1, classId);
                psCls.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch classes by school year
    public List<ClassEntity> getClassesBySchoolYear(int schoolYearId) throws SQLException {
        List<ClassEntity> classes = new ArrayList<>();

        String sql = "SELECT c.id, c.class_name, c.school_year_id FROM class_entities c WHERE c.school_year_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, schoolYearId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClassEntity cls = new ClassEntity();
                    cls.setId(rs.getInt("id"));
                    cls.setClassName(rs.getString("class_name"));

                    SchoolYear year = new SchoolYear();
                    year.setId(rs.getInt("school_year_id"));
                    cls.setSchoolYear(year);

                    classes.add(cls);
                }
            }
        }

        return classes;
    }
}
