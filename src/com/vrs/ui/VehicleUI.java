package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class VehicleUI extends JPanel {
    JLabel title;
    JButton car1, car2, car3, back;

    public VehicleUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("AVAILABLE VEHICLES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        car1 = new JButton("Maruti Swift - ₹1500/day");
        car2 = new JButton("Hyundai i20 - ₹1800/day");
        car3 = new JButton("Toyota Innova - ₹2500/day");

        panel.add(car1);
        panel.add(car2);
        panel.add(car3);

        add(panel, BorderLayout.CENTER);

        back = new JButton("Back");
        add(back, BorderLayout.SOUTH);

        back.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new DashboardUI());
            frame.revalidate();
            frame.repaint();
        });
    }
}