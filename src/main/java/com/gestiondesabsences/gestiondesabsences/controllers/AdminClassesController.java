package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.ClassEntityService;
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

import java.util.List;

public class AdminClassesController {

    @FXML private TableView<ClassEntity> classesTable;
    @FXML private TableColumn<ClassEntity, Integer> idCol;
    @FXML private TableColumn<ClassEntity, String> nameCol;
    @FXML private TableColumn<ClassEntity, String> majorCol;
    @FXML private TableColumn<ClassEntity, String> yearCol;

    private final ClassEntityService classService = new ClassEntityService();

    @FXML
    public void initialize() {
        // Initialize table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("className"));

        majorCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getSchoolYear().getMajor().getName()
                )
        );

        yearCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getSchoolYear().getYearLevel()
                )
        );

        loadClasses();
    }

    private void loadClasses() {
        ObservableList<ClassEntity> data = FXCollections.observableArrayList(classService.getAllClasses());
        classesTable.setItems(data);
    }

    @FXML
    void handleCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/ClassesFormView.fxml"
            ));
            AnchorPane formView = loader.load();

            AdminClassesFormController controller = loader.getController();
            controller.setMode("ADD"); // Only Add mode

            StackPane contentPane = (StackPane) classesTable.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(formView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete() {
        ClassEntity selected = classesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a class to delete.").showAndWait();
            return;
        }

        // Preview students in the class
        List<Student> students = classService.getStudentsInClass(selected.getId());
        StringBuilder studentList = new StringBuilder();
        if (students.isEmpty()) {
            studentList.append("No students in this class.");
        } else {
            for (Student s : students) {
                studentList.append(s.getFirstName())
                        .append(" ")
                        .append(s.getLastName())
                        .append("\n");
            }
        }

        // Confirmation alert with student preview
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete class: " + selected.getClassName());
        confirm.setContentText(
                "This will delete all absences and students in this class.\n\n" +
                        "Students:\n" + studentList
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Cascade delete
                boolean success = classService.deleteClassCascade(selected.getId());
                if (success) {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Class and related students and absences deleted successfully.")
                            .showAndWait();
                    loadClasses(); // Refresh table
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete class.").showAndWait();
                }
            }
        });
    }
}
