package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Absence;
import com.gestiondesabsences.gestiondesabsences.services.AbsenceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Optional;

public class AdminAbsencesController {

    @FXML private TableView<Absence> absencesTable;
    @FXML private TableColumn<Absence, Integer> idCol;
    @FXML private TableColumn<Absence, String> studentCol;
    @FXML private TableColumn<Absence, String> moduleCol;
    @FXML private TableColumn<Absence, String> majorCol;
    @FXML private TableColumn<Absence, String> yearCol;
    @FXML private TableColumn<Absence, String> dateCol;

    private final AbsenceService absenceService = new AbsenceService();

    @FXML
    public void initialize() {
        // Table columns
        idCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(cell.getValue().getId()));

        studentCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getStudent() != null ?
                                cell.getValue().getStudent().getFirstName() + " " + cell.getValue().getStudent().getLastName() : ""
                ));

        moduleCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getModule() != null ?
                                cell.getValue().getModule().getModuleName() : ""
                ));

        majorCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getStudent() != null && cell.getValue().getStudent().getMajor() != null ?
                                cell.getValue().getStudent().getMajor().getName() : ""
                ));

        yearCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getStudent() != null && cell.getValue().getStudent().getSchoolYear() != null ?
                                cell.getValue().getStudent().getSchoolYear().getYearLevel() : ""
                ));

        dateCol.setCellValueFactory(cell ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        cell.getValue().getDate() != null ?
                                cell.getValue().getDate().toString() : ""
                ));

        loadAbsences();
    }

    private void loadAbsences() {
        List<Absence> absences = absenceService.getAll();
        if (absences != null) {
            ObservableList<Absence> observableList = FXCollections.observableArrayList(absences);
            absencesTable.setItems(observableList);
        }
    }

    @FXML
    void handleCreate() {
        openForm("CREATE");
    }

    @FXML
    void handleUpdate() {
        Absence selected = absencesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an absence to update.", Alert.AlertType.WARNING);
            return;
        }
        openForm("UPDATE", selected);
    }

    @FXML
    void handleDelete() {
        Absence selected = absencesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an absence to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete absence of "
                + selected.getStudent().getFirstName() + " " + selected.getStudent().getLastName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = absenceService.delete(selected.getId());
            showAlert(success ? "Deleted" : "Error",
                    success ? "Absence deleted successfully!" : "Failed to delete absence.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (success) loadAbsences();
        }
    }

    private void openForm(String mode) {
        openForm(mode, null);
    }

    private void openForm(String mode, Absence absence) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/AbsencesFormView.fxml"
            ));
            AnchorPane formView = loader.load();
            AdminAbsencesFormController controller = loader.getController();
            controller.setMode(mode);
            controller.setParentController(this);
            if (absence != null) controller.setAbsence(absence);

            StackPane contentPane = (StackPane) absencesTable.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(formView);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open form view.", Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        loadAbsences();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
