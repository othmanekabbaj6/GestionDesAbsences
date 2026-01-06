module com.gestiondesabsences.gestiondesabsences {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires static lombok;

    opens com.gestiondesabsences.gestiondesabsences to javafx.fxml;
    exports com.gestiondesabsences.gestiondesabsences;
    opens com.gestiondesabsences.gestiondesabsences.Views to javafx.fxml;
    exports com.gestiondesabsences.gestiondesabsences.controllers;
    opens com.gestiondesabsences.gestiondesabsences.controllers to javafx.fxml;

    opens com.gestiondesabsences.gestiondesabsences.models to javafx.base;


}