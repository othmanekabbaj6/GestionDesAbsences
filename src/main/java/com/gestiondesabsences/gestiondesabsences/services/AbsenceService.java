package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.AbsenceDAO;
import com.gestiondesabsences.gestiondesabsences.models.Absence;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AbsenceService {

    private final AbsenceDAO absenceDAO;

    public AbsenceService() {
        this.absenceDAO = new AbsenceDAO();
    }

    public List<Absence> getAll() {
        try {
            return absenceDAO.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean add(Absence absence) {
        try {
            absenceDAO.add(absence);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Absence absence) {
        try {
            absenceDAO.update(absence);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            absenceDAO.delete(id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Absence getById(int id) {
        try {
            return absenceDAO.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Map<String, Integer> getStudentAbsenceStats(int studentId) {
        try {
            return absenceDAO.getAbsenceStatsByStudent(studentId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load absence statistics", e);
        }
    }
}
