
package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class VehicleUI extends JPanel {

    public VehicleUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("AVAILABLE VEHICLES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Vehicle ID", "Vehicle Name", "Type", "Rent/Day"};
        String[][] data = {
            {"V101", "Swift", "Car", "1500"},
            {"V102", "Activa", "Bike", "500"},
            {"V103", "Innova", "Car", "2500"}
        };

        JTable table = new JTable(data, columns);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton back = new JButton("Back to Dashboard");
        add(back, BorderLayout.SOUTH);

        back.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.setContentPane(new DashboardUI());
                frame.revalidate();
                frame.repaint();
            }
        });
    }
}
