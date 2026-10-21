package com.vrs.ui;

import com.vrs.dao.CustomerDAO;
import com.vrs.dao.RentalDAO;
import com.vrs.model.Customer;
import com.vrs.model.Rental;
import com.vrs.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalPanel extends JPanel implements ActionListener {

    private JComboBox<String> vehicleBox = new JComboBox<>();
    private JComboBox<String> customerBox = new JComboBox<>();
    private JSpinner startSpinner;
    private JSpinner endSpinner;

    private JButton createButton = new JButton("Create Rental");
    private JButton returnButton = new JButton("Return Vehicle");
    private JButton refreshButton = new JButton("Refresh");

    private DefaultTableModel tableModel;
    private JTable table;

    private RentalDAO rentalDAO = new RentalDAO();
    private CustomerDAO customerDAO = new CustomerDAO();

    private Map<String, Integer> vehicleMap = new HashMap<>();
    private Map<String, Integer> customerMap = new HashMap<>();

    public RentalPanel() {
        setLayout(new BorderLayout(10, 10));

        SpinnerDateModel startModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        SpinnerDateModel endModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        startSpinner = new JSpinner(startModel);
        endSpinner = new JSpinner(endModel);
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "yyyy-MM-dd"));
        endSpinner.setEditor(new JSpinner.DateEditor(endSpinner, "yyyy-MM-dd"));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Rental Management"));
        add(top, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Vehicle ID", "Customer ID", "Start", "End",
                "Return Date", "Total Charge", "Late Fee", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.add(new JLabel("Vehicle (Available)"));
        form.add(vehicleBox);
        form.add(new JLabel("Customer"));
        form.add(customerBox);
        form.add(new JLabel("Start Date"));
        form.add(startSpinner);
        form.add(new JLabel("End Date"));
        form.add(endSpinner);
        form.add(createButton);
        form.add(returnButton);
        form.add(refreshButton);
        form.add(new JLabel(""));
        add(form, BorderLayout.SOUTH);

        createButton.addActionListener(this);
        returnButton.addActionListener(this);
        refreshButton.addActionListener(this);

        loadDropdowns();
        loadTable();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            createRental();
        } else if (e.getSource() == returnButton) {
            returnVehicle();
        } else if (e.getSource() == refreshButton) {
            loadDropdowns();
            loadTable();
        }
    }

    private void createRental() {
        if (vehicleBox.getSelectedItem() == null || customerBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select vehicle and customer");
            return;
        }

        try {
            String vKey = vehicleBox.getSelectedItem().toString();
            String cKey = customerBox.getSelectedItem().toString();
            int vehicleId = vehicleMap.get(vKey);
            int customerId = customerMap.get(cKey);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String start = sdf.format((Date) startSpinner.getValue());
            String end = sdf.format((Date) endSpinner.getValue());

            if (LocalDate.parse(end).isBefore(LocalDate.parse(start))) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date");
                return;
            }

            Rental r = new Rental(0, vehicleId, customerId, start, end, null, 0, 0, "Active");
            int id = rentalDAO.createRental(r);
            r.setRentalId(id);

            loadDropdowns();
            loadTable();
            JOptionPane.showMessageDialog(this, "Rental created. ID: " + id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnVehicle() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an active rental first");
            return;
        }

        String status = tableModel.getValueAt(row, 8).toString();
        if (!"Active".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only active rentals can be returned");
            return;
        }

        int rentalId = (int) tableModel.getValueAt(row, 0);
        String today = LocalDate.now().toString();

        try {
            rentalDAO.closeRental(rentalId, today);

            // simple receipt
            String receipt = "===== RECEIPT =====\n"
                    + "Rental ID: " + rentalId + "\n"
                    + "Vehicle ID: " + tableModel.getValueAt(row, 1) + "\n"
                    + "Customer ID: " + tableModel.getValueAt(row, 2) + "\n"
                    + "Start: " + tableModel.getValueAt(row, 3) + "\n"
                    + "Planned End: " + tableModel.getValueAt(row, 4) + "\n"
                    + "Actual Return: " + today + "\n"
                    + "===================";

            JOptionPane.showMessageDialog(this, receipt, "Return Receipt", JOptionPane.INFORMATION_MESSAGE);

            loadDropdowns();
            loadTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDropdowns() {
        vehicleBox.removeAllItems();
        customerBox.removeAllItems();
        vehicleMap.clear();
        customerMap.clear();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT vehicle_id, make, model, reg_number FROM vehicles WHERE status = 'Available'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getInt("vehicle_id") + " - " + rs.getString("make")
                        + " " + rs.getString("model") + " (" + rs.getString("reg_number") + ")";
                vehicleBox.addItem(label);
                vehicleMap.put(label, rs.getInt("vehicle_id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load vehicles: " + ex.getMessage());
        }

        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer c : customers) {
                String label = c.getCustomerId() + " - " + c.getName();
                customerBox.addItem(label);
                customerMap.put(label, c.getCustomerId());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage());
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Rental> list = rentalDAO.getAllRentals();
            for (Rental r : list) {
                tableModel.addRow(new Object[]{
                        r.getRentalId(),
                        r.getVehicleId(),
                        r.getCustomerId(),
                        r.getStartDate(),
                        r.getEndDate(),
                        r.getActualReturnDate() == null ? "" : r.getActualReturnDate(),
                        r.getTotalCharge(),
                        r.getLateFee(),
                        r.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rentals: " + ex.getMessage());
        }
    }
}