package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.ClassEntity;
import com.gestiondesabsences.gestiondesabsences.models.Major;
import com.gestiondesabsences.gestiondesabsences.models.SchoolYear;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.ClassEntityService;
import com.gestiondesabsences.gestiondesabsences.services.MajorService;
import com.gestiondesabsences.gestiondesabsences.services.SchoolYearService;
import com.gestiondesabsences.gestiondesabsences.services.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class AdminStudentsFormController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<ClassEntity> classCombo;
    @FXML private ComboBox<Major> majorCombo;
    @FXML private ComboBox<SchoolYear> yearCombo;
    @FXML private Button actionButton;

    private String mode;
    private Student student;
    private AdminStudentsController parentController;

    private final StudentService studentService = new StudentService();
    private final MajorService majorService = new MajorService();
    private final SchoolYearService schoolYearService = new SchoolYearService();
    private final ClassEntityService classService = new ClassEntityService();

    private final ObservableList<Major> majorList = FXCollections.observableArrayList();
    private final ObservableList<SchoolYear> yearList = FXCollections.observableArrayList();
    private final ObservableList<ClassEntity> classList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            // Load Majors
            List<Major> majors = majorService.getAllMajors();
            majorList.setAll(majors);
            majorCombo.setItems(majorList);

            // Display Major names properly
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

            // When Major is selected, load related SchoolYears
            majorCombo.setOnAction(e -> loadSchoolYears());

            // When SchoolYear is selected, load related Classes
            yearCombo.setOnAction(e -> loadClasses());

            // Display ClassEntity names only
            classCombo.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(ClassEntity item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getClassName());
                }
            });
            classCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(ClassEntity item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getClassName());
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSchoolYears() {
        Major selectedMajor = majorCombo.getValue();
        if (selectedMajor != null) {
            List<SchoolYear> years = schoolYearService.getSchoolYearsByMajor(selectedMajor.getId());
            yearList.setAll(years);
            yearCombo.setItems(yearList);
            yearCombo.setValue(null); // reset selection
            classList.clear();
            classCombo.setItems(classList);
        }
    }

    private void loadClasses() {
        SchoolYear selectedYear = yearCombo.getValue();
        if (selectedYear != null) {
            List<ClassEntity> classes = classService.getClassesBySchoolYear(selectedYear.getId());
            classList.setAll(classes);
            classCombo.setItems(classList);
            classCombo.setValue(null); // reset selection
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
        actionButton.setText("CREATE".equals(mode) ? "Save" : "Update");
        if ("CREATE".equals(mode)) clearForm();
    }

    public void setStudent(Student student) {
        this.student = student;
        populateForm();
    }

    public void setParentController(AdminStudentsController parent) {
        this.parentController = parent;
    }

    @FXML
    void handleAction() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        Major major = majorCombo.getValue();
        SchoolYear year = yearCombo.getValue();
        ClassEntity classEntity = classCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || major == null || year == null || classEntity == null) {
            showAlert("Missing Data", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        if ("CREATE".equals(mode)) {
            Student newStudent = new Student();
            newStudent.setFirstName(firstName);
            newStudent.setLastName(lastName);
            newStudent.setMajor(major);
            newStudent.setSchoolYear(year);
            newStudent.setClassEntity(classEntity);
            newStudent.setAbsences(FXCollections.observableArrayList());

            boolean success = studentService.addStudent(newStudent);
            showAlert(success ? "Success" : "Error",
                    success ? "Student added successfully!" : "Failed to add student.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);

        } else if ("UPDATE".equals(mode) && student != null) {
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setMajor(major);
            student.setSchoolYear(year);
            student.setClassEntity(classEntity);

            boolean success = studentService.updateStudent(student);
            showAlert(success ? "Success" : "Error",
                    success ? "Student updated successfully!" : "Failed to update student.",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        }

        if (parentController != null) parentController.refreshTable();
        handleBack();
    }

    private void populateForm() {
        if (student != null) {
            firstNameField.setText(student.getFirstName());
            lastNameField.setText(student.getLastName());

            // Set Major
            majorCombo.setValue(student.getMajor());

            // Load SchoolYears of the major and set the correct one
            loadSchoolYears();
            yearCombo.setValue(student.getSchoolYear());

            // Load Classes of the selected year and set the correct one
            loadClasses();
            classCombo.setValue(student.getClassEntity());
        }
    }

    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        majorCombo.getSelectionModel().clearSelection();
        yearCombo.getSelectionModel().clearSelection();
        classCombo.getSelectionModel().clearSelection();
        yearList.clear();
        classList.clear();
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/StudentsView.fxml"
                    )
            );
            AnchorPane studentsView = loader.load();
            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            contentPane.getChildren().setAll(studentsView);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot load students view.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
