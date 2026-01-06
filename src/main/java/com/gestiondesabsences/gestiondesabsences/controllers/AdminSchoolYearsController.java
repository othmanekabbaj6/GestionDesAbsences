package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.services.SchoolYearService;
import com.gestiondesabsences.gestiondesabsences.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminSchoolYearsController {

    @FXML private TableView<SchoolYear> schoolYearsTable;
    @FXML private TableColumn<SchoolYear, Integer> idColumn;
    @FXML private TableColumn<SchoolYear, String> majorColumn;
    @FXML private TableColumn<SchoolYear, String> yearColumn;

    private SchoolYearService schoolYearService;

    @FXML
    public void initialize() {
        // Updated: no arguments needed
        schoolYearService = new SchoolYearService();
        System.out.println("SchoolYearService initialized successfully!");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        majorColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getMajor().getName()
                )
        );
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("yearLevel"));

        loadSchoolYears();
    }

    public void loadSchoolYears() {
        List<SchoolYear> list = schoolYearService.getAllSchoolYears();
        if (list != null) {
            ObservableList<SchoolYear> observableList = FXCollections.observableArrayList(list);
            schoolYearsTable.setItems(observableList);
        }
    }

    @FXML
    void handleCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/SchoolYearsFormView.fxml"
            ));
            AnchorPane formView = loader.load();

            AdminSchoolYearsFormController controller = loader.getController();
            controller.setSchoolYearService(schoolYearService);
            controller.setParentController(this);
            controller.loadMajors();

            StackPane contentPane = (StackPane) schoolYearsTable.getScene().lookup("#contentPane");
            if (contentPane != null) contentPane.getChildren().setAll(formView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete() {
        SchoolYear selected = schoolYearsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;


        String preview = schoolYearService.getPreviewDeleteMessage(selected.getId());

        StringBuilder filtered = new StringBuilder();
        String[] lines = preview.split("\n");
        boolean show = false;
        for (String line : lines) {
            if (line.startsWith("Students:") || line.startsWith("Classes:")) {
                show = true;
                filtered.append(line).append("\n");
            } else if (line.startsWith("Modules:") || line.startsWith("Absences:")) {
                show = false;
            } else if (show) {
                filtered.append(line).append("\n");
            }
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this School Year?");
        confirm.setContentText(filtered.toString());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            schoolYearService.deleteSchoolYearCascade(selected.getId());
            loadSchoolYears();


            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Deleted");
            success.setHeaderText(null);
            success.setContentText("School Year deleted successfully!");
            success.showAndWait();
        }
    }
}
