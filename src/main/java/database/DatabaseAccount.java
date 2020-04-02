package database;

import bank.Account;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccount extends Database {

    private Statement statement;
    private ResultSet resultSet;

    public DatabaseAccount() {
        super();
    }

    public boolean addAccount(int chooseNumber) {
        try {
            connection = DriverManager.getConnection(getDriverName(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "INSERT heroku_b0fe3d77cdb9844.accounts(id_customer, count, history) VALUES (" + chooseNumber + ", 350, \"\")";
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

    public String selectAllAccount(int chooseNumber) {
        List<Account> accountList = new ArrayList<>();
        Account tempAccount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Список счетов клиента " + chooseNumber + ":\n");
        int fl = stringBuilder.length();//для проверки пустой строки stringBuilder (если в цикле ничего не запишется)
        try {
            connection = DriverManager.getConnection(getDriverName(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_b0fe3d77cdb9844.accounts WHERE id_customer = " + chooseNumber;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempAccount = new Account();
                tempAccount.setId(resultSet.getInt(1));
                tempAccount.setCount(resultSet.getInt(3));
                tempAccount.setCountPlus(resultSet.getInt(4));
                tempAccount.setCountMinus(resultSet.getInt(5));
                tempAccount.setHistory(resultSet.getString(6));
                accountList.add(tempAccount);
            }

            for (Account temp : accountList) {
                stringBuilder.append(temp.getId());
                stringBuilder.append(". Баланс ");
                stringBuilder.append(temp.getCount());
                stringBuilder.append("\n");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                if (stringBuilder.length() == fl)
                    stringBuilder.append("Пусто...");
                return stringBuilder.toString();
            }
        }
    }

    public Account selectAccount(int chooseAccount, int chooseClient) {
        Account tempAccount = null;
        try {
            connection = DriverManager.getConnection(getDriverName(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_b0fe3d77cdb9844.accounts WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempAccount = new Account();
                tempAccount.setId(resultSet.getInt(1));
                tempAccount.setCount(resultSet.getInt(3));
                tempAccount.setCountPlus(resultSet.getInt(4));
                tempAccount.setCountMinus(resultSet.getInt(5));
                tempAccount.setHistory(resultSet.getString(6));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
                return tempAccount;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                return tempAccount;
            }
        }
    }

    public boolean editCountPlus(int chooseAccount, int chooseClient, Account account, int countPlus) {
        try {
            connection = DriverManager.getConnection(getDriverName(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.accounts SET count = \'" + (account.getCount() + countPlus) + "\', count_plus = "
                    + (account.getCountPlus() + countPlus) + " WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
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
}
