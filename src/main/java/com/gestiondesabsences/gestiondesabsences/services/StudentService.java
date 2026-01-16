package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.StudentDAO;
import com.gestiondesabsences.gestiondesabsences.models.Absence;
import com.gestiondesabsences.gestiondesabsences.models.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    // Get all students
    public List<Student> getAllStudents() {
        try {
            return studentDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add a student
    public boolean addStudent(Student student) {
        try {
            studentDAO.add(student);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update a student
    public boolean updateStudent(Student student) {
        try {
            studentDAO.update(student);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a student by ID
    public boolean deleteStudent(int studentId) {
        try {
            studentDAO.delete(studentId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get a student by email (for login)
    public Student getStudentByEmail(String email) {
        try {
            return studentDAO.getStudentByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Student searchStudentsByName(String firstName, String lastName) {
        try {
            return studentDAO.getStudentByName(firstName, lastName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search students", e);
        }
    }


}
