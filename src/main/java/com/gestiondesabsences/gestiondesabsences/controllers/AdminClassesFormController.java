package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.dao.SchoolYearDAO;
import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.services.ClassEntityService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class AdminClassesFormController {

    @FXML private TextField classNameField;
    @FXML private ComboBox<Major> majorCombo;
    @FXML private ComboBox<SchoolYear> yearCombo;
    @FXML private Button actionButton;

    private final ClassEntityService classService = new ClassEntityService();
    private SchoolYearDAO schoolYearDAO;
    private String mode;

    @FXML
    public void initialize() {
        schoolYearDAO = new SchoolYearDAO(); // no connection passed

        try {
            // Load all majors from DB
            List<Major> majors = schoolYearDAO.getAllMajors();
            majorCombo.getItems().addAll(majors);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load majors from database").showAndWait();
        }

        // When major changes, update school year combo
        majorCombo.setOnAction(e -> {
            Major selectedMajor = majorCombo.getValue();
            if (selectedMajor != null) {
                loadSchoolYears(selectedMajor.getId());
            } else {
                yearCombo.getItems().clear();
            }
        });
    }

    private void loadSchoolYears(int majorId) {
        try {
            List<SchoolYear> years = schoolYearDAO.getByMajor(majorId);
            ObservableList<SchoolYear> data = FXCollections.observableArrayList(years);
            yearCombo.setItems(data);
            yearCombo.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load school years for selected major").showAndWait();
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
        actionButton.setText("Add"); // Only Add
        clearForm();
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/ClassesView.fxml"
                    )
            );

            AnchorPane classesView = loader.load();
            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(classesView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAdd() {
        Major selectedMajor = majorCombo.getValue();
        SchoolYear selectedYear = yearCombo.getValue();
        String name = classNameField.getText();

        if (name == null || name.isBlank() || selectedMajor == null || selectedYear == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").showAndWait();
            return;
        }

        ClassEntity c = new ClassEntity();
        c.setClassName(name);
        c.setSchoolYear(selectedYear);

        // Check for duplicates
        if (!classService.addClass(c)) {
            new Alert(Alert.AlertType.WARNING,
                    "A class with the same name, major, and school year already exists!")
                    .showAndWait();
            return;
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Success");
        success.setHeaderText(null);
        success.setContentText("Class added successfully.");
        success.showAndWait();

        handleBack();
    }

    private void clearForm() {
        classNameField.clear();
        majorCombo.getSelectionModel().clearSelection();
        yearCombo.getItems().clear();
    }
}
