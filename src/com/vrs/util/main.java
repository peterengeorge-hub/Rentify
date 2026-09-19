package com.vrs.util;
public class main {
    public static void main(String[] args) {
        try {
            var conn = com.vrs.util.DBConnection.getConnection();
            System.out.println("Connected successfully!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
