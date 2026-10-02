package com.vrs.ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VehiclePanel extends JPanel {

    JTextField make = new JTextField();
    JTextField model = new JTextField();
    JTextField regNo = new JTextField();
    JTextField rate = new JTextField();
    JTextField search = new JTextField(10);

    JComboBox<String> type = new JComboBox<>(
            new String[]{"Sedan", "SUV", "Hatchback", "Van", "Bike"});

    JComboBox<String> status = new JComboBox<>(
            new String[]{"Available", "Rented", "Maintenance"});

    JComboBox<String> filter = new JComboBox<>(
            new String[]{"All", "Available", "Rented", "Maintenance"});

    JButton addButton = new JButton("Add");
    JButton updateButton = new JButton("Update");
    JButton deleteButton = new JButton("Delete");
    JButton clearButton = new JButton("Clear");

    JTable table;
    DefaultTableModel tableModel;

    public VehiclePanel() {

        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();

        top.add(new JLabel("Vehicle Management"));
        top.add(new JLabel("Search:"));
        top.add(search);
        top.add(new JLabel("Status:"));
        top.add(filter);

        add(top, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Make", "Model", "Type",
                "Reg Number", "Daily Rate", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));

        form.add(new JLabel("Make"));
        form.add(make);

        form.add(new JLabel("Model"));
        form.add(model);

        form.add(new JLabel("Type"));
        form.add(type);

        form.add(new JLabel("Reg Number"));
        form.add(regNo);

        form.add(new JLabel("Daily Rate"));
        form.add(rate);
        
        form.add(new JLabel("Status"));
        form.add(status);
        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);
        form.add(clearButton);

        add(form, BorderLayout.SOUTH);
    }

}
