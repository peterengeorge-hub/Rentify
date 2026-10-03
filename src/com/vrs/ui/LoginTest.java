package com.vrs.ui;

import javax.swing.*;

public class LoginTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Vehicle Rental System");
        frame.add(new LoginUI());
        frame.setSize(500, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}