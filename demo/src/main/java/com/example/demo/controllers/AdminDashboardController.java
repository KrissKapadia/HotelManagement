package com.example.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory; // Import PropertyValueFactory
import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import com.example.demo.models.DatabaseManager; // Import DatabaseManager
import com.example.demo.models.DatabaseManager.ReservationDisplay; // Import ReservationDisplay

import java.io.IOException;
import java.time.LocalDate; // Import LocalDate
import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<ReservationDisplay> reservationsTable; // Specify the model type
    @FXML
    private TableColumn<ReservationDisplay, String> guestNameColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> roomColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> checkInTimeColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> statusColumn;

    @FXML
    public void initialize() {
        // Set up cell value factories for TableColumns
        // These property names must match the getter methods in DatabaseManager.ReservationDisplay
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestFullName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomDetailsSummary")); // Matches getRoomDetailsSummary()
        checkInTimeColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDateFormatted")); // Matches getCheckInDateFormatted()
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Matches getStatus()

        // Load upcoming reservations when the controller initializes
        loadUpcomingReservations();

        System.out.println("AdminDashboardController initialized.");
    }

    /**
     * Loads reservations for today and tomorrow into the reservationsTable.
     */
    private void loadUpcomingReservations() {
        // Initialize the database connection and table if they don't exist
        DatabaseManager.initialize();

        List<ReservationDisplay> upcomingReservations = DatabaseManager.getReservationsForNext24Hours();
        ObservableList<ReservationDisplay> observableList = FXCollections.observableArrayList(upcomingReservations);
        reservationsTable.setItems(observableList);

        if (upcomingReservations.isEmpty()) {
            reservationsTable.setPlaceholder(new Label("No upcoming reservations for today or tomorrow."));
        }
    }

    /**
     * Handles the action for the "New Reservation" sidebar button.
     * Navigates to the Kiosk Date Selection page to start a new reservation.
     * @param event The action event.
     * @throws IOException If the FXML file for the date selection page cannot be loaded.
     */
    @FXML
    private void handleNewReservation(ActionEvent event) throws IOException {
        // Create a new ReservationDetails object for the new booking
        ReservationDetails newReservation = new ReservationDetails();
        MainApplication.loadDateSelectionScene(newReservation);
    }

    /**
     * Handles the action for the "Guest Search" sidebar button.
     * Navigates to the Guest Search & Management page.
     * @param event The action event.
     * @throws IOException If the FXML for the guest search page cannot be loaded.
     */
    @FXML
    private void handleGuestSearch(ActionEvent event) throws IOException {
        MainApplication.loadGuestSearchManagementScene(); // Navigate to the Guest Search & Management scene
    }

    /**
     * Handles the action for the "Check-Out" sidebar button.
     * Navigates to the Admin Check-Out page.
     * @param event The action event.
     * @throws IOException If the FXML for the check-out page cannot be loaded.
     */
    @FXML
    private void handleCheckOut(ActionEvent event) throws IOException {
        MainApplication.loadAdminCheckOutScene(); // Navigate to the Admin Check-Out scene
    }

    /**
     * Handles the action for the "Reports" sidebar button.
     * Navigates to the Admin Reports page.
     * @param event The action event.
     * @throws IOException If the FXML for the reports page cannot be loaded.
     */
    @FXML
    private void handleReports(ActionEvent event) throws IOException {
        MainApplication.loadAdminReportsScene(); // Navigate to the Admin Reports scene
    }

    /**
     * Helper method to display an information alert.
     * @param title The title of the alert.
     * @param content The content message of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles the action for the "Logout" button (if added later).
     * For now, it returns to the welcome page.
     * @param event The action event.
     * @throws IOException If the FXML for the welcome page cannot be loaded.
     */
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
    }
}
