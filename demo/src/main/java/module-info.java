module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;

    opens com.example.demo to javafx.fxml;
    opens com.example.demo.controllers to javafx.fxml;

    exports com.example.demo;
    exports com.example.demo.controllers;
    exports com.example.demo.models;
}