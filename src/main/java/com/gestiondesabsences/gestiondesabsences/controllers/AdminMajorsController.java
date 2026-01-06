package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.services.MajorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class AdminMajorsController {

    @FXML private TableView<Major> majorsTable;
    @FXML private TableColumn<Major, Integer> idCol;
    @FXML private TableColumn<Major, String> nameCol;

    private final MajorService majorService = new MajorService();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        loadMajors();
    }

    private void loadMajors() {
        ObservableList<Major> data =
                FXCollections.observableArrayList(majorService.getAllMajors());
        majorsTable.setItems(data);
    }

    @FXML
    void handleCreate() {
        openForm();
    }

    @FXML
    void handleDelete() {
        Major selected = majorsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a major first").showAndWait();
            return;
        }


        String preview = majorService.getPreviewDeleteMessage(selected.getId());
        Alert previewAlert = new Alert(Alert.AlertType.INFORMATION);
        previewAlert.setTitle("Preview Deletion");
        previewAlert.setHeaderText("These rows will be deleted:");
        previewAlert.setContentText(preview);
        previewAlert.getDialogPane().setPrefWidth(500); // expand dialog width
        previewAlert.showAndWait();

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you want to delete major \"" + selected.getName() + "\" and all its dependent rows?",
                ButtonType.YES,
                ButtonType.NO
        );

        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            majorService.deleteMajor(selected.getId());
            loadMajors();
        }
    }

    private void openForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/MajorsFormView.fxml"
            ));

            AnchorPane formView = loader.load();

            StackPane contentPane =
                    (StackPane) majorsTable.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(formView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
