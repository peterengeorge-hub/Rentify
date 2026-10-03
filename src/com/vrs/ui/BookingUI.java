package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class BookingUI extends JPanel {
    JLabel title, vehicle, date, days;
    JComboBox<String> vehicleBox;
    JTextField dateField, daysField;
    JButton book, clear, back;

    public BookingUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("BOOK VEHICLE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        vehicle = new JLabel("Vehicle:");
        date = new JLabel("Date:");
        days = new JLabel("Days:");

        vehicleBox = new JComboBox<>(new String[]{"Maruti Swift", "Hyundai i20", "Toyota Innova"});
        dateField = new JTextField();
        daysField = new JTextField();

        panel.add(vehicle);
        panel.add(vehicleBox);
        panel.add(date);
        panel.add(dateField);
        panel.add(days);
        panel.add(daysField);

        add(panel, BorderLayout.CENTER);

        book = new JButton("Book");
        clear = new JButton("Clear");
        back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(book);
        buttons.add(clear);
        buttons.add(back);

        add(buttons, BorderLayout.SOUTH);

        back.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new DashboardUI());
            frame.revalidate();
            frame.repaint();
        });
    }
}