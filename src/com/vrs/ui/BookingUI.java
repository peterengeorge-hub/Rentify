
package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class BookingUI extends JPanel {

    public BookingUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("VEHICLE BOOKINGS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Vehicle", "Customer", "Status"};
        String[][] data = {
            {"B101", "Swift", "Customer 1", "Pending"},
            {"B102", "Activa", "Customer 2", "Confirmed"}
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
