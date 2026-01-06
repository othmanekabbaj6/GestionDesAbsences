package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Absence;
import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.AbsenceService;
import com.gestiondesabsences.gestiondesabsences.services.ModuleService;
import com.gestiondesabsences.gestiondesabsences.services.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAbsencesFormController {

    @FXML private ComboBox<Student> studentCombo;
    @FXML private ComboBox<Module> moduleCombo;
    @FXML private DatePicker datePicker;
    @FXML private Button actionButton;

    private String mode;
    private Absence absence;
    private AdminAbsencesController parentController;

    private final StudentService studentService = new StudentService();
    private final ModuleService moduleService = new ModuleService(new com.gestiondesabsences.gestiondesabsences.dao.ModuleDAO());
    private final AbsenceService absenceService = new AbsenceService();

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<Module> modules = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Load all students
        List<Student> studentList = studentService.getAllStudents();
        if (studentList != null) {
            students.setAll(studentList);
            studentCombo.setItems(students);
        }

        // Load all modules (we will filter later)
        moduleCombo.setItems(modules);
        moduleCombo.setDisable(true);

        // Make studentCombo editable
        studentCombo.setEditable(true);

        // StringConverter for Student (display + parse typed text)
        studentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Student student) {
                if (student == null) return "";
                String major = student.getMajor() != null ? student.getMajor().getName() : "Unknown";
                String year = student.getSchoolYear() != null ? student.getSchoolYear().getYearLevel() : "Unknown";
                return student.getFirstName() + " " + student.getLastName() + " | " + major + " | " + year;
            }

            @Override
            public Student fromString(String typed) {
                if (typed == null || typed.isEmpty()) return null;
                String trimmed = typed.trim().toLowerCase();
                for (Student s : students) {
                    String fullName = (s.getFirstName() + " " + s.getLastName()).toLowerCase();
                    if (fullName.equals(trimmed)) {
                        return s;
                    }
                }
                return null;
            }
        });

        // Custom cell for student display
        studentCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Student item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String major = item.getMajor() != null ? item.getMajor().getName() : "Unknown";
                    String year = item.getSchoolYear() != null ? item.getSchoolYear().getYearLevel() : "Unknown";
                    setText(item.getFirstName() + " " + item.getLastName() + " | " + major + " | " + year);
                }
            }
        });
        studentCombo.setButtonCell(studentCombo.getCellFactory().call(null));

        // Custom cell for module display
        moduleCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Module item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getModuleName());
                }
            }
        });
        moduleCombo.setButtonCell(moduleCombo.getCellFactory().call(null));

        // Listen to student selection -> filter modules
        studentCombo.valueProperty().addListener((obs, oldStudent, newStudent) -> {
            loadModulesForStudent(newStudent);
        });
    }

    /** Filter modules based on the selected student */
    private void loadModulesForStudent(Student student) {
        modules.clear();
        moduleCombo.setDisable(true);

        if (student != null) {
            List<Module> studentModules = moduleService.getModulesByStudentId(student.getId());
            if (studentModules != null) {
                modules.setAll(studentModules);
                moduleCombo.setDisable(false);
            }
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
        actionButton.setText("CREATE".equals(mode) ? "Save" : "Update");
        if ("CREATE".equals(mode)) clearForm();
    }

    public void setAbsence(Absence absence) {
        this.absence = absence;
        populateForm();
    }

    public void setParentController(AdminAbsencesController controller) {
        this.parentController = controller;
    }

    @FXML
    void handleAction() {
        Student student = studentCombo.getValue();
        Module module = moduleCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (student == null || module == null || date == null) {
            showAlert("Missing Data", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        if ("CREATE".equals(mode)) {
            Absence newAbsence = new Absence();
            newAbsence.setStudent(student);
            newAbsence.setModule(module);
            newAbsence.setDate(date);

            boolean success = absenceService.add(newAbsence);
            showAlert(success ? "Success" : "Error",
                    success ? "Absence added!" : "Failed to add absence",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);

        } else if ("UPDATE".equals(mode) && absence != null) {
            absence.setStudent(student);
            absence.setModule(module);
            absence.setDate(date);

            boolean success = absenceService.update(absence);
            showAlert(success ? "Success" : "Error",
                    success ? "Absence updated!" : "Failed to update absence",
                    success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        }

        if (parentController != null) parentController.refreshTable();
        handleBack();
    }

    private void populateForm() {
        if (absence != null) {
            studentCombo.setValue(absence.getStudent());
            moduleCombo.setValue(absence.getModule());
            datePicker.setValue(absence.getDate());
        }
    }

    private void clearForm() {
        studentCombo.getSelectionModel().clearSelection();
        moduleCombo.getSelectionModel().clearSelection();
        moduleCombo.setDisable(true);
        datePicker.setValue(null);
    }

    @FXML
    void handleBack() {
        try {
            StackPane contentPane = (StackPane) actionButton.getScene().lookup("#contentPane");
            contentPane.getChildren().clear();
        } catch (Exception e) {
            e.printStackTrace();
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
