
package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JPanel {

    public LoginUI() {
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel title = new JLabel("VEHICLE RENTAL SYSTEM");
        JLabel userLabel = new JLabel("Username:");
        JTextField username = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField password = new JPasswordField(15);

        JButton login = new JButton("Login");
        JButton register = new JButton("Register");

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(userLabel);
        panel.add(username);
        panel.add(passLabel);
        panel.add(password);
        panel.add(login);
        panel.add(register);

        add(panel);

        login.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.setContentPane(new DashboardUI());
                frame.revalidate();
                frame.repaint();
            }
        });
    }
}
