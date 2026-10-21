package com.vrs.dao;

import com.vrs.model.Expense;
import com.vrs.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public int addExpense(Expense e) throws SQLException {
        String sql = "INSERT INTO expenses (vehicle_id, description, amount, expense_date) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, e.getVehicleId());
                ps.setString(2, e.getDescription());
                ps.setDouble(3, e.getAmount());
                ps.setString(4, e.getExpenseDate());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Could not get expense ID");
    }

    public List<Expense> getAllExpenses() throws SQLException {
        String sql = "SELECT * FROM expenses ORDER BY expense_id DESC";
        List<Expense> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Expense(
                        rs.getInt("expense_id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("expense_date")
                ));
            }
        }
        return list;
    }

    public double getTotalExpenses() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double getTotalIncome() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_charge), 0) FROM rentals WHERE status = 'Closed'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }
}