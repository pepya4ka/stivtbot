package com;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {


    private final String driverName = "com.mysql.jdbc.Driver";
    private final String connectionString = "jdbc:mysql://us-cdbr-iron-east-01.cleardb.net";
    private final String login = "baab4a4396de37";
    private final String password = "3f075f47";


    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    public void run() {
        connectToDB();
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
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
