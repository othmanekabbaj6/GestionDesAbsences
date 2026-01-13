package com.gestiondesabsences.gestiondesabsences.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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

            // Add CSS
            view.getStylesheets().add(
                    getClass().getResource("/com/gestiondesabsences/gestiondesabsences/Design/AdminDashboard.css")
                            .toExternalForm()
            );

            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);

            // Bind size
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

    // Fixed logout
    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("Any unsaved changes will be lost.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Load login view
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/gestiondesabsences/gestiondesabsences/Views/login-view.fxml"
                ));
                Parent loginRoot = loader.load();

                // Get the current stage from any node in the scene (contentPane)
                Stage stage = (Stage) contentPane.getScene().getWindow();

                // Set the new scene
                Scene scene = new Scene(loginRoot);
                stage.setScene(scene);
                stage.setTitle("Login");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Cannot load login view.");
                errorAlert.showAndWait();
            }
        }
    }
}
