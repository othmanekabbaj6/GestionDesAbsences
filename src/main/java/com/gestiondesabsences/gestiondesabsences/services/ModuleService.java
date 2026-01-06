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

    /**
     * Fetch all modules from the database.
     * Returns an empty list if an error occurs.
     */
    public List<Module> getAllModules() {
        try {
            List<Module> modules = moduleDAO.getAllModules();
            return modules != null ? modules : new ArrayList<>();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching modules from the database.");
            return new ArrayList<>();
        }
    }

    /**
     * Add a new module to the database.
     * Returns true if successful, false otherwise.
     */
    public boolean addModule(Module module) {
        if (module == null || module.getModuleName() == null || module.getMajor() == null || module.getSchoolYear() == null) {
            System.err.println("Invalid module data. Cannot add to database.");
            return false;
        }
        try {
            moduleDAO.addModule(module);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to add module: " + module.getModuleName());
            return false;
        }
    }

    /**
     * Delete a module by its ID.
     * Returns true if successful, false otherwise.
     */
    public boolean deleteModuleCascade(int moduleId) {
        try {
            moduleDAO.deleteModuleCascade(moduleId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Module> getModulesByStudentId(int studentId) {
        return moduleDAO.getModulesByStudentId(studentId);
    }

}
