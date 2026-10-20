package com.vrs.ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import com.vrs.model.Vehicle;
import com.vrs.dao.VehicleDAO;
public class VehiclePanel extends JPanel implements ActionListener{

    JTextField make = new JTextField();
    JTextField model = new JTextField();
    JTextField regNo = new JTextField();
    JTextField rate = new JTextField();
    JTextField search = new JTextField(10);
    JTextField insuranceExpiry = new JTextField();
    JTextField registrationExpiry = new JTextField();
    
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
    	addButton.addActionListener(this);
    	updateButton.addActionListener(this);
    	deleteButton.addActionListener(this);
    	clearButton.addActionListener(this);
    	
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
        
        form.add(new JLabel("Insurance Expiry"));
        form.add(insuranceExpiry);

        form.add(new JLabel("Registration Expiry"));
        form.add(registrationExpiry);
        
        form.add(new JLabel("Status"));
        form.add(status);
        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);
        form.add(clearButton);

        add(form, BorderLayout.SOUTH);
    }
public void actionPerformed(ActionEvent e) {
	if(e.getSource()==addButton) {
		Vehicle v = new Vehicle(0,
			    make.getText(),
			    model.getText(),
			    type.getSelectedItem().toString(),
			    regNo.getText(),
			    Double.parseDouble(rate.getText()),
			    status.getSelectedItem().toString(),
			    insuranceExpiry.getText(),
			    registrationExpiry.getText() );
		        VehicleDAO ob = new VehicleDAO();
		        try {
		        ob.addVehicle(v);}
		        catch(SQLException er) {
		        er.printStackTrace();
		        }
	}
	
}
}
