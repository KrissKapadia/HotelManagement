package com.example.demo.models;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Manages the connection and operations for the SQLite database.
 * This class handles creating the database file and the reservation table,
 * and saving, searching, and updating guest and reservation data.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:reservations.db";

    /**
     * Initializes the database connection and ensures the reservation table exists.
     * Call this method before performing any database operations.
     */
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                createReservationTable(conn);
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Creates the 'reservations' table if it does not already exist.
     * Updated to match the provided Guest model (no firstName, lastName, gender, age, country).
     * @param conn The database connection.
     * @throws SQLException If a database access error occurs.
     */
    private static void createReservationTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS reservations (" +
                "reservation_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "full_name TEXT NOT NULL," +
                "phone_number TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "address TEXT NOT NULL," + // Maps to streetName
                "province TEXT NOT NULL," +
                "city TEXT NOT NULL," +
                "postal_code TEXT NOT NULL," +
                "id_proof_type TEXT NOT NULL," +
                "id_proof_number TEXT NOT NULL," +
                "check_in_date TEXT NOT NULL," +
                "check_out_date TEXT NOT NULL," +
                "adults INTEGER NOT NULL," +
                "children INTEGER NOT NULL," +
                "single_rooms INTEGER NOT NULL," +
                "double_rooms INTEGER NOT NULL," +
                "deluxe_rooms INTEGER NOT NULL," +
                "penthouse_rooms INTEGER NOT NULL," + // Updated column name to match camelCase in model
                "total_price REAL NOT NULL," +
                "status TEXT NOT NULL," +
                "room_number TEXT," +
                "room_type TEXT" +
                ");";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Reservations table created or already exists.");
        }
    }

    /**
     * Inserts a new reservation and guest record into the database.
     * Returns the generated reservation ID.
     *
     * @param guest               The Guest object to save.
     * @param details             The ReservationDetails object to save.
     * @param finalEstimatedTotal
     * @return The generated reservation ID as a String, or null if insertion fails.
     */
    public static String insertReservation(Guest guest, ReservationDetails details, double finalEstimatedTotal) {
        String sql = "INSERT INTO reservations(" +
                "full_name, phone_number, email, address, province, city, postal_code, " +
                "id_proof_type, id_proof_number, check_in_date, check_out_date, " +
                "adults, children, single_rooms, double_rooms, deluxe_rooms, penthouse_rooms, " +
                "total_price, status, room_number, room_type) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 21 parameters

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Guest parameters (indices adjusted)
            pstmt.setString(1, guest.getFullName());
            pstmt.setString(2, guest.getPhoneNumber());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getAddress());
            pstmt.setString(5, guest.getProvince());
            pstmt.setString(6, guest.getCity());
            pstmt.setString(7, guest.getPostalCode());
            pstmt.setString(8, guest.getIdProofType());
            pstmt.setString(9, guest.getIdProofNumber());

            // ReservationDetails parameters (indices adjusted)
            pstmt.setString(10, details.getCheckInDate().toString());
            pstmt.setString(11, details.getCheckOutDate().toString());
            pstmt.setInt(12, details.getNumberOfAdults());
            pstmt.setInt(13, details.getNumberOfChildren());
            pstmt.setInt(14, details.getSingleRooms());
            pstmt.setInt(15, details.getDoubleRooms());
            pstmt.setInt(16, details.getDeluxeRooms());
            pstmt.setInt(17, details.getPenthouses());
            pstmt.setDouble(18, details.getEstimatedPrice()); // Using getEstimatedPrice from details
            pstmt.setString(19, details.getStatus());
            pstmt.setString(20, details.getRoomNumber());
            pstmt.setString(21, details.getRoomType());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        String generatedId = String.valueOf(rs.getInt(1));
                        System.out.println("Reservation successfully saved with ID: " + generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a reservation from the database based on its reservation ID.
     * @param reservationId The ID of the reservation to delete.
     * @return true if the reservation was deleted successfully, false otherwise.
     */
    public static boolean deleteReservation(String reservationId) {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Assuming reservation_id is an INTEGER in your DB, convert if necessary
            // If reservation_id is AUTOINCREMENT, it's likely an INTEGER
            pstmt.setInt(1, Integer.parseInt(reservationId));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Reservation with ID " + reservationId + " successfully deleted from database.");
                return true;
            } else {
                System.out.println("No reservation found with ID " + reservationId + " for deletion.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Invalid reservation ID format: " + reservationId + ". Must be a number.");
            return false;
        }
    }

    /**
     * Searches for reservations by guest phone number.
     * Updated to match the provided Guest model.
     * @param phoneNumber The phone number to search for.
     * @return A list of ReservationDisplay objects matching the phone number.
     */
    public static List<ReservationDisplay> searchReservationsByPhoneNumber(String phoneNumber) {
        List<ReservationDisplay> results = new ArrayList<>();
        String sql = "SELECT reservation_id, full_name, phone_number, check_in_date, " +
                "email, address, province, city, postal_code, " +
                "id_proof_type, id_proof_number, check_out_date, adults, children, single_rooms, double_rooms, " +
                "deluxe_rooms, penthouse_rooms, total_price, status, room_number, room_type " +
                "FROM reservations WHERE phone_number LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + phoneNumber + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Construct Guest object using the available constructor
                Guest guest = new Guest(
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("province"),
                        rs.getString("city"),
                        rs.getString("postal_code"),
                        rs.getString("id_proof_type"),
                        rs.getString("id_proof_number")
                );

                // Construct ReservationDetails object using all fields
                ReservationDetails details = new ReservationDetails();
                details.setReservationId(String.valueOf(rs.getInt("reservation_id")));
                details.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
                details.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
                details.setNumberOfAdults(rs.getInt("adults"));
                details.setNumberOfChildren(rs.getInt("children"));
                details.setSingleRooms(rs.getInt("single_rooms"));
                details.setDoubleRooms(rs.getInt("double_rooms"));
                details.setDeluxeRooms(rs.getInt("deluxe_rooms"));
                details.setPenthouses(rs.getInt("penthouse_rooms"));
                details.setEstimatedPrice(rs.getDouble("total_price"));
                details.setStatus(rs.getString("status"));
                details.setRoomNumber(rs.getString("room_number"));
                details.setRoomType(rs.getString("room_type"));

                // Create ReservationDisplay object for the TableView
                results.add(new ReservationDisplay(guest, details));
            }
        } catch (SQLException e) {
            System.err.println("Error searching reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Retrieves a full Guest and ReservationDetails object by reservation ID.
     * Updated to match the provided Guest model.
     * @param reservationId The ID of the reservation to retrieve.
     * @return A Map containing the Guest and ReservationDetails, or null if not found.
     */
    public static Map<String, Object> getReservationById(String reservationId) {
        String sql = "SELECT reservation_id, full_name, phone_number, check_in_date, " +
                "email, address, province, city, postal_code, " +
                "id_proof_type, id_proof_number, check_out_date, adults, children, single_rooms, double_rooms, " +
                "deluxe_rooms, penthouse_rooms, total_price, status, room_number, room_type " +
                "FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(reservationId));

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Construct Guest object using the available constructor
                Guest guest = new Guest(
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("province"),
                        rs.getString("city"),
                        rs.getString("postal_code"),
                        rs.getString("id_proof_type"),
                        rs.getString("id_proof_number")
                );

                // Construct ReservationDetails object using all fields
                ReservationDetails details = new ReservationDetails();
                details.setReservationId(String.valueOf(rs.getInt("reservation_id")));
                details.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
                details.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
                details.setNumberOfAdults(rs.getInt("adults"));
                details.setNumberOfChildren(rs.getInt("children"));
                details.setSingleRooms(rs.getInt("single_rooms"));
                details.setDoubleRooms(rs.getInt("double_rooms"));
                details.setDeluxeRooms(rs.getInt("deluxe_rooms"));
                details.setPenthouses(rs.getInt("penthouse_rooms"));
                details.setEstimatedPrice(rs.getDouble("total_price"));
                details.setStatus(rs.getString("status"));
                details.setRoomNumber(rs.getString("room_number"));
                details.setRoomType(rs.getString("room_type"));

                Map<String, Object> result = new HashMap<>();
                result.put("guest", guest);
                result.put("reservationDetails", details);
                return result;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservation by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing reservation record in the database.
     * Updated to match the provided Guest model.
     * @param guest The updated Guest object.
     * @param details The updated ReservationDetails object.
     * @return true if the update was successful, false otherwise.
     */
    public static boolean updateReservation(Guest guest, ReservationDetails details) {
        String sql = "UPDATE reservations SET " +
                "full_name = ?, phone_number = ?, email = ?, address = ?, province = ?, city = ?, postal_code = ?, " +
                "id_proof_type = ?, id_proof_number = ?, check_in_date = ?, check_out_date = ?, " +
                "adults = ?, children = ?, single_rooms = ?, double_rooms = ?, deluxe_rooms = ?, penthouse_rooms = ?, " +
                "total_price = ?, status = ?, room_number = ?, room_type = ? " +
                "WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Guest parameters (indices adjusted)
            pstmt.setString(1, guest.getFullName());
            pstmt.setString(2, guest.getPhoneNumber());
            pstmt.setString(3, guest.getEmail());
            pstmt.setString(4, guest.getAddress());
            pstmt.setString(5, guest.getProvince());
            pstmt.setString(6, guest.getCity());
            pstmt.setString(7, guest.getPostalCode());
            pstmt.setString(8, guest.getIdProofType());
            pstmt.setString(9, guest.getIdProofNumber());

            // ReservationDetails parameters (indices adjusted)
            pstmt.setString(10, details.getCheckInDate().toString());
            pstmt.setString(11, details.getCheckOutDate().toString());
            pstmt.setInt(12, details.getNumberOfAdults());
            pstmt.setInt(13, details.getNumberOfChildren());
            pstmt.setInt(14, details.getSingleRooms());
            pstmt.setInt(15, details.getDoubleRooms());
            pstmt.setInt(16, details.getDeluxeRooms());
            pstmt.setInt(17, details.getPenthouses());
            pstmt.setDouble(18, details.getEstimatedPrice()); // Recalculated price
            pstmt.setString(19, details.getStatus());
            pstmt.setString(20, details.getRoomNumber());
            pstmt.setString(21, details.getRoomType());
            pstmt.setInt(22, Integer.parseInt(details.getReservationId())); // WHERE clause

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Reservation ID " + details.getReservationId() + " successfully updated.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves reservations with a check-in date of today or tomorrow.
     * @return A list of ReservationDisplay objects for upcoming reservations.
     */
    public static List<ReservationDisplay> getReservationsForNext24Hours() {
        List<ReservationDisplay> results = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        String sql = "SELECT reservation_id, full_name, phone_number, check_in_date, " +
                "email, address, province, city, postal_code, " +
                "id_proof_type, id_proof_number, check_out_date, adults, children, single_rooms, double_rooms, " +
                "deluxe_rooms, penthouse_rooms, total_price, status, room_number, room_type " +
                "FROM reservations WHERE check_in_date = ? OR check_in_date = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, today.toString());
            pstmt.setString(2, tomorrow.toString());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getString("full_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("province"),
                        rs.getString("city"),
                        rs.getString("postal_code"),
                        rs.getString("id_proof_type"),
                        rs.getString("id_proof_number")
                );

                ReservationDetails details = new ReservationDetails();
                details.setReservationId(String.valueOf(rs.getInt("reservation_id")));
                details.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
                details.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
                details.setNumberOfAdults(rs.getInt("adults"));
                details.setNumberOfChildren(rs.getInt("children"));
                details.setSingleRooms(rs.getInt("single_rooms"));
                details.setDoubleRooms(rs.getInt("double_rooms"));
                details.setDeluxeRooms(rs.getInt("deluxe_rooms"));
                details.setPenthouses(rs.getInt("penthouse_rooms"));
                details.setEstimatedPrice(rs.getDouble("total_price"));
                details.setStatus(rs.getString("status"));
                details.setRoomNumber(rs.getString("room_number"));
                details.setRoomType(rs.getString("room_type"));

                results.add(new ReservationDisplay(guest, details));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving upcoming reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Inner class to represent a reservation for display in TableViews.
     * Provides convenient getters for TableColumn PropertyValueFactory.
     */
    public static class ReservationDisplay {
        private Guest guest;
        private ReservationDetails details;

        public ReservationDisplay(Guest guest, ReservationDetails details) {
            this.guest = guest;
            this.details = details;
        }

        public Guest getGuest() {
            return guest;
        }

        public ReservationDetails getDetails() {
            return details;
        }

        // Helper getters for TableView columns
        public String getGuestFullName() {
            return guest != null ? guest.getFullName() : "N/A";
        }

        public String getGuestPhoneNumber() {
            return guest != null ? guest.getPhoneNumber() : "N/A";
        }

        public String getReservationIdValue() {
            return details != null ? details.getReservationId() : "N/A";
        }

        public String getRoomDetailsSummary() {
            if (details != null) {
                StringBuilder sb = new StringBuilder();
                if (details.getSingleRooms() > 0) sb.append(details.getSingleRooms()).append("S ");
                if (details.getDoubleRooms() > 0) sb.append(details.getDoubleRooms()).append("D ");
                if (details.getDeluxeRooms() > 0) sb.append(details.getDeluxeRooms()).append("DX ");
                if (details.getPenthouses() > 0) sb.append(details.getPenthouses()).append("PH ");
                String roomSummary = sb.toString().trim();

                // Add room number if available
                if (details.getRoomNumber() != null && !details.getRoomNumber().isEmpty()) {
                    if (!roomSummary.isEmpty()) {
                        roomSummary += " (Rm: " + details.getRoomNumber() + ")";
                    } else {
                        roomSummary = "Rm: " + details.getRoomNumber();
                    }
                }
                return !roomSummary.isEmpty() ? roomSummary : "N/A";
            }
            return "N/A";
        }

        public String getCheckInDateFormatted() {
            return details != null && details.getCheckInDate() != null ?
                    details.getCheckInDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A";
        }

        public String getStatus() {
            return details != null && details.getStatus() != null ? details.getStatus() : "N/A";
        }
    }
}
