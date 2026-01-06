package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.ClassEntityDAO;
import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Student;

import java.util.List;

public class ClassEntityService {

    private final ClassEntityDAO classDAO = new ClassEntityDAO();

    // Get all classes
    public List<ClassEntity> getAllClasses() {
        return classDAO.getAll();
    }

    // Add a new class
    public boolean addClass(ClassEntity c) {
        return classDAO.add(c);
    }

    // Get students of a class (for preview before deletion)
    public List<Student> getStudentsInClass(int classId) {
        return classDAO.getStudentsInClass(classId);
    }

    // Cascade delete a class (delete absences -> students -> class)
    public boolean deleteClassCascade(int classId) {
        return classDAO.cascadeDeleteClass(classId);
    }

    public List<ClassEntity> getClassesBySchoolYear(int schoolYearId) {
        try {
            return classDAO.getClassesBySchoolYear(schoolYearId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
