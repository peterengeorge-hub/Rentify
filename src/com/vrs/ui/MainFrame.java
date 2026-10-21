package com.vrs.ui;


import com.vrs.util.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Vehicle Rental System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicles", new VehiclePanel());
        tabs.addTab("Customers", new CustomerRentalManagement());
        tabs.addTab("Rentals", new RentalPanel());
        tabs.addTab("Profit Summary", new ProfitPanel());

        add(tabs);

        checkExpiry();
    }

    private void checkExpiry() {
        String sql = "SELECT make, model, reg_number, insurance_expiry, registration_expiry "
                + "FROM vehicles WHERE "
                + "insurance_expiry <= date('now', '+30 days') "
                + "OR registration_expiry <= date('now', '+30 days')";

        StringBuilder msg = new StringBuilder();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                msg.append(rs.getString("make")).append(" ")
                        .append(rs.getString("model"))
                        .append(" (").append(rs.getString("reg_number")).append(") - ")
                        .append("Ins: ").append(rs.getString("insurance_expiry"))
                        .append(", Reg: ").append(rs.getString("registration_expiry"))
                        .append("\n");
            }
        } catch (SQLException e) {
            return;
        }

        if (msg.length() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Vehicles with insurance/registration expiring within 30 days:\n\n" + msg,
                    "Expiry Alert",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}