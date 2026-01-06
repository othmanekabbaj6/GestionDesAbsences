package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.services.SchoolYearService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminSchoolYearsFormController {

    @FXML private ComboBox<Major> majorCombo;
    @FXML private ComboBox<String> yearCombo;
    @FXML private Button actionButton;

    @Setter private SchoolYearService schoolYearService;
    @Setter private AdminSchoolYearsController parentController;

    private final List<String> allYears = Arrays.asList(
            "1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year"
    );

    @FXML
    public void initialize() {
        yearCombo.getItems().addAll(allYears);

        // Filter year options based on selected major
        majorCombo.setOnAction(event -> filterYears());
    }

    /**
     * Load majors dynamically AFTER setting the service
     */
    public void loadMajors() {
        if (schoolYearService != null) {
            List<Major> majors = schoolYearService.getAllMajors();
            if (majors != null) {
                majorCombo.getItems().clear();
                majorCombo.getItems().addAll(majors);
            }
        }
    }

    /**
     * Only show years that are not already used by the selected major
     */
    private void filterYears() {
        Major selectedMajor = majorCombo.getValue();
        if (selectedMajor == null) return;

        List<SchoolYear> existingYears = schoolYearService.getSchoolYearsByMajor(selectedMajor.getId());
        List<String> availableYears = new ArrayList<>(allYears);

        if (existingYears != null) {
            List<String> usedYears = existingYears.stream()
                    .map(SchoolYear::getYearLevel)
                    .collect(Collectors.toList());
            availableYears.removeAll(usedYears);
        }

        yearCombo.getItems().clear();
        yearCombo.getItems().addAll(availableYears);
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/SchoolYearsView.fxml")
            );
            AnchorPane schoolYearsView = loader.load();

            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            if (contentPane != null) contentPane.getChildren().setAll(schoolYearsView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAdd() {
        Major major = majorCombo.getValue();
        String year = yearCombo.getValue();

        if (major == null || year == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select both a major and a year!");
            return;
        }

        // Double-check year uniqueness
        List<SchoolYear> existingYears = schoolYearService.getSchoolYearsByMajor(major.getId());
        if (existingYears != null && existingYears.stream().anyMatch(sy -> sy.getYearLevel().equals(year))) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Year", "This year already exists for the selected major.");
            filterYears();
            return;
        }

        // Add new school year
        SchoolYear sy = new SchoolYear();
        sy.setMajor(major);
        sy.setYearLevel(year);
        schoolYearService.addSchoolYear(sy);

        showAlert(Alert.AlertType.INFORMATION, "Success", "School Year added successfully!");

        if (parentController != null) parentController.loadSchoolYears();

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
