package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.StudentDAO;
import com.gestiondesabsences.gestiondesabsences.models.Student;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public List<Student> getAllStudents() {
        try {
            return studentDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addStudent(Student student) {
        try {
            studentDAO.add(student);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStudent(Student student) {
        try {
            studentDAO.update(student);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        try {
            studentDAO.delete(studentId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
