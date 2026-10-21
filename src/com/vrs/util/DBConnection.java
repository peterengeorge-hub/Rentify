package com.vrs.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_NAME = "vehiclerental.db";
    private static final String URL = "jdbc:sqlite:" + new File(DB_NAME).getAbsolutePath();

    public static Connection getConnection() throws SQLException {
        System.out.println("Using database: " + new File(DB_NAME).getAbsolutePath());
        return DriverManager.getConnection(URL);
    }
}