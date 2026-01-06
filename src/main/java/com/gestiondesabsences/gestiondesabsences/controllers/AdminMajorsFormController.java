package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.services.MajorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AdminMajorsFormController {

    @FXML private TextField majorNameField;
    @FXML private Button actionButton;

    private final MajorService majorService = new MajorService();

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/MajorsView.fxml")
            );
            AnchorPane majorsView = loader.load();
            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(majorsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAdd() {
        String name = majorNameField.getText();

        if (name == null || name.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Major name is required");
            return;
        }

        Major major = new Major();
        major.setName(name);

        boolean success = majorService.addMajor(major);

        if (!success) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Major", "A major with this name already exists!");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Major added successfully!");
        handleBack();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
