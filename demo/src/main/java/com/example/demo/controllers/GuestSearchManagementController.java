package com.example.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType; // Import ButtonType for confirmation dialogs
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager; // Import DatabaseManager
import com.example.demo.models.DatabaseManager.ReservationDisplay; // Import the inner class
import com.example.demo.models.Guest; // Import Guest model
import com.example.demo.models.ReservationDetails; // Import ReservationDetails model

import java.io.IOException;
import java.util.List;
import java.util.Optional; // Import Optional for Alert results

public class GuestSearchManagementController {

    @FXML
    private TextField searchField; // Renamed from searchPhoneField to match FXML

    @FXML
    private Button searchButton;

    @FXML
    private TableView<ReservationDisplay> guestReservationsTable;
    @FXML
    private TableColumn<ReservationDisplay, String> guestNameColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> phoneColumn; // Matched FXML fx:id
    @FXML
    private TableColumn<ReservationDisplay, String> reservationIdColumn; // Matched FXML fx:id
    @FXML
    private TableColumn<ReservationDisplay, String> checkInDateColumn; // Matched FXML fx:id
    @FXML
    private TableColumn<ReservationDisplay, String> statusColumn; // Added to match controller's expectation

    @FXML
    private Button backButton; // This fx:id is for the "Back to Dashboard" button
    @FXML
    private Button viewDetailsButton;
    @FXML
    private Button cancelBookingButton;
    @FXML
    private Button modifyBookingButton; // Added FXML ID for the "Modify Booking" button

    @FXML
    public void initialize() {
        // Set up cell value factories for TableColumns
        // These property names must match the getter methods in DatabaseManager.ReservationDisplay
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestFullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("guestPhoneNumber")); // Using new getter
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationIdValue")); // Using new getter
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDateFormatted"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        DatabaseManager.initialize();
        loadAllReservations(); // Load initial data or placeholder

        // Disable action buttons initially if no item is selected
        viewDetailsButton.setDisable(true);
        cancelBookingButton.setDisable(true);
        modifyBookingButton.setDisable(true);

        // Add listener to enable/disable buttons based on table selection
        guestReservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            viewDetailsButton.setDisable(!isSelected);
            cancelBookingButton.setDisable(!isSelected);
            modifyBookingButton.setDisable(!isSelected);
        });
    }

    /**
     * Handles the search action when the search button is clicked.
     * Searches for reservations by phone number.
     * @param event The action event.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText(); // Using searchField
        List<ReservationDisplay> results = DatabaseManager.searchReservationsByPhoneNumber(searchText);

        if (results.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Search Results", "No reservations found for the given phone number.");
        }
        guestReservationsTable.setItems(FXCollections.observableArrayList(results));
    }

    /**
     * Loads all reservations into the table. This could be used for initial display
     * or after clearing a search.
     */
    private void loadAllReservations() {
        // This method is a placeholder. If you have a DatabaseManager.getAllReservations() method, use it here.
        // For now, it sets a placeholder message.
        guestReservationsTable.setPlaceholder(new Label("Enter a phone number to search for reservations."));
    }

    /**
     * Handles the action for the "View Details" button.
     * Retrieves the selected reservation and loads the Guest Details scene (read-only).
     * @param event The action event.
     * @throws IOException If the FXML for the guest details page cannot be loaded.
     */
    @FXML
    private void handleViewDetails(ActionEvent event) throws IOException {
        ReservationDisplay selectedReservation = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Guest guest = selectedReservation.getGuest();
            ReservationDetails details = selectedReservation.getDetails();
            // Assuming loadGuestDetailsScene is for the read-only view
            MainApplication.loadGuestDetailsScene(guest, details); // This method is in MainApplication
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to view its details.");
        }
    }

    /**
     * Handles the action for the "Modify Booking" button.
     * Retrieves the selected reservation and loads the editable Guest Details scene.
     * @param event The action event.
     * @throws IOException If the FXML for the editable guest details page cannot be loaded.
     */
    @FXML
    private void handleModifyBooking(ActionEvent event) throws IOException {
        ReservationDisplay selectedReservation = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            Guest guest = selectedReservation.getGuest();
            ReservationDetails details = selectedReservation.getDetails();
            // This will load the editable GuestDetails.fxml
            MainApplication.loadAdminGuestDetailsModifyScene(guest, details); // Method in MainApplication
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to modify.");
        }
    }

    /**
     * Handles the action for the "Cancel Booking" button.
     * Deletes the selected reservation from the database upon user confirmation.
     * @param event The action event.
     */
    @FXML
    private void handleCancelBooking(ActionEvent event) {
        ReservationDisplay selectedDisplay = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedDisplay != null) {
            String reservationId = selectedDisplay.getReservationIdValue();

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Cancellation");
            confirmationAlert.setHeaderText("Delete Reservation?");
            confirmationAlert.setContentText("Are you sure you want to cancel and delete reservation ID " + reservationId + "? This action cannot be undone.");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseManager.initialize(); // Ensure DB is ready
                boolean deleted = DatabaseManager.deleteReservation(reservationId); // Call deleteReservation
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation ID " + reservationId + " has been successfully cancelled and deleted.");
                    handleSearch(null); // Refresh the table after deletion
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete reservation ID " + reservationId + ".");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to cancel.");
        }
    }

    /**
     * Handles the action for the "Back to Dashboard" button.
     * Navigates back to the Admin Dashboard.
     * @param event The action event.
     * @throws IOException If the FXML for the dashboard cannot be loaded.
     */
    @FXML
    private void handleBackToDashboard(ActionEvent event) throws IOException {
        MainApplication.loadAdminDashboardScene();
    }

    /**
     * Helper method to display an alert.
     * @param type The type of alert (e.g., INFORMATION, ERROR).
     * @param title The title of the alert window.
     * @param content The message content of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
