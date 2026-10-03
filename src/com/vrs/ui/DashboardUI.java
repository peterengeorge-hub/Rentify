package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardUI extends JPanel {
    JLabel title;
    JButton vehicles, bookings, profile, logout;

    public DashboardUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("VEHICLE RENTAL SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        vehicles = new JButton("Vehicles");
        bookings = new JButton("Bookings");
        profile = new JButton("Profile");
        logout = new JButton("Logout");

        panel.add(vehicles);
        panel.add(bookings);
        panel.add(profile);
        panel.add(logout);

        add(panel, BorderLayout.CENTER);

        vehicles.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new VehicleUI());
            frame.revalidate();
            frame.repaint();
        });

        bookings.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new BookingUI());
            frame.revalidate();
            frame.repaint();
        });

        profile.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new ProfileUI());
            frame.revalidate();
            frame.repaint();
        });

        logout.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new LoginUI());
            frame.revalidate();
            frame.repaint();
        });
    }
}