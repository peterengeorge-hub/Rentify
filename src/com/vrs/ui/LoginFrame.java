package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");

    public LoginFrame() {
        setTitle("Vehicle Rental System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new GridBagLayout());

        JPanel container = new JPanel(new BorderLayout(0, 20));

        JLabel title = new JLabel(
                "Vehicle Rental System",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 28));
        container.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        form.add(new JLabel("Username:"));
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        form.add(passwordField);

        form.add(new JLabel(""));
        form.add(loginButton);

        container.add(form, BorderLayout.CENTER);
        add(container);

        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("Rentify@123")) {
            try {
                
                MainFrame mainFrame = new MainFrame();
                mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainFrame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        "Could not open the main page:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Incorrect username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}