package com.gestiondesabsences.gestiondesabsences.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AdminController {

    @FXML
    private StackPane contentPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        System.out.println("AdminController initialized");
        loadView("HomeView.fxml");
    }

    public void setAdminName(String name) {
        welcomeLabel.setText("Welcome, " + name);
    }

    private void loadView(String fxml) {
        try {
            String path = "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/" + fxml;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));

            AnchorPane view = loader.load();

            view.getStylesheets().add(
                    getClass().getResource("/com/gestiondesabsences/gestiondesabsences/Design/AdminDashboard.css")
                            .toExternalForm()
            );
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
            view.prefWidthProperty().bind(contentPane.widthProperty());
            view.prefHeightProperty().bind(contentPane.heightProperty());

            System.out.println("Loaded view: " + fxml);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sidebar buttons
    @FXML
    void handleHome() {
        loadView("HomeView.fxml");
    }

    @FXML
    void handleAbsences() {
        loadView("AbsencesView.fxml");
    }

    @FXML
    void handleStudents() {
        loadView("StudentsView.fxml");
    }

    @FXML
    void handleClasses() {
        loadView("ClassesView.fxml");
    }

    @FXML
    void handleModules() {
        loadView("ModulesView.fxml");
    }

    @FXML
    void handleMajors() {
        loadView("MajorsView.fxml");
    }

    @FXML
    void handleSchoolYears() {
        loadView("SchoolYearsView.fxml");
    }

    @FXML
    void handleLogout() {
        System.out.println("Logout");
    }
}
