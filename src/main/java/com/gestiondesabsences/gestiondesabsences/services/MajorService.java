package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.MajorDAO;
import com.gestiondesabsences.gestiondesabsences.models.Major;

import java.util.List;

public class MajorService {

    private final MajorDAO majorDAO = new MajorDAO();

    public List<Major> getAllMajors() {
        return majorDAO.findAll();
    }


    public boolean addMajor(Major major) {
        return majorDAO.insert(major);
    }

    public void deleteMajor(int id) {
        majorDAO.deleteCascade(id);
    }

    public String getPreviewDeleteMessage(int majorId) {
        return majorDAO.previewDeleteMessage(majorId);
    }
}
