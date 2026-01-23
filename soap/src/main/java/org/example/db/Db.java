package org.example.db;

import java.sql.*;

public class Db {
    private static final String URL  = "jdbc:mysql://localhost:3306/flightdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}