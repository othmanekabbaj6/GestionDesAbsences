package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.services.ModuleService;
import com.gestiondesabsences.gestiondesabsences.dao.MajorDAO;
import com.gestiondesabsences.gestiondesabsences.dao.SchoolYearDAO;
import com.gestiondesabsences.gestiondesabsences.dao.ModuleDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class AdminModulesFormController {

    @FXML private TextField moduleNameField;
    @FXML private ComboBox<Major> majorCombo;
    @FXML private ComboBox<SchoolYear> yearCombo;
    @FXML private Button actionButton;

    private ModuleService moduleService;
    private MajorDAO majorDAO;
    private SchoolYearDAO schoolYearDAO;

    private ObservableList<Major> majorList = FXCollections.observableArrayList();
    private ObservableList<SchoolYear> yearList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Instantiate DAOs using no-arg constructors
        moduleService = new ModuleService(new ModuleDAO());
        majorDAO = new MajorDAO();
        schoolYearDAO = new SchoolYearDAO();

        // Load majors from DB
        try {
            List<Major> majorsFromDb = majorDAO.findAll();
            majorList.setAll(majorsFromDb);
            majorCombo.setItems(majorList);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database Error", "Failed to load majors from the database.");
        }

        // Display major name in ComboBox
        majorCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Major item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        majorCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Major item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });

        // When a major is selected, load school years for that major
        majorCombo.setOnAction(e -> {
            Major selectedMajor = majorCombo.getValue();
            if (selectedMajor != null) {
                try {
                    List<SchoolYear> years = schoolYearDAO.getByMajor(selectedMajor.getId());
                    yearList.setAll(years);
                    yearCombo.setItems(yearList);

                    // Display yearLevel in ComboBox
                    yearCombo.setCellFactory(param -> new ListCell<>() {
                        @Override
                        protected void updateItem(SchoolYear item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? "" : item.getYearLevel());
                        }
                    });
                    yearCombo.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(SchoolYear item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? "" : item.getYearLevel());
                        }
                    });

                    yearCombo.getSelectionModel().clearSelection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Database Error", "Failed to load school years for the selected major.");
                }
            }
        });
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/ModulesView.fxml"
                    )
            );
            AnchorPane modulesView = loader.load();
            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(modulesView);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation Error", "Cannot load modules view.");
        }
    }

    @FXML
    void handleAdd() {
        String moduleName = moduleNameField.getText();
        Major selectedMajor = majorCombo.getValue();
        SchoolYear selectedYear = yearCombo.getValue();

        if (moduleName.isEmpty() || selectedMajor == null || selectedYear == null) {
            showWarning("Missing Data", "Please fill all fields before adding a module.");
            return;
        }

        try {
            Module module = new Module();
            module.setModuleName(moduleName);
            module.setMajor(selectedMajor);
            module.setSchoolYear(selectedYear);

            boolean success = moduleService.addModule(module);
            if (success) {
                showInfo("Success", "Module added successfully!");

                // Clear form
                moduleNameField.clear();
                majorCombo.getSelectionModel().clearSelection();
                yearCombo.getItems().clear();

                // Go back to table view
                handleBack();
            } else {
                showError("Error", "Failed to add module.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Exception", "An error occurred while adding the module.");
        }
    }

    // --- Alert helpers ---
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
