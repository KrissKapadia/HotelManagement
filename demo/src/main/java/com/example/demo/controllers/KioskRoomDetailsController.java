package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class KioskRoomDetailsController {

    private ReservationDetails reservationDetails;

    public void setReservationDetails(ReservationDetails reservation) {
        this.reservationDetails = reservation;
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadRoomSelectionScene(reservationDetails);
    }

    @FXML
    private void handleContinue(ActionEvent event) throws IOException {
        MainApplication.loadRoomDetailsScene(reservationDetails);
    }
}