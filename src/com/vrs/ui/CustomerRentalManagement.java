package com.vrs.ui;

import com.vrs.dao.CustomerDAO;
import com.vrs.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class CustomerRentalManagement extends JPanel implements ActionListener {

    private final JTextField name = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField licenseNumber = new JTextField();
    private final JTextField search = new JTextField(15);

    private final JButton addButton = new JButton("Add");
    private final JButton updateButton = new JButton("Update");
    private final JButton deleteButton = new JButton("Delete");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton searchButton = new JButton("Search");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Phone", "Email", "License Number"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);
    private final CustomerDAO customerDAO = new CustomerDAO();

    public CustomerRentalManagement() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Customer Management"));
        top.add(new JLabel("Search by name or customer ID:"));
        top.add(search);
        top.add(searchButton);

        searchButton.addActionListener(e -> searchCustomers());
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("Phone"));
        form.add(phone);

        form.add(new JLabel("Email"));
        form.add(email);
        form.add(new JLabel("License Number"));
        form.add(licenseNumber);

        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);
        form.add(refreshButton);
        add(form, BorderLayout.SOUTH);

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        refreshButton.addActionListener(this);

        search.addActionListener(e -> searchCustomers());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomer();
            }
        });

        loadCustomers();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addCustomer();
        } else if (e.getSource() == updateButton) {
            updateCustomer();
        } else if (e.getSource() == deleteButton) {
            deleteCustomer();
        } else if (e.getSource() == refreshButton) {
            search.setText("");
            loadCustomers();
        }
    }

    private void addCustomer() {
        if (!isFormValid()) {
            return;
        }

        try {
            Customer customer = new Customer(
                    0, name.getText().trim(), phone.getText().trim(),
                    email.getText().trim(), licenseNumber.getText().trim()
            );

            customer.setCustomerId(customerDAO.addCustomer(customer));
            addCustomerToTable(customer);
            clearForm();
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void updateCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer to update.");
            return;
        }
        if (!isFormValid()) {
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Customer customer = new Customer(
                    id, name.getText().trim(), phone.getText().trim(),
                    email.getText().trim(), licenseNumber.getText().trim()
            );

            customerDAO.updateCustomer(customer);
            showCustomerInTable(row, customer);
            clearForm();
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void deleteCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer to delete.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);

        try {
            customerDAO.deleteCustomer(id);
            tableModel.removeRow(row);
            clearForm();
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadCustomers() {
        try {
            displayCustomers(customerDAO.getAllCustomers());
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void searchCustomers() {
        try {
            displayCustomers(customerDAO.searchByName(search.getText().trim()));
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void displayCustomers(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer customer : customers) {
            addCustomerToTable(customer);
        }
    }

    private void addCustomerToTable(Customer customer) {
        tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getLicenseNumber()
        });
    }

    private void showCustomerInTable(int row, Customer customer) {
        tableModel.setValueAt(customer.getName(), row, 1);
        tableModel.setValueAt(customer.getPhone(), row, 2);
        tableModel.setValueAt(customer.getEmail(), row, 3);
        tableModel.setValueAt(customer.getLicenseNumber(), row, 4);
    }

    private void loadSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        name.setText(tableModel.getValueAt(row, 1).toString());
        phone.setText(tableModel.getValueAt(row, 2).toString());
        email.setText(tableModel.getValueAt(row, 3).toString());
        licenseNumber.setText(tableModel.getValueAt(row, 4).toString());
    }

    private boolean isFormValid() {
    	if (!phone.getText().trim().matches("\\d{10}")) {
    	    JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
    	    return false;
    	}

    	String emailText = email.getText().trim();
    	if (!emailText.isEmpty()
    	        && !emailText.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
    	    JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
    	    return false;
    	}
    	    String license = licenseNumber.getText()
    	            .trim()
    	            .toUpperCase()
    	            .replaceAll("[\\s/-]", "");

    	    if (!license.matches("[A-Z0-9]{8,20}")) {
    	        JOptionPane.showMessageDialog(
    	                this,
    	                "Enter a valid licence number (8–20 letters or digits)."
    	        );
    	        return false;
    	    
    	}
        return true;
    }

    private void clearForm() {
        name.setText("");
        phone.setText("");
        email.setText("");
        licenseNumber.setText("");
        table.clearSelection();
    }

    private void showDatabaseError(SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Customer Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new CustomerRentalManagement());
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}