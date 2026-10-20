package com.vrs.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.vrs.model.Vehicle;
import com.vrs.util.DBConnection;
import java.sql.ResultSet;

public class VehicleDAO {

    public int addVehicle(Vehicle v) throws SQLException {

   String sql = "INSERT INTO vehicles (make, model, type, reg_number, daily_rate, status, insurance_expiry, registration_expiry) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, v.getMake());
            ps.setString(2, v.getModel());
            ps.setString(3, v.getType());
            ps.setString(4, v.getRegNumber());
            ps.setDouble(5, v.getDailyRate());
            ps.setString(6, v.getStatus());
            ps.setString(7, v.getInsuranceExpiry());
            ps.setString(8, v.getRegistrationExpiry());

            ps.executeUpdate();
            

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("Failed to retrieve vehicle ID.");
        }
    }}