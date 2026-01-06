package com.gestiondesabsences.gestiondesabsences.dao;

import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Fetch all students with related Major, SchoolYear, Class
    public List<Student> getAll() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = """
            SELECT s.id, s.first_name, s.last_name,
                   c.id AS class_id, c.class_name, c.school_year_id,
                   m.id AS major_id, m.name AS major_name,
                   sy.id AS year_id, sy.year_level
            FROM students s
            JOIN class_entities c ON s.class_id = c.id
            JOIN majors m ON s.major_id = m.id
            JOIN school_years sy ON s.school_year_id = sy.id
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Major major = new Major(rs.getInt("major_id"), rs.getString("major_name"), null);

                SchoolYear year = new SchoolYear();
                year.setId(rs.getInt("year_id"));
                year.setYearLevel(rs.getString("year_level"));
                year.setMajor(major);

                ClassEntity classEntity = new ClassEntity();
                classEntity.setId(rs.getInt("class_id"));
                classEntity.setClassName(rs.getString("class_name"));
                classEntity.setSchoolYear(year);

                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setClassEntity(classEntity); // store ClassEntity object
                student.setMajor(major);
                student.setSchoolYear(year);
                student.setAbsences(new ArrayList<>());

                students.add(student);
            }
        }

        return students;
    }

    // Add new student
    public void add(Student student) throws SQLException {
        String sql = """
            INSERT INTO students (first_name, last_name, class_id, major_id, school_year_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setInt(3, student.getClassEntity().getId());
            stmt.setInt(4, student.getMajor().getId());
            stmt.setInt(5, student.getSchoolYear().getId());
            stmt.executeUpdate();
        }
    }

    // Update student
    public void update(Student student) throws SQLException {
        String sql = """
            UPDATE students
            SET first_name = ?, last_name = ?, class_id = ?, major_id = ?, school_year_id = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setInt(3, student.getClassEntity().getId());
            stmt.setInt(4, student.getMajor().getId());
            stmt.setInt(5, student.getSchoolYear().getId());
            stmt.setInt(6, student.getId());
            stmt.executeUpdate();
        }
    }

    // Delete student + cascade absences
    public void delete(int studentId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete absences
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM absences WHERE student_id = ?")) {
                    stmt.setInt(1, studentId);
                    stmt.executeUpdate();
                }

                // Delete student
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM students WHERE id = ?")) {
                    stmt.setInt(1, studentId);
                    stmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Get a single student by ID
    public Student getStudentById(int id) throws SQLException {
        String sql = """
        SELECT s.id, s.first_name, s.last_name,
               c.id AS class_id, c.class_name, c.school_year_id,
               m.id AS major_id, m.name AS major_name,
               sy.id AS year_id, sy.year_level
        FROM students s
        JOIN class_entities c ON s.class_id = c.id
        JOIN majors m ON s.major_id = m.id
        JOIN school_years sy ON s.school_year_id = sy.id
        WHERE s.id = ? """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Major major = new Major(rs.getInt("major_id"), rs.getString("major_name"), null);

                SchoolYear year = new SchoolYear();
                year.setId(rs.getInt("year_id"));
                year.setYearLevel(rs.getString("year_level"));
                year.setMajor(major);

                ClassEntity classEntity = new ClassEntity();
                classEntity.setId(rs.getInt("class_id"));
                classEntity.setClassName(rs.getString("class_name"));
                classEntity.setSchoolYear(year);

                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setClassEntity(classEntity);
                student.setMajor(major);
                student.setSchoolYear(year);
                student.setAbsences(null); // optional, can load separately

                return student;
            }
        }
        return null;
    }
}
