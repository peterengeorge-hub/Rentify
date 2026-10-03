package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class ProfileUI extends JPanel {
    JLabel title, name, email, phone;
    JTextField nameField, emailField, phoneField;
    JButton save, back;

    public ProfileUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("MY PROFILE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        name = new JLabel("Name:");
        email = new JLabel("Email:");
        phone = new JLabel("Phone:");

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        panel.add(name);
        panel.add(nameField);
        panel.add(email);
        panel.add(emailField);
        panel.add(phone);
        panel.add(phoneField);

        add(panel, BorderLayout.CENTER);

        save = new JButton("Save");
        back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(save);
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