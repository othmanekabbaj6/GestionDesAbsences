package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.StudentService;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.util.Optional;

public class AdminStudentsController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> idCol;
    @FXML private TableColumn<Student, String> firstNameCol;
    @FXML private TableColumn<Student, String> lastNameCol;
    @FXML private TableColumn<Student, String> classCol;
    @FXML private TableColumn<Student, String> majorCol;
    @FXML private TableColumn<Student, String> yearCol;

    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        // Setup table columns
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        // Class column: display class name only
        classCol.setCellValueFactory(cellData -> {
            ClassEntity cls = cellData.getValue().getClassEntity();
            String className = cls != null ? cls.getClassName() : "";
            return new ReadOnlyStringWrapper(className);
        });

        // Major column: display major name only
        majorCol.setCellValueFactory(cellData -> {
            String majorName = cellData.getValue().getMajor() != null ? cellData.getValue().getMajor().getName() : "";
            return new ReadOnlyStringWrapper(majorName);
        });

        // SchoolYear column: display year level only
        yearCol.setCellValueFactory(cellData -> {
            String yearLevel = cellData.getValue().getSchoolYear() != null ? cellData.getValue().getSchoolYear().getYearLevel() : "";
            return new ReadOnlyStringWrapper(yearLevel);
        });

        // Load all students into the table
        loadStudents();
    }

    private void loadStudents() {
        List<Student> students = studentService.getAllStudents();
        if (students != null) {
            ObservableList<Student> observableList = FXCollections.observableArrayList(students);
            studentsTable.setItems(observableList);
        }
    }

    @FXML
    void handleCreate() {
        openForm("CREATE");
    }

    @FXML
    void handleUpdate() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a student to update.", Alert.AlertType.WARNING);
            return;
        }
        openForm("UPDATE", selected);
    }

    @FXML
    void handleDelete() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a student to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Deleting student will also remove all related absences!");
        confirm.setContentText("Are you sure you want to delete: " +
                selected.getFirstName() + " " + selected.getLastName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = studentService.deleteStudent(selected.getId());
            showAlert(success ? "Deleted" : "Error",
                    success ? "Student deleted successfully!" : "Failed to delete student.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            if (success) loadStudents();
        }
    }

    private void openForm(String mode) {
        openForm(mode, null);
    }

    private void openForm(String mode, Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/StudentsFormView.fxml"
            ));
            AnchorPane formView = loader.load();
            AdminStudentsFormController controller = loader.getController();
            controller.setMode(mode);
            controller.setParentController(this);

            if (student != null) controller.setStudent(student);

            StackPane contentPane = (StackPane) studentsTable.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(formView);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open form view.", Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        loadStudents();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
