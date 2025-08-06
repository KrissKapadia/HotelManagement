package com.example.demo.controllers;

import com.example.demo.util.Systemlogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import com.example.demo.models.DatabaseManager;
import com.example.demo.models.DatabaseManager.ReservationDisplay;
import com.example.demo.util.Systemlogger; // Import your custom logger utility
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger; // Import the Java logging class

public class AdminDashboardController {

    // Get the custom logger instance
    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private TableView<ReservationDisplay> reservationsTable;
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
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestFullName"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("roomDetailsSummary"));
        checkInTimeColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDateFormatted"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUpcomingReservations();

        logger.info("AdminDashboardController initialized.");
    }

    /**
     * Loads reservations for today and tomorrow into the reservationsTable.
     */
    private void loadUpcomingReservations() {
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
        logger.info("Admin clicked on 'New Reservation' button."); // Log the button click
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
        logger.info("Admin clicked on 'Guest Search' button."); // Log the button click
        MainApplication.loadGuestSearchManagementScene();
    }

    /**
     * Handles the action for the "Check-Out" sidebar button.
     * Navigates to the Admin Check-Out page.
     * @param event The action event.
     * @throws IOException If the FXML for the check-out page cannot be loaded.
     */
    @FXML
    private void handleCheckOut(ActionEvent event) throws IOException {
        logger.info("Admin clicked on 'Check-Out' button."); // Log the button click
        MainApplication.loadAdminCheckOutScene();
    }

    /**
     * Handles the action for the "Reports" sidebar button.
     * Navigates to the Admin Reports page.
     * @param event The action event.
     * @throws IOException If the FXML for the reports page cannot be loaded.
     */
    @FXML
    private void handleReports(ActionEvent event) throws IOException {
        logger.info("Admin clicked on 'Reports' button."); // Log the button click
        MainApplication.loadAdminReportsScene();
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
        logger.info("Admin logged out from dashboard."); // Log the logout action
        MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
    }
}
