package com.vrs.ui;

import com.vrs.dao.CustomerDAO;
import com.vrs.dao.RentalDAO;
import com.vrs.model.Customer;
import com.vrs.model.Rental;
import com.vrs.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalPanel extends JPanel {

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter DATABASE_DATE =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private final JComboBox<String> vehicleBox = new JComboBox<>();
    private final JComboBox<String> customerBox = new JComboBox<>();

    private final JSpinner startSpinner;
    private final JSpinner endSpinner;
    private final JSpinner returnDateSpinner;

    private final JButton createButton = new JButton("Create Rental");
    private final JButton returnButton = new JButton("Return Vehicle");
    private final JButton refreshButton = new JButton("Refresh");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{
                    "Rental ID",
                    "Vehicle ID",
                    "Customer ID",
                    "Start",
                    "End",
                    "Return Date",
                    "Total Charge",
                    "Late Fee",
                    "Status"
            },
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final RentalDAO rentalDAO = new RentalDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final Map<String, Integer> vehicleMap = new HashMap<>();
    private final Map<String, Integer> customerMap = new HashMap<>();

    public RentalPanel() {
        setLayout(new BorderLayout(10, 10));

        Date today = new Date();

        startSpinner = new JSpinner(new SpinnerDateModel(
                today, null, null, java.util.Calendar.DAY_OF_MONTH));

        endSpinner = new JSpinner(new SpinnerDateModel(
                today, null, null, java.util.Calendar.DAY_OF_MONTH));

        returnDateSpinner = new JSpinner(new SpinnerDateModel(
                today, null, null, java.util.Calendar.DAY_OF_MONTH));

        startSpinner.setEditor(
                new JSpinner.DateEditor(startSpinner, "dd/MM/yyyy"));
        endSpinner.setEditor(
                new JSpinner.DateEditor(endSpinner, "dd/MM/yyyy"));
        returnDateSpinner.setEditor(
                new JSpinner.DateEditor(returnDateSpinner, "dd/MM/yyyy"));

        JLabel title = new JLabel("Rental Management");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(title);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));

        form.add(new JLabel("Vehicle (Available)"));
        form.add(vehicleBox);
        form.add(new JLabel("Customer"));
        form.add(customerBox);

        form.add(new JLabel("Start Date"));
        form.add(startSpinner);
        form.add(new JLabel("End Date"));
        form.add(endSpinner);

        form.add(new JLabel("Actual Return Date"));
        form.add(returnDateSpinner);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        form.add(createButton);
        form.add(returnButton);
        form.add(refreshButton);
        form.add(new JLabel(""));

        add(form, BorderLayout.SOUTH);

        createButton.addActionListener(event -> createRental());
        returnButton.addActionListener(event -> returnVehicle());
        refreshButton.addActionListener(event -> refreshData());

        refreshData();
    }

    private void createRental() {
        Integer vehicleId = vehicleMap.get(vehicleBox.getSelectedItem());
        Integer customerId = customerMap.get(customerBox.getSelectedItem());

        if (vehicleId == null || customerId == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an available vehicle and a customer."
            );
            return;
        }

        try {
            startSpinner.commitEdit();
            endSpinner.commitEdit();

            LocalDate startDate =
                    toLocalDate((Date) startSpinner.getValue());
            LocalDate endDate =
                    toLocalDate((Date) endSpinner.getValue());

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(
                        this,
                        "End date cannot be before start date."
                );
                return;
            }

            String startForDatabase = startDate.format(DATABASE_DATE);
            String endForDatabase = endDate.format(DATABASE_DATE);

            Rental rental = new Rental(
                    0,
                    vehicleId,
                    customerId,
                    startForDatabase,
                    endForDatabase,
                    null,
                    0,
                    0,
                    "Active"
            );

            int rentalId = rentalDAO.createRental(rental);

            refreshData();

            JOptionPane.showMessageDialog(
                    this,
                    "Rental created. ID: " + rentalId
            );

        } catch (ParseException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter the start and end dates in dd/MM/yyyy format."
            );
        } catch (SQLException exception) {
            showDatabaseError("Could not create rental", exception);
        }
    }

    private void returnVehicle() {
        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an active rental first."
            );
            return;
        }

        int row = table.convertRowIndexToModel(selectedViewRow);

        if (!"Active".equalsIgnoreCase(valueAt(row, 8))) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only active rentals can be returned."
            );
            return;
        }

        int rentalId;
        try {
            rentalId = ((Number) tableModel.getValueAt(row, 0)).intValue();
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "The selected rental has an invalid ID."
            );
            return;
        }

        try {
            returnDateSpinner.commitEdit();

            LocalDate returnDate =
                    toLocalDate((Date) returnDateSpinner.getValue());

            LocalDate startDate = LocalDate.parse(
                    valueAt(row, 3),
                    DISPLAY_DATE
            );

            if (returnDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Return date cannot be before the rental start date."
                );
                return;
            }

            if (returnDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Return date cannot be in the future."
                );
                return;
            }

            String returnDateForDatabase =
                    returnDate.format(DATABASE_DATE);

            String vehicleId = valueAt(row, 1);
            String customerId = valueAt(row, 2);
            String plannedStart = valueAt(row, 3);
            String plannedEnd = valueAt(row, 4);

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Return rental " + rentalId + " on "
                            + returnDate.format(DISPLAY_DATE) + "?",
                    "Confirm Return",
                    JOptionPane.YES_NO_OPTION
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            rentalDAO.closeRental(rentalId, returnDateForDatabase);
            refreshData();

            String receipt = "===== RECEIPT =====\n"
                    + "Rental ID: " + rentalId + "\n"
                    + "Vehicle ID: " + vehicleId + "\n"
                    + "Customer ID: " + customerId + "\n"
                    + "Start: " + plannedStart + "\n"
                    + "Planned End: " + plannedEnd + "\n"
                    + "Actual Return: "
                    + returnDate.format(DISPLAY_DATE) + "\n"
                    + "===================";

            JOptionPane.showMessageDialog(
                    this,
                    receipt,
                    "Return Receipt",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (ParseException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid return date in dd/MM/yyyy format."
            );
        } catch (DateTimeParseException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not read a rental date. Refresh the table and try again."
            );
        } catch (SQLException exception) {
            showDatabaseError("Could not return vehicle", exception);
        }
    }

    private void refreshData() {
        loadDropdowns();
        loadTable();
    }

    private void loadDropdowns() {
        vehicleBox.removeAllItems();
        customerBox.removeAllItems();
        vehicleMap.clear();
        customerMap.clear();

        String vehicleSql = """
                SELECT vehicle_id, make, model, reg_number
                FROM vehicles
                WHERE status = 'Available'
                ORDER BY vehicle_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(vehicleSql);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                int vehicleId = results.getInt("vehicle_id");

                String label = vehicleId + " - "
                        + results.getString("make") + " "
                        + results.getString("model") + " ("
                        + results.getString("reg_number") + ")";

                vehicleBox.addItem(label);
                vehicleMap.put(label, vehicleId);
            }
        } catch (SQLException exception) {
            showDatabaseError("Failed to load available vehicles", exception);
        }

        try {
            List<Customer> customers = customerDAO.getAllCustomers();

            for (Customer customer : customers) {
                String label = customer.getCustomerId()
                        + " - " + customer.getName();

                customerBox.addItem(label);
                customerMap.put(label, customer.getCustomerId());
            }
        } catch (SQLException exception) {
            showDatabaseError("Failed to load customers", exception);
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);

        try {
            List<Rental> rentals = rentalDAO.getAllRentals();

            for (Rental rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getRentalId(),
                        rental.getVehicleId(),
                        rental.getCustomerId(),
                        formatDatabaseDate(rental.getStartDate()),
                        formatDatabaseDate(rental.getEndDate()),
                        formatDatabaseDate(rental.getActualReturnDate()),
                        rental.getTotalCharge(),
                        rental.getLateFee(),
                        rental.getStatus()
                });
            }
        } catch (SQLException exception) {
            showDatabaseError("Failed to load rentals", exception);
        }
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String formatDatabaseDate(String date) {
        if (date == null || date.isBlank()) {
            return "";
        }

        try {
            return LocalDate.parse(date, DATABASE_DATE)
                    .format(DISPLAY_DATE);
        } catch (DateTimeParseException exception) {
            return date;
        }
    }

    private String valueAt(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private void showDatabaseError(String action, SQLException exception) {
        JOptionPane.showMessageDialog(
                this,
                action + ": " + exception.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}