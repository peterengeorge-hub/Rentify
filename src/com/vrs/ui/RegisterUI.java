package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class RegisterUI extends JPanel {
    JLabel title, name, email, username, password;
    JTextField nameField, emailField, userField;
    JPasswordField passField;
    JButton register, clear, back;

    public RegisterUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        title = new JLabel("REGISTER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        name = new JLabel("Name:");
        email = new JLabel("Email:");
        username = new JLabel("Username:");
        password = new JLabel("Password:");

        nameField = new JTextField(15);
        emailField = new JTextField(15);
        userField = new JTextField(15);
        passField = new JPasswordField(15);

        panel.add(name);
        panel.add(nameField);
        panel.add(email);
        panel.add(emailField);
        panel.add(username);
        panel.add(userField);
        panel.add(password);
        panel.add(passField);

        add(panel, BorderLayout.CENTER);

        register = new JButton("Register");
        clear = new JButton("Clear");
        back = new JButton("Back");

        JPanel buttons = new JPanel();
        buttons.add(register);
        buttons.add(clear);
        buttons.add(back);

        add(buttons, BorderLayout.SOUTH);
        back.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setContentPane(new LoginUI());
            frame.revalidate();
            frame.repaint();
        });
    }
}