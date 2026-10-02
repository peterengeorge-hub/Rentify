package com.vrs.billing;

import javax.swing.*;

public class BillingUI {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Billing & Profit");

        frame.setSize(700, 600);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Billing & Profit");
        title.setBounds(270, 30, 200, 30);
        frame.add(title);

        JLabel customer = new JLabel("Customer Name:");
        customer.setBounds(80, 100, 120, 30);
        frame.add(customer);

        JTextField customerText = new JTextField();
        customerText.setBounds(220, 100, 300, 30);
        frame.add(customerText);

        JLabel vehicle = new JLabel("Vehicle ID:");
        vehicle.setBounds(80, 150, 120, 30);
        frame.add(vehicle);

        JTextField vehicleText = new JTextField();
        vehicleText.setBounds(220, 150, 300, 30);
        frame.add(vehicleText);

        JLabel days = new JLabel("Rental Days:");
        days.setBounds(80, 200, 120, 30);
        frame.add(days);

        JTextField daysText = new JTextField();
        daysText.setBounds(220, 200, 300, 30);
        frame.add(daysText);

        JLabel amount = new JLabel("Total Amount:");
        amount.setBounds(80, 250, 120, 30);
        frame.add(amount);

        JTextField amountText = new JTextField();
        amountText.setBounds(220, 250, 300, 30);
        frame.add(amountText);

        JLabel profit = new JLabel("Profit:");
        profit.setBounds(80, 300, 120, 30);
        frame.add(profit);

        JTextField profitText = new JTextField();
        profitText.setBounds(220, 300, 300, 30);
        frame.add(profitText);

        JButton generate = new JButton("Generate Bill");
        generate.setBounds(220, 370, 140, 40);
        frame.add(generate);

        JButton clear = new JButton("Clear");
        clear.setBounds(380, 370, 140, 40);
        frame.add(clear);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}