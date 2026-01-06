package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.services.ModuleService;
import com.gestiondesabsences.gestiondesabsences.dao.ModuleDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class AdminModulesController {

    @FXML
    private TableView<Module> modulesTable;

    @FXML
    private TableColumn<Module, Integer> idCol;

    @FXML
    private TableColumn<Module, String> nameCol;

    @FXML
    private TableColumn<Module, String> majorCol;

    @FXML
    private TableColumn<Module, String> yearCol;

    private final ModuleService service;

    public AdminModulesController() {
        try {
            // Use no-arg constructor for ModuleDAO since it handles its own connection
            this.service = new ModuleService(new ModuleDAO());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ModuleService", e);
        }
    }

    @FXML
    public void initialize() {
        // Setup table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("moduleName"));

        majorCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMajor() != null) {
                return new SimpleStringProperty(cellData.getValue().getMajor().getName());
            } else {
                return new SimpleStringProperty("");
            }
        });

        yearCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSchoolYear() != null) {
                return new SimpleStringProperty(cellData.getValue().getSchoolYear().getYearLevel());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Load modules from DB
        refreshTable();
    }

    @FXML
    void handleCreate() {
        openForm();
    }

    @FXML
    void handleDelete() {
        Module selectedModule = modulesTable.getSelectionModel().getSelectedItem();
        if (selectedModule == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a module to delete.");
            alert.showAndWait();
            return;
        }

        // Show warning about related absences
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Deleting this module will also delete all related absences!");
        confirm.setContentText("Are you sure you want to delete module: " + selectedModule.getModuleName() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = service.deleteModuleCascade(selectedModule.getId());

                Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                alert.setTitle(success ? "Deleted" : "Error");
                alert.setHeaderText(null);
                alert.setContentText(success ? "Module deleted successfully!" : "Failed to delete module.");
                alert.showAndWait();

                if (success) refreshTable();
            }
        });
    }

    private void openForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/ModulesFormView.fxml"
            ));

            AnchorPane formView = loader.load();
            // AdminModulesFormController controller = loader.getController(); // optional if you want to interact

            StackPane contentPane = (StackPane) modulesTable.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(formView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        modulesTable.getItems().clear();
        modulesTable.getItems().addAll(service.getAllModules());
    }
}
