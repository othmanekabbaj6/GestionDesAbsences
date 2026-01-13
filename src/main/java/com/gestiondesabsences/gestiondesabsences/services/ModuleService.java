package com.gestiondesabsences.gestiondesabsences.services;

import com.gestiondesabsences.gestiondesabsences.dao.ModuleDAO;
import com.gestiondesabsences.gestiondesabsences.models.Module;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModuleService {

    private final ModuleDAO moduleDAO;

    public ModuleService(ModuleDAO moduleDAO) {
        this.moduleDAO = moduleDAO;
    }

    public List<Module> getAllModules() {
        try {
            return moduleDAO.getAllModules();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Module> getModulesByStudentId(int studentId) {
        try {
            return moduleDAO.getModulesByStudentId(studentId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addModule(Module module) {
        if (module == null || module.getModuleName() == null) return false;
        try {
            moduleDAO.addModule(module);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteModuleCascade(int moduleId) {
        try {
            moduleDAO.deleteModuleCascade(moduleId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Module> getModulesByMajorAndYear(int majorId, int yearId) {
        try {
            return moduleDAO.getModulesByMajorAndYear(majorId, yearId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
