package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseClient {


    private final String driverName = "com.mysql.jdbc.Driver";
    private final String connectionString = "jdbc:mysql://us-cdbr-iron-east-01.cleardb.net";
    private final String login = "baab4a4396de37";
    private final String password = "3f075f47";


    public DatabaseClient() {
        connectToDB();
    }

    public void addClient() {
    }

    private void connectToDB() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
        } catch (SQLException throwables) {
            System.out.println("Can't get connection. Incorrect URL");
            throwables.printStackTrace();
            return;
        }

        try {
            connection.close();
        } catch (SQLException throwables) {
            System.out.println("Can't close connection");
            throwables.printStackTrace();
            return;
        }
    }

}
