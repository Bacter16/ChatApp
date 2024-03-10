module com.exemple.socialapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires com.zaxxer.hikari;

    opens com.exemple.socialapp;
    opens com.exemple.socialapp.controller;
    opens com.exemple.socialapp.domain;
    opens com.exemple.socialapp.repository;
    opens com.exemple.socialapp.service;
    exports com.exemple.socialapp;
    exports com.exemple.socialapp.controller;
}