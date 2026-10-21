package com.vrs.dao;

import com.vrs.model.Rental;
import com.vrs.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    public int createRental(Rental r) throws SQLException {
        String sql = "INSERT INTO rentals (vehicle_id, customer_id, start_date, end_date, status) "
                + "VALUES (?, ?, ?, ?, 'Active')";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, r.getVehicleId());
                ps.setInt(2, r.getCustomerId());
                ps.setString(3, r.getStartDate());
                ps.setString(4, r.getEndDate());
                ps.executeUpdate();
            }

            int rentalId;
            try (PreparedStatement ps = con.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    rentalId = rs.getInt(1);
                } else {
                    throw new SQLException("Could not get rental ID");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE vehicles SET status = 'Rented' WHERE vehicle_id = ?")) {
                ps.setInt(1, r.getVehicleId());
                ps.executeUpdate();
            }

            return rentalId;
        }
    }

    public List<Rental> getAllRentals() throws SQLException {
        String sql = "SELECT * FROM rentals ORDER BY rental_id DESC";
        List<Rental> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(readRental(rs));
            }
        }
        return list;
    }

    public List<Rental> getActiveRentals() throws SQLException {
        String sql = "SELECT * FROM rentals WHERE status = 'Active' ORDER BY rental_id DESC";
        List<Rental> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(readRental(rs));
            }
        }
        return list;
    }

    public void closeRental(int rentalId, String actualReturnDate) throws SQLException {
        Rental rental = null;
        double dailyRate = 0;

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM rentals WHERE rental_id = ?")) {
                ps.setInt(1, rentalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        rental = readRental(rs);
                    }
                }
            }

            if (rental == null) {
                throw new SQLException("Rental not found");
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT daily_rate FROM vehicles WHERE vehicle_id = ?")) {
                ps.setInt(1, rental.getVehicleId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dailyRate = rs.getDouble("daily_rate");
                    }
                }
            }

            double base = calculateCharge(rental.getStartDate(), rental.getEndDate(), dailyRate);
            double late = calculateLateFee(rental.getEndDate(), actualReturnDate, dailyRate);
            double total = base + late;

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE rentals SET actual_return_date = ?, total_charge = ?, "
                            + "late_fee = ?, status = 'Closed' WHERE rental_id = ?")) {
                ps.setString(1, actualReturnDate);
                ps.setDouble(2, total);
                ps.setDouble(3, late);
                ps.setInt(4, rentalId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE vehicles SET status = 'Available' WHERE vehicle_id = ?")) {
                ps.setInt(1, rental.getVehicleId());
                ps.executeUpdate();
            }
        }
    }

    public double calculateCharge(String start, String end, double dailyRate) {
        LocalDate s = LocalDate.parse(start);
        LocalDate e = LocalDate.parse(end);
        long days = ChronoUnit.DAYS.between(s, e);
        if (days < 1) days = 1;
        return days * dailyRate;
    }

    public double calculateLateFee(String endDate, String actualReturn, double dailyRate) {
        LocalDate end = LocalDate.parse(endDate);
        LocalDate actual = LocalDate.parse(actualReturn);
        if (!actual.isAfter(end)) {
            return 0;
        }
        long lateDays = ChronoUnit.DAYS.between(end, actual);
        return lateDays * dailyRate * 1.5;
    }

    private Rental readRental(ResultSet rs) throws SQLException {
        return new Rental(
                rs.getInt("rental_id"),
                rs.getInt("vehicle_id"),
                rs.getInt("customer_id"),
                rs.getString("start_date"),
                rs.getString("end_date"),
                rs.getString("actual_return_date"),
                rs.getDouble("total_charge"),
                rs.getDouble("late_fee"),
                rs.getString("status")
        );
    }
}