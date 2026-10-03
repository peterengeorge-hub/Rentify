package com.vrs.ui;
import javax.swing.*;
import java.awt.*;

public class LoginUI extends JPanel {
    JLabel title, username, password;
    JTextField userField;
    JPasswordField passField;
    JButton login, clear, register;

    public LoginUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("VEHICLE RENTAL SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        username = new JLabel("Username:");
        password = new JLabel("Password:");
        userField = new JTextField(15);
        passField = new JPasswordField(15);

        panel.add(username);
        panel.add(userField);
        panel.add(password);
        panel.add(passField);

        add(panel, BorderLayout.CENTER);

        login = new JButton("Login");
        clear = new JButton("Clear");
        register = new JButton("Register");

        JPanel buttons = new JPanel();
        buttons.add(login);
        buttons.add(clear);
        buttons.add(register);

        add(buttons, BorderLayout.SOUTH);
        login.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new DashboardUI());
            frame.revalidate();
            frame.repaint();
        });

        register.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new RegisterUI());
            frame.revalidate();
            frame.repaint();
        });
    }
}