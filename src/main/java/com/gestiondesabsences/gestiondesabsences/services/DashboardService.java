package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.*;

public class DashboardService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ClassEntityDAO classEntityDAO = new ClassEntityDAO();
    private final MajorDAO majorDAO = new MajorDAO();
    private final ModuleDAO moduleDAO = new ModuleDAO();

    public int getStudentsCount() {
        return studentDAO.countStudents();
    }

    public int getClassesCount() {
        return classEntityDAO.countClasses();
    }

    public int getMajorsCount() {
        return majorDAO.countMajors();
    }

    public int getModulesCount() {
        return moduleDAO.countModules();
    }
}
