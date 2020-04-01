package com;

import bank.Person;

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

    public boolean addClient(Person person, Bot bot) {//Schema: heroku_b0fe3d77cdb9844

        bot.setFlsNA(false,false,true);
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "INSERT heroku_b0fe3d77cdb9844.customers(name_customer, age_customer, work_place_customer) \n"
                    + "VALUES ('" + person.getName() +"', '" + person.getAge() +"', '" + person.getPlaceWork() +"');";
            statement.executeUpdate(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
                statement.close();
                return true;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return false;
            }
        }
    }

    public boolean editClient(Person person) {
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.customers SET name_customer = \'" + person.getName() + "\', age_customer = "
                    + person.getAge() + ", work_place_customer = \'" + person.getPlaceWork() + "\' WHERE id_customer = " + person.getId();
            statement.executeUpdate(query);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
            return false;
        } finally {
            try {
                connection.close();
                statement.close();
                return true;
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                return false;
            }
        }
    }

    public Person selectClient(int number) {
        Person tempPerson = null;
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_b0fe3d77cdb9844.customers WHERE id_customer = " + number;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempPerson = new Person();
                tempPerson.setId(resultSet.getInt(1));
                tempPerson.setName(resultSet.getString(2));
                tempPerson.setAge(resultSet.getInt(3));
                tempPerson.setPlaceWork(resultSet.getString(4));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            return tempPerson;
        }
    }

    public void deleteClient(int number) {
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "DELETE FROM heroku_b0fe3d77cdb9844.customers WHERE id_customer = " + number;
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String selectAllClient() {
        List<Person> personList = new ArrayList<>();
        Person tempPerson;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Список клиентов:\n");
        try {
            connection = DriverManager.getConnection(connectionString, login, password);
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_b0fe3d77cdb9844.customers";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempPerson = new Person();
                tempPerson.setId(resultSet.getInt(1));
                tempPerson.setName(resultSet.getString(2));
                tempPerson.setAge(resultSet.getInt(3));
                tempPerson.setPlaceWork(resultSet.getString(4));
                personList.add(tempPerson);
            }

            for (Person temp : personList) {
                stringBuilder.append(temp.getId());
                stringBuilder.append(". ");
                stringBuilder.append(temp.getName());
                stringBuilder.append("\n");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
                return stringBuilder.toString();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                return stringBuilder.toString();
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
