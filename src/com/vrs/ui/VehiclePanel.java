package com.vrs.ui;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.vrs.util.DBConnection;
import java.sql.ResultSet;
import com.vrs.model.Vehicle;
import com.vrs.dao.VehicleDAO;
public class VehiclePanel extends JPanel implements ActionListener{

    JTextField make = new JTextField();
    JTextField model = new JTextField();
    JTextField regNo = new JTextField();
    JTextField rate = new JTextField();
    JTextField insuranceExpiry = new JTextField();
    JTextField registrationExpiry = new JTextField();
    
    JComboBox<String> type = new JComboBox<>(
            new String[]{"Sedan", "SUV", "Hatchback", "Van", "Bike"});

    JComboBox<String> status = new JComboBox<>(
            new String[]{"Available", "Rented", "Maintenance"});

    JButton addButton = new JButton("Add");
    JButton updateButton = new JButton("Update");
    JButton deleteButton = new JButton("Delete");
    JButton clearButton = new JButton("Clear");
    JTable table;
    DefaultTableModel tableModel;

    public VehiclePanel() {
       setLayout(new BorderLayout(10, 10));
       addButton.addActionListener(this);
     	updateButton.addActionListener(this);
   	   deleteButton.addActionListener(this);
   	   clearButton.addActionListener(this);
        JPanel top = new JPanel();

        top.add(new JLabel("Vehicle Management"));
        add(top, BorderLayout.NORTH);

        String[] columns = {
                "ID", "Make", "Model", "Type",
                "Reg Number", "Daily Rate", "Status", "insuranceExpiry", "registrationExpiry"
        };

        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();

                if (row != -1) {
                    make.setText(tableModel.getValueAt(row, 1).toString());
                    model.setText(tableModel.getValueAt(row, 2).toString());
                    type.setSelectedItem(tableModel.getValueAt(row, 3));
                    regNo.setText(tableModel.getValueAt(row, 4).toString());
                    rate.setText(tableModel.getValueAt(row, 5).toString());
                    status.setSelectedItem(tableModel.getValueAt(row, 6));
                    insuranceExpiry.setText(tableModel.getValueAt(row, 7).toString());
                    registrationExpiry.setText(tableModel.getValueAt(row, 8).toString());
                }
            }
        });
        String sql = "SELECT * FROM vehicles";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("vehicle_id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getString("reg_number"),
                    rs.getDouble("daily_rate"),
                    rs.getString("status"),
                    rs.getString("insurance_expiry"),
                    rs.getString("registration_expiry")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Failed to load vehicles: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        JPanel form = new JPanel(new GridLayout(5, 4, 10, 10));

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
		  if (rate.getText().trim().isEmpty()) {
		        JOptionPane.showMessageDialog(this, "Please enter daily rate");
		        return;
		    }

		    try {
		        Double.parseDouble(rate.getText());
		    } catch (NumberFormatException ex) {
		        JOptionPane.showMessageDialog(this, "Please enter a valid daily rate");
		        return;
		    }
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
		        int id = ob.addVehicle(v);
		        v.setVehicleId(id);
		        tableModel.addRow(new Object[]{
		        	    v.getVehicleId(),
		        	    v.getMake(),
		        	    v.getModel(),
		        	    v.getType(),
		        	    v.getRegNumber(),
		        	    v.getDailyRate(),
		        	    v.getStatus(),
		        	    v.getInsuranceExpiry(),
		        	    v.getRegistrationExpiry()
		        	});
		        }
		        catch(SQLException er) {
		        er.printStackTrace();
		        }
		        
	}
	else if(e.getSource()==updateButton) {
		int row = table.getSelectedRow();
		if(row!=-1) {
			if (rate.getText().trim().isEmpty()) {
			    JOptionPane.showMessageDialog(this, "Please enter daily rate");
			    return;
			}

			try {
			    Double.parseDouble(rate.getText());
			} catch (NumberFormatException ex) {
			    JOptionPane.showMessageDialog(this, "Please enter a valid daily rate");
			    return;
			}
		int id = (int) tableModel.getValueAt(row, 0);
		Vehicle v = new Vehicle(id,
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
		        ob.updateVehicle(v);
		        tableModel.setValueAt(v.getMake(), row, 1);
		        tableModel.setValueAt(v.getModel(), row, 2);
		        tableModel.setValueAt(v.getType(), row, 3);
		        tableModel.setValueAt(v.getRegNumber(), row, 4);
		        tableModel.setValueAt(v.getDailyRate(), row, 5);
		        tableModel.setValueAt(v.getStatus(), row, 6);
		        tableModel.setValueAt(v.getInsuranceExpiry(), row, 7);
		        tableModel.setValueAt(v.getRegistrationExpiry(), row, 8);
		                     }
		        catch(SQLException er) {
		        er.printStackTrace();
		        }}
		else {JOptionPane.showMessageDialog(
			    this, "Please select a vehicle first");}
	}
	else if(e.getSource()==deleteButton) {
		        int row = table.getSelectedRow();
		        if(row!=-1) {
		        	int id = (int) tableModel.getValueAt(row, 0);
		        VehicleDAO ob = new VehicleDAO();
		        try {
		        ob.deleteVehicle(id);
		        tableModel.removeRow(row);
		                     }
		        catch(SQLException er) {
		        er.printStackTrace();
		        }}
		        else {
		        	JOptionPane.showMessageDialog(
		        		    this, "Please select a vehicle first");
		        }
		        
	}
	else if(e.getSource()==clearButton) {
		 make.setText("");
		    model.setText("");
		    regNo.setText("");
		    rate.setText("");
		    insuranceExpiry.setText("");
		    registrationExpiry.setText("");

		    type.setSelectedIndex(0);
		    status.setSelectedIndex(0);

		    table.clearSelection();
	}
}
}
