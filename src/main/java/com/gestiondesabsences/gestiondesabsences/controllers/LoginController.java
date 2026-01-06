package com.gestiondesabsences.gestiondesabsences.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class LoginController {


    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    void handleLogin(ActionEvent event) throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.equals("othmane") && pass.equals("admin123")) {
            switchScene(event, "admin-view.fxml", "Admin Dashboard");

        } else if (user.equals("student") && pass.equals("student123")) {
            switchScene(event, "student-view.fxml", "Student Portal");

        }else if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Missing Information", "Please fill in all fields.");
                return;
        } else {
            showAlert(
                    "Login Failed",
                    "Invalid username or password.\nPlease try again."
            );
            passwordField.clear();
            usernameField.clear();
        }
    }

    private void switchScene(ActionEvent event, String fxmlFileName, String title) throws IOException {

        String path = "/com/gestiondesabsences/gestiondesabsences/Views/" + fxmlFileName;
        var resource = getClass().getResource(path);

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Object controller = loader.getController();
        if (controller instanceof AdminController adminController) {
            adminController.setAdminName(usernameField.getText());
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.centerOnScreen();
        stage.show();
    }

}
