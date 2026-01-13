package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.services.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML private Label studentsCountLabel;
    @FXML private Label classesCountLabel;
    @FXML private Label majorsCountLabel;
    @FXML private Label modulesCountLabel;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        studentsCountLabel.setText(
                String.valueOf(dashboardService.getStudentsCount())
        );

        classesCountLabel.setText(
                String.valueOf(dashboardService.getClassesCount())
        );

        majorsCountLabel.setText(
                String.valueOf(dashboardService.getMajorsCount())
        );

        modulesCountLabel.setText(
                String.valueOf(dashboardService.getModulesCount())
        );
    }
}
