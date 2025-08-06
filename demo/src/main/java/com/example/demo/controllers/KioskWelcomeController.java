package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller for the KioskWelcomePage-01.fxml scene.
 * This class handles the initial user interactions, such as starting a booking
 * or navigating to other parts of the application like the admin login or
 * rules page.
 */
public class KioskWelcomeController {

    /**
     * Handles the "Self-Checkout/Kiosk" button action.
     * This method initializes a new ReservationDetails object and navigates
     * the user to the date selection scene to begin the booking process.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the next scene cannot be loaded.
     */
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        ReservationDetails reservationDetails = new ReservationDetails(null, null, 0, 0);
        MainApplication.loadDateSelectionScene(reservationDetails);
    }

    /**
     * Handles the "Admin Login" button action.
     * This method now navigates to the admin login scene using the specific FXML file.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the admin scene cannot be loaded.
     */
    @FXML
    private void handleAdminLogin(ActionEvent event) throws IOException {
        System.out.println("Admin login button clicked.");
        MainApplication.loadAdminLoginScene(); // This will load "AdminLoginPage-08.fxml" as defined in MainApplication
    }

    /**
     * Handles the "Rules & Regulations" button action.
     * This method navigates the user to a page that displays the
     * hotel's rules and regulations.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the rules page cannot be loaded.
     */
    @FXML
    private void handleViewRules(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskRulesPage-15.fxml");
    }
}
