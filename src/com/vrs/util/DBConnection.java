package com.vrs.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:vehiclerental.db";

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL);

        String createCustomersTable =
                "CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "phone TEXT, "
                + "email TEXT, "
                + "license_number TEXT NOT NULL"
                + ")";

        String makeLicenseUnique =
                "CREATE UNIQUE INDEX IF NOT EXISTS "
                + "idx_customers_license_number "
                + "ON customers(license_number)";

        try (Statement statement = con.createStatement()) {
            statement.execute(createCustomersTable);
            statement.execute(makeLicenseUnique);
        }

        return con;
    }
}