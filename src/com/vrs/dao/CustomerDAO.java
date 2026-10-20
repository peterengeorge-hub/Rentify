package com.vrs.dao;

import com.vrs.model.Customer;
import com.vrs.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public int addCustomer(Customer customer) throws SQLException {
        String insertSql = "INSERT INTO customers "
                + "(name, phone, email, license_number) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getPhone());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getLicenseNumber());
                ps.executeUpdate();
            }

            try (PreparedStatement ps =
                         con.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Could not get the new customer ID.");
    }

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, license_number "
                + "FROM customers ORDER BY customer_id";

        List<Customer> customers = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(readCustomer(rs));
            }
        }

        return customers;
    }

    public List<Customer> searchByName(String keyword) throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, license_number "
                + "FROM customers "
                + "WHERE name LIKE ? OR CAST(customer_id AS TEXT) LIKE ? "
                + "ORDER BY customer_id";

        List<Customer> customers = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String searchText = "%" + keyword + "%";
            ps.setString(1, searchText);
            ps.setString(2, searchText);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customers.add(readCustomer(rs));
                }
            }
        }

        return customers;
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers "
                + "SET name = ?, phone = ?, email = ?, license_number = ? "
                + "WHERE customer_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getLicenseNumber());
            ps.setInt(5, customer.getCustomerId());
            ps.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }

    private Customer readCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("license_number")
        );
    }
}