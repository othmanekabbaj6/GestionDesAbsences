package com.gestiondesabsences.gestiondesabsences.controllers;

import com.gestiondesabsences.gestiondesabsences.models.Student;
import com.gestiondesabsences.gestiondesabsences.services.AbsenceService;
import com.gestiondesabsences.gestiondesabsences.services.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;

public class StudentSearchController {

    /* =========================
       Services
       ========================= */
    private final StudentService studentService = new StudentService();
    private final AbsenceService absenceService = new AbsenceService();

    /* =========================
       Search fields
       ========================= */
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;

    /* =========================
       Student info section
       ========================= */
    @FXML private VBox resultBox;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label classLabel;
    @FXML private Label majorLabel;
    @FXML private Label yearLabel;

    /* =========================
       Statistics section
       ========================= */
    @FXML private VBox statsBox;
    @FXML private Label totalAbsencesLabel;

    @FXML private TableView<ModuleStat> moduleStatsTable;
    @FXML private TableColumn<ModuleStat, String> moduleColumn;
    @FXML private TableColumn<ModuleStat, Integer> countColumn;

    /* =========================
       Initialization
       ========================= */
    @FXML
    public void initialize() {

        moduleColumn.setCellValueFactory(
                data -> data.getValue().moduleProperty()
        );

        countColumn.setCellValueFactory(
                data -> data.getValue().countProperty().asObject()
        );

        resultBox.setVisible(false);
        statsBox.setVisible(false);
    }

    /* =========================
       🔍 Search handler
       ========================= */
    @FXML
    private void handleSearch() {

        String firstName = firstNameField.getText().trim();
        String lastName  = lastNameField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Validation Error",
                    "Please enter both first name and last name.");
            return;
        }

        try {
            Student student =
                    studentService.searchStudentsByName(firstName, lastName);

            if (student == null) {
                resultBox.setVisible(false);
                statsBox.setVisible(false);
                showAlert("Not Found",
                        "No student found with the given name.");
                return;
            }

            displayStudent(student);
            loadStatistics(student.getId());

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    /* =========================
       📋 Display student info
       ========================= */
    private void displayStudent(Student student) {

        nameLabel.setText("👤 " +
                student.getFirstName() + " " + student.getLastName());


        classLabel.setText("🏫 " +
                student.getClassEntity().getClassName());

        majorLabel.setText("🎓 " +
                student.getMajor().getName());

        yearLabel.setText("📅 " +
                student.getSchoolYear().getYearLevel());

        resultBox.setVisible(true);
    }

    /* =========================
       📊 Load absence statistics
       ========================= */
    private void loadStatistics(int studentId) {

        Map<String, Integer> stats =
                absenceService.getStudentAbsenceStats(studentId);

        ObservableList<ModuleStat> data =
                FXCollections.observableArrayList();

        int total = 0;

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            data.add(new ModuleStat(entry.getKey(), entry.getValue()));
            total += entry.getValue();
        }

        moduleStatsTable.setItems(data);
        totalAbsencesLabel.setText("Total absences: " + total);

        statsBox.setVisible(true);
    }

    /* =========================
       ⚠ Alerts
       ========================= */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /* =========================
       📦 Table helper class
       ========================= */
    public static class ModuleStat {

        private final SimpleStringProperty module;
        private final SimpleIntegerProperty count;

        public ModuleStat(String module, int count) {
            this.module = new SimpleStringProperty(module);
            this.count = new SimpleIntegerProperty(count);
        }

        public String getModule() {
            return module.get();
        }

        public SimpleStringProperty moduleProperty() {
            return module;
        }

        public int getCount() {
            return count.get();
        }

        public SimpleIntegerProperty countProperty() {
            return count;
        }
    }
}
