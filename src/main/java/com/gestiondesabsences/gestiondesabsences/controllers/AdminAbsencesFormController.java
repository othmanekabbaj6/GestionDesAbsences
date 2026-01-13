package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Absence;
import com.gestiondesabsences.gestiondesabsences.models.Module;
import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.AbsenceService;
import com.gestiondesabsences.gestiondesabsences.services.ModuleService;
import com.gestiondesabsences.gestiondesabsences.services.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public class AdminAbsencesFormController {

    @FXML private ComboBox<Student> studentCombo;
    @FXML private ComboBox<Module> moduleCombo;
    @FXML private DatePicker datePicker;
    @FXML private Button actionButton;

    private String mode;
    private Absence absence;
    private AdminAbsencesController parentController;

    private final StudentService studentService = new StudentService();
    private final ModuleService moduleService =
            new ModuleService(new com.gestiondesabsences.gestiondesabsences.dao.ModuleDAO());
    private final AbsenceService absenceService = new AbsenceService();

    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<Module> modules = FXCollections.observableArrayList();

    private FilteredList<Student> filteredStudents;

    @FXML
    public void initialize() {

        /* ================= STUDENTS ================= */
        students.setAll(studentService.getAllStudents());
        filteredStudents = new FilteredList<>(students, s -> true);

        studentCombo.setItems(filteredStudents);
        studentCombo.setEditable(true);


        studentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Student s) {
                if (s == null) return "";
                return s.getFirstName() + " " + s.getLastName();
            }

            @Override
            public Student fromString(String string) {
                return studentCombo.getValue();
            }
        });

        /* ================= AUTOCOMPLETE ================= */
        studentCombo.getEditor().textProperty().addListener((obs, old, text) -> {
            if (studentCombo.getValue() != null &&
                    text.equals(studentCombo.getConverter().toString(studentCombo.getValue()))) {
                return;
            }

            filteredStudents.setPredicate(s ->
                    text == null || text.isEmpty()
                            || (s.getFirstName() + " " + s.getLastName())
                            .toLowerCase().contains(text.toLowerCase())
            );

            if (!filteredStudents.isEmpty()) {
                studentCombo.show();
            }
        });

        /* ================= STUDENT DISPLAY ================= */
        studentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" :
                        s.getFirstName() + " " + s.getLastName()
                                + " | " + s.getMajor().getName()
                                + " | " + s.getSchoolYear().getYearLevel());
            }
        });

        /* ================= MODULE ================= */
        moduleCombo.setItems(modules);
        moduleCombo.setDisable(true);

        moduleCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Module m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? "" : m.getModuleName());
            }
        });
        moduleCombo.setButtonCell(moduleCombo.getCellFactory().call(null));

        /* ================= STUDENT SELECT ================= */
        studentCombo.valueProperty().addListener((obs, old, student) -> {
            if (student != null) {
                loadModulesForStudent(student);
            }
        });
    }

    /* ================= LOAD MODULES ================= */
    private void loadModulesForStudent(Student student) {
        modules.clear();
        moduleCombo.setDisable(true);

        List<Module> list = moduleService.getModulesByStudentId(student.getId());
        if (list != null && !list.isEmpty()) {
            modules.setAll(list);
            moduleCombo.setDisable(false);
        }
    }

    /* ================= FORM MODE ================= */
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

    /* ================= ACTION ================= */
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
            Absence a = new Absence();
            a.setStudent(student);
            a.setModule(module);
            a.setDate(date);
            absenceService.add(a);
        } else {
            absence.setStudent(student);
            absence.setModule(module);
            absence.setDate(date);
            absenceService.update(absence);
        }

        if (parentController != null) parentController.refreshTable();
        handleBack();
    }

    /* ================= UPDATE MODE ================= */
    private void populateForm() {
        studentCombo.setValue(absence.getStudent());
        loadModulesForStudent(absence.getStudent());
        moduleCombo.setValue(absence.getModule());
        datePicker.setValue(absence.getDate());
    }

    private void clearForm() {
        studentCombo.setValue(null);
        filteredStudents.setPredicate(s -> true);
        moduleCombo.getSelectionModel().clearSelection();
        moduleCombo.setDisable(true);
        datePicker.setValue(null);
    }

    /* ================= BACK ================= */
    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/gestiondesabsences/gestiondesabsences/Views/AdminViews/AbsencesView.fxml"
            ));
            AnchorPane view = loader.load();

            StackPane contentPane =
                    (StackPane) actionButton.getScene().lookup("#contentPane");

            contentPane.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
