package customer_rental;
import javax.swing.*;
import java.awt.*;
public class CustomerRentalManagement extends JFrame {

    JLabel title;
    JLabel customerId, customerName, phone;
    JLabel vehicle, rentalDate, returnDate;

    JTextField customerIdField;
    JTextField customerNameField;
    JTextField phoneField;
    JTextField rentalDateField;
    JTextField returnDateField;

    JComboBox<String> vehicleBox;

    JButton addCustomer;
    JButton bookVehicle;
    JButton returnVehicle;
    JButton viewRecords;

    public CustomerRentalManagement() {

        setTitle("Customer & Rental Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        title = new JLabel("CUSTOMER & RENTAL MANAGEMENT");
        title.setHorizontalAlignment(JLabel.CENTER);
        add(title, BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6,2,10,10));
        customerId = new JLabel("Customer ID:");
        customerIdField = new JTextField();
        panel.add(customerId);
        panel.add(customerIdField);
        customerName = new JLabel("Customer Name:");
        customerNameField = new JTextField();
        panel.add(customerName);
        panel.add(customerNameField);
        phone = new JLabel("Phone:");
        phoneField = new JTextField();
        panel.add(phone);
        panel.add(phoneField);
        vehicle = new JLabel("Vehicle:");
        String vehicles[] = {"Select Vehicle", "Swift", "Nexon", "i20", "Glanza"};
        vehicleBox = new JComboBox<>(vehicles);
        panel.add(vehicle);
        panel.add(vehicleBox);
        rentalDate = new JLabel("Rental Date:");
        rentalDateField = new JTextField();
        panel.add(rentalDate);
        panel.add(rentalDateField);
        returnDate = new JLabel("Return Date:");
        returnDateField = new JTextField();
        panel.add(returnDate);
        panel.add(returnDateField);
        addCustomer = new JButton("Add Customer");
        bookVehicle = new JButton("Book Vehicle");
        returnVehicle = new JButton("Return Vehicle");
        viewRecords = new JButton("View Records");
        JPanel buttonPanel=new JPanel(new FlowLayout());

        buttonPanel.add(addCustomer);
        buttonPanel.add(bookVehicle);
        buttonPanel.add(returnVehicle);
        buttonPanel.add(viewRecords);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
       
    }
}