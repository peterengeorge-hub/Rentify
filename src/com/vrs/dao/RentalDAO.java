package com.vrs.dao;

import com.vrs.model.Rental;
import com.vrs.util.DBConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    /**
     * Creates a rental for a vehicle that is currently Available.
     * Dates must be supplied in yyyy-MM-dd format.
     */
    public int createRental(Rental rental) throws SQLException {
        if (rental == null) {
            throw new SQLException("Rental information is missing.");
        }

        LocalDate startDate = parseDate(rental.getStartDate(), "Start date");
        LocalDate endDate = parseDate(rental.getEndDate(), "End date");

        if (endDate.isBefore(startDate)) {
            throw new SQLException("End date cannot be before start date.");
        }

        String reserveVehicleSql = """
                UPDATE vehicles
                SET status = 'Rented'
                WHERE vehicle_id = ? AND status = 'Available'
                """;

        String insertRentalSql = """
                INSERT INTO rentals (
                    vehicle_id, customer_id, start_date, end_date,
                    actual_return_date, total_charge, late_fee, status
                )
                VALUES (?, ?, ?, ?, NULL, 0, 0, 'Active')
                """;

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Reserve the vehicle only if it is still available.
                try (PreparedStatement statement =
                             connection.prepareStatement(reserveVehicleSql)) {
                    statement.setInt(1, rental.getVehicleId());

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Vehicle not found or it is no longer available."
                        );
                    }
                }

                int rentalId;

                try (PreparedStatement statement =
                             connection.prepareStatement(insertRentalSql)) {
                    statement.setInt(1, rental.getVehicleId());
                    statement.setInt(2, rental.getCustomerId());
                    statement.setString(3, startDate.toString());
                    statement.setString(4, endDate.toString());
                    statement.executeUpdate();
                }

                // Get the ID assigned to the inserted rental on this connection.
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     "SELECT last_insert_rowid()");
                     ResultSet results = statement.executeQuery()) {

                    if (!results.next()) {
                        throw new SQLException("Could not retrieve the new rental ID.");
                    }

                    rentalId = results.getInt(1);
                }

                connection.commit();
                return rentalId;

            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    public List<Rental> getAllRentals() throws SQLException {
        String sql = """
                SELECT rental_id, vehicle_id, customer_id, start_date, end_date,
                       actual_return_date, total_charge, late_fee, status
                FROM rentals
                ORDER BY rental_id DESC
                """;

        List<Rental> rentals = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                rentals.add(readRental(results));
            }
        }

        return rentals;
    }

    public List<Rental> getActiveRentals() throws SQLException {
        String sql = """
                SELECT rental_id, vehicle_id, customer_id, start_date, end_date,
                       actual_return_date, total_charge, late_fee, status
                FROM rentals
                WHERE status = 'Active'
                ORDER BY rental_id DESC
                """;

        List<Rental> rentals = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                rentals.add(readRental(results));
            }
        }

        return rentals;
    }

    /**
     * Closes an active rental and records its charges.
     * actualReturnDate must use yyyy-MM-dd format.
     */
    public void closeRental(int rentalId, String actualReturnDate)
            throws SQLException {

        LocalDate returnDate = parseDate(actualReturnDate, "Return date");

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Rental rental = findRental(connection, rentalId);

                if (rental == null) {
                    throw new SQLException("Rental not found.");
                }

                if (!"Active".equalsIgnoreCase(rental.getStatus())) {
                    throw new SQLException("This rental has already been returned.");
                }

                LocalDate startDate = parseDate(rental.getStartDate(), "Start date");
                LocalDate endDate = parseDate(rental.getEndDate(), "End date");

                if (returnDate.isBefore(startDate)) {
                    throw new SQLException(
                            "Return date cannot be before the rental start date."
                    );
                }

                double dailyRate = findDailyRate(connection, rental.getVehicleId());

                double baseCharge = calculateCharge(
                        startDate.toString(),
                        endDate.toString(),
                        dailyRate
                );

                double lateFee = calculateLateFee(
                        endDate.toString(),
                        returnDate.toString(),
                        dailyRate
                );

                double totalCharge = roundMoney(baseCharge + lateFee);

                String updateRentalSql = """
                        UPDATE rentals
                        SET actual_return_date = ?,
                            total_charge = ?,
                            late_fee = ?,
                            status = 'Closed'
                        WHERE rental_id = ? AND status = 'Active'
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(updateRentalSql)) {
                    statement.setString(1, returnDate.toString());
                    statement.setDouble(2, totalCharge);
                    statement.setDouble(3, lateFee);
                    statement.setInt(4, rentalId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Rental could not be closed. Refresh and try again."
                        );
                    }
                }

                String releaseVehicleSql = """
                        UPDATE vehicles
                        SET status = 'Available'
                        WHERE vehicle_id = ? AND status = 'Rented'
                        """;

                try (PreparedStatement statement =
                             connection.prepareStatement(releaseVehicleSql)) {
                    statement.setInt(1, rental.getVehicleId());

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Rental was not closed because the vehicle status "
                                        + "could not be updated."
                        );
                    }
                }

                connection.commit();

            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        }
    }

    /**
     * Charges the planned rental period, with a minimum charge of one day.
     */
    public double calculateCharge(String start, String end, double dailyRate) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }

        validateDailyRate(dailyRate);

        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));

        return roundMoney(dailyRate * days);
    }

    /**
     * Late fee rule: 1.5 times the daily rate for each day returned late.
     */
    public double calculateLateFee(
            String endDate,
            String actualReturnDate,
            double dailyRate
    ) {
        LocalDate plannedEndDate = LocalDate.parse(endDate);
        LocalDate returnDate = LocalDate.parse(actualReturnDate);

        validateDailyRate(dailyRate);

        if (!returnDate.isAfter(plannedEndDate)) {
            return 0.0;
        }

        long lateDays = ChronoUnit.DAYS.between(plannedEndDate, returnDate);
        double fee = lateDays * dailyRate * 1.5;

        return roundMoney(fee);
    }

    private Rental findRental(Connection connection, int rentalId)
            throws SQLException {

        String sql = """
                SELECT rental_id, vehicle_id, customer_id, start_date, end_date,
                       actual_return_date, total_charge, late_fee, status
                FROM rentals
                WHERE rental_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, rentalId);

            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? readRental(results) : null;
            }
        }
    }

    private double findDailyRate(Connection connection, int vehicleId)
            throws SQLException {

        String sql = """
                SELECT daily_rate
                FROM vehicles
                WHERE vehicle_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, vehicleId);

            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new SQLException("The rental's vehicle was not found.");
                }

                double dailyRate = results.getDouble("daily_rate");
                validateDailyRate(dailyRate);
                return dailyRate;
            }
        }
    }

    private Rental readRental(ResultSet results) throws SQLException {
        return new Rental(
                results.getInt("rental_id"),
                results.getInt("vehicle_id"),
                results.getInt("customer_id"),
                results.getString("start_date"),
                results.getString("end_date"),
                results.getString("actual_return_date"),
                results.getDouble("total_charge"),
                results.getDouble("late_fee"),
                results.getString("status")
        );
    }

    private LocalDate parseDate(String value, String fieldName)
            throws SQLException {

        if (value == null || value.isBlank()) {
            throw new SQLException(fieldName + " is required.");
        }

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new SQLException(
                    fieldName + " must use yyyy-MM-dd format.",
                    exception
            );
        }
    }

    private void validateDailyRate(double dailyRate) {
        if (!Double.isFinite(dailyRate) || dailyRate <= 0) {
            throw new IllegalArgumentException(
                    "Daily rate must be a positive number."
            );
        }
    }

    private double roundMoney(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void rollback(Connection connection, Exception originalException) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(rollbackException);
        }
    }
}