package com.gestiondesabsences.gestiondesabsences.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminHomeController {
    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalClassesLabel;

    @FXML
    private Label totalMajorsLabel;

    @FXML
    private Label totalModulesLabel;

    @FXML
    public void initialize() {
        loadDashboardStats();
    }

    private void loadDashboardStats() {
        // Temporary values (UI wiring phase)
        totalStudentsLabel.setText("120");
        totalClassesLabel.setText("8");
        totalMajorsLabel.setText("5");
        totalModulesLabel.setText("3");
    }


}
