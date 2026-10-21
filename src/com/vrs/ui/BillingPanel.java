package com.vrs.ui;

import com.vrs.util.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BillingPanel extends JPanel {

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JTextField rentalIdField = new JTextField(10);
    private final JButton generateButton = new JButton("Generate Bill");
    private final JButton clearButton = new JButton("Clear");
    private final JTextArea billArea = new JTextArea();

    public BillingPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Completed Rental ID:"));
        searchPanel.add(rentalIdField);
        searchPanel.add(generateButton);
        searchPanel.add(clearButton);
        add(searchPanel, BorderLayout.NORTH);

        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(billArea), BorderLayout.CENTER);

        generateButton.addActionListener(event -> generateBill());
        rentalIdField.addActionListener(event -> generateBill());
        clearButton.addActionListener(event -> {
            rentalIdField.setText("");
            billArea.setText("");
        });
    }

    private void generateBill() {
        int rentalId;

        try {
            rentalId = Integer.parseInt(rentalIdField.getText().trim());
            if (rentalId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid rental ID."
            );
            return;
        }

        String sql = """
                SELECT r.rental_id,
                       r.start_date,
                       r.end_date,
                       r.actual_return_date,
                       r.total_charge,
                       r.late_fee,
                       c.name AS customer_name,
                       v.make,
                       v.model,
                       v.reg_number
                FROM rentals r
                JOIN customers c ON c.customer_id = r.customer_id
                JOIN vehicles v ON v.vehicle_id = r.vehicle_id
                WHERE r.rental_id = ?
                  AND r.status = 'Closed'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, rentalId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    billArea.setText("");
                    JOptionPane.showMessageDialog(
                            this,
                            "No completed rental found with that ID."
                    );
                    return;
                }

                String start = result.getString("start_date");
                String end = result.getString("end_date");
                String actualReturn = result.getString("actual_return_date");

                double total = result.getDouble("total_charge");
                double lateFee = result.getDouble("late_fee");
                double rentalCharge = Math.max(0, total - lateFee);

                long days = ChronoUnit.DAYS.between(
                        LocalDate.parse(start),
                        LocalDate.parse(end)
                );
                days = Math.max(1, days);

                showBill(
                        result.getInt("rental_id"),
                        result.getString("customer_name"),
                        result.getString("make"),
                        result.getString("model"),
                        result.getString("reg_number"),
                        start,
                        end,
                        actualReturn,
                        days,
                        rentalCharge,
                        lateFee,
                        total
                );
            }

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not generate bill: " + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not read the rental dates: " + exception.getMessage(),
                    "Billing Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showBill(
            int rentalId,
            String customerName,
            String make,
            String model,
            String regNumber,
            String start,
            String end,
            String actualReturn,
            long days,
            double rentalCharge,
            double lateFee,
            double total
    ) {
        StringBuilder bill = new StringBuilder();

        bill.append("          VEHICLE RENTAL BILL\n");
        bill.append("----------------------------------------\n");
        bill.append("Rental ID:       ").append(rentalId).append("\n");
        bill.append("Customer:        ").append(customerName).append("\n");
        bill.append("Vehicle:         ").append(make).append(" ")
                .append(model).append("\n");
        bill.append("Registration:    ").append(regNumber).append("\n");
        bill.append("Start Date:      ").append(displayDate(start)).append("\n");
        bill.append("Planned End:     ").append(displayDate(end)).append("\n");
        bill.append("Return Date:     ").append(displayDate(actualReturn)).append("\n");
        bill.append("Rental Days:     ").append(days).append("\n");
        bill.append("----------------------------------------\n");
        bill.append("Rental Charge:   ").append(money(rentalCharge)).append("\n");
        bill.append("Late Fee:        ").append(money(lateFee)).append("\n");
        bill.append("----------------------------------------\n");
        bill.append("TOTAL:           ").append(money(total)).append("\n");
        bill.append("----------------------------------------\n");
        bill.append("Thank you!\n");

        billArea.setText(bill.toString());
        billArea.setCaretPosition(0);
    }

    private String displayDate(String date) {
        if (date == null || date.isBlank()) {
            return "";
        }

        return LocalDate.parse(date).format(DISPLAY_DATE);
    }

    private String money(double amount) {
        return "₹" + String.format("%,.2f", amount);
    }
}