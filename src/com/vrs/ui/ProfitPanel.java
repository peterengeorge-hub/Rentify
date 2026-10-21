package com.vrs.ui;

import com.vrs.dao.ExpenseDAO;
import com.vrs.model.Expense;
import com.vrs.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfitPanel extends JPanel implements ActionListener {

    private JLabel incomeLabel = new JLabel("Total Income: 0.00");
    private JLabel expenseLabel = new JLabel("Total Expenses: 0.00");
    private JLabel profitLabel = new JLabel("Net Profit: 0.00");

    private JComboBox<String> vehicleBox = new JComboBox<>();
    private JTextField descField = new JTextField();
    private JTextField amountField = new JTextField();
    private JTextField dateField = new JTextField(LocalDate.now().toString());

    private JButton addExpenseButton = new JButton("Add Expense");
    private JButton refreshButton = new JButton("Refresh");

    private DefaultTableModel tableModel;
    private JTable table;

    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private Map<String, Integer> vehicleMap = new HashMap<>();

    public ProfitPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new GridLayout(1, 3, 20, 10));
        incomeLabel.setFont(incomeLabel.getFont().deriveFont(16f));
        expenseLabel.setFont(expenseLabel.getFont().deriveFont(16f));
        profitLabel.setFont(profitLabel.getFont().deriveFont(16f));
        top.add(incomeLabel);
        top.add(expenseLabel);
        top.add(profitLabel);
        add(top, BorderLayout.NORTH);

        String[] cols = {"Expense ID", "Vehicle ID", "Description", "Amount", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 5, 10, 10));
        form.add(new JLabel("Vehicle"));
        form.add(vehicleBox);
        form.add(new JLabel("Description"));
        form.add(descField);
        form.add(new JLabel("Amount"));
        form.add(amountField);
        form.add(new JLabel("Date (yyyy-MM-dd)"));
        form.add(dateField);
        form.add(addExpenseButton);
        form.add(refreshButton);
        add(form, BorderLayout.SOUTH);

        addExpenseButton.addActionListener(this);
        refreshButton.addActionListener(this);

        loadVehicles();
        loadData();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addExpenseButton) {
            addExpense();
        } else if (e.getSource() == refreshButton) {
            loadVehicles();
            loadData();
        }
    }

    private void addExpense() {
        if (vehicleBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle");
            return;
        }
        if (descField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter description");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid amount");
            return;
        }

        try {
            String key = vehicleBox.getSelectedItem().toString();
            int vehicleId = vehicleMap.get(key);
            String date = dateField.getText().trim();

            Expense exp = new Expense(0, vehicleId, descField.getText().trim(), amount, date);
            int id = expenseDAO.addExpense(exp);
            exp.setExpenseId(id);

            descField.setText("");
            amountField.setText("");
            dateField.setText(LocalDate.now().toString());
            loadData();
            JOptionPane.showMessageDialog(this, "Expense added");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVehicles() {
        vehicleBox.removeAllItems();
        vehicleMap.clear();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT vehicle_id, make, model, reg_number FROM vehicles");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String label = rs.getInt("vehicle_id") + " - " + rs.getString("make")
                        + " " + rs.getString("model") + " (" + rs.getString("reg_number") + ")";
                vehicleBox.addItem(label);
                vehicleMap.put(label, rs.getInt("vehicle_id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load vehicles");
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<Expense> list = expenseDAO.getAllExpenses();
            for (Expense e : list) {
                tableModel.addRow(new Object[]{
                        e.getExpenseId(),
                        e.getVehicleId(),
                        e.getDescription(),
                        e.getAmount(),
                        e.getExpenseDate()
                });
            }

            double income = expenseDAO.getTotalIncome();
            double expenses = expenseDAO.getTotalExpenses();
            double profit = income - expenses;

            incomeLabel.setText("Total Income: " + String.format("%.2f", income));
            expenseLabel.setText("Total Expenses: " + String.format("%.2f", expenses));
            profitLabel.setText("Net Profit: " + String.format("%.2f", profit));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + ex.getMessage());
        }
    }
}