package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.SchoolYearDAO;
import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;

import java.util.List;

public class SchoolYearService {

    private final SchoolYearDAO schoolYearDAO;


    public SchoolYearService() {
        this.schoolYearDAO = new SchoolYearDAO();
    }

    public List<SchoolYear> getAllSchoolYears() {
        try {
            return schoolYearDAO.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addSchoolYear(SchoolYear schoolYear) {
        try {
            schoolYearDAO.add(schoolYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSchoolYear(int id) {
        try {
            schoolYearDAO.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSchoolYearCascade(int schoolYearId) {
        try {
            schoolYearDAO.deleteCascade(schoolYearId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Major> getAllMajors() {
        try {
            return schoolYearDAO.getAllMajors();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<SchoolYear> getSchoolYearsByMajor(int majorId) {
        try {
            return schoolYearDAO.getByMajor(majorId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPreviewDeleteMessage(int schoolYearId) {
        return schoolYearDAO.previewDeleteMessage(schoolYearId);
    }
}
