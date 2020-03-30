package com;

import bank.Person;
import sun.print.PeekGraphics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseClient {


    private final String driverName = "com.mysql.jdbc.Driver";
    private final String connectionString = "jdbc:mysql://us-cdbr-iron-east-01.cleardb.net";
    private final String login = "baab4a4396de37";
    private final String password = "3f075f47";
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    public DatabaseClient() {
        connectToDB();
    }

    public void addClient(Person person) {
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "INSERT heroku_80379340d9d6f55.customers(name_customer, age_customer, work_place_customer) \n"
                    + "VALUES ('" + person.getName() +"', '" + person.getAge() +"', '" + person.getPlaceWork() +"');";
            statement.executeUpdate(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public List selectAllClient() {
        List<Person> personList = new ArrayList<>();
        Person tempPerson;
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_80379340d9d6f55.customers";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempPerson = new Person();
                tempPerson.setId(resultSet.getInt(1));
                tempPerson.setName(resultSet.getString(2));
                tempPerson.setAge(resultSet.getInt(3));
                tempPerson.setPlaceWork(resultSet.getString(4));
                personList.add(tempPerson);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
                return personList;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                return null;
            }
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

        connection = null;
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
