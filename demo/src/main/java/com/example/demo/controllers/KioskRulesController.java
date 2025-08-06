package com.example.demo.controllers;

import com.example.demo.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

public class KioskRulesController {

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskGuestSelection-03.fxml");
    }
}