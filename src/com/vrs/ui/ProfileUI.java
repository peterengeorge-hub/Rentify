
package com.vrs.ui;

import javax.swing.*;
import java.awt.*;

public class ProfileUI extends JPanel {

    public ProfileUI() {
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel title = new JLabel("CUSTOMER PROFILE");
        JLabel nameLabel = new JLabel("Name:");
        JTextField name = new JTextField(15);

        JLabel emailLabel = new JLabel("Email:");
        JTextField email = new JTextField(15);

        JButton save = new JButton("Save");
        JButton back = new JButton("Back to Dashboard");

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(nameLabel);
        panel.add(name);
        panel.add(emailLabel);
        panel.add(email);
        panel.add(save);
        panel.add(back);

        add(panel);

        back.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.setContentPane(new DashboardUI());
                frame.revalidate();
                frame.repaint();
            }
        });

        save.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Profile saved temporarily.")
        );
    }
}
