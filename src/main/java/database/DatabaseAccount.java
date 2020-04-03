package database;

import bank.Account;
import bank.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccount extends Database {

    private Statement statement;
    private ResultSet resultSet;

    private boolean temp;

    public DatabaseAccount() {
        super();
    }

    public boolean addAccount(int chooseNumber) {
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "INSERT heroku_b0fe3d77cdb9844.accounts(id_customer, count_account, history) VALUES (" + chooseNumber + ", 350, \"open count\n\")";
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
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "SELECT * FROM heroku_b0fe3d77cdb9844.accounts WHERE id_customer = " + chooseNumber;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                tempAccount = new Account();
                tempAccount.setId(resultSet.getInt(1));
                tempAccount.setCount(resultSet.getInt(3));
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
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
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

    public boolean editCountPlus(int chooseAccount, int choosePerson, Account account, int countPlus) {
        account.setCount(account.getCount() + countPlus);
        account.setCountPlus(account.getCountPlus() + countPlus);
        account.setHistory(account.getHistory() + "Refill (" + countPlus + ")\n");
        temp = false;
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.accounts SET count_account = " + account.getCount()
                    + ", count_plus = " + account.getCountPlus()
                    + ", history = '" + account.getHistory() + "'"
                    + " WHERE id_account = " + chooseAccount + " AND id_customer = " + choosePerson;
            statement.executeUpdate(query);
            temp = true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                temp = false;
            } finally {
                return temp;
            }
        }
    }

    public boolean editCountMinus(int chooseAccount, int choosePerson, Account account, int countMinus) {//Withdrawal - снятие со счета
        account.setCount(account.getCount() - countMinus);
        account.setCountMinus(account.getCountMinus() + countMinus);
        account.setHistory(account.getHistory() + "Withdrawal (" + countMinus + ")\n");
        temp = false;
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.accounts SET count_account = " + account.getCount()
                    + ", count_minus = " + account.getCountMinus()
                    + ", history = '" + account.getHistory() + "'"
                    + " WHERE id_account = " + chooseAccount + " AND id_customer = " + choosePerson;
            statement.executeUpdate(query);
            temp = true;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
                temp = false;
            } finally {
                return temp;
            }
        }
    }

    public String selectCount(int chooseAccount, int chooseClient, Account account) {
        account.setHistory(account.getHistory() + "Check balance\n");
        String count = null;
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.accounts SET history = '" + account.getHistory() + "'"
                    + " WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
            statement.executeUpdate(query);
            query = "SELECT * FROM heroku_b0fe3d77cdb9844.accounts WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                count = String.valueOf(resultSet.getInt(3));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
                return count;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                return count;
            }
        }
    }

    public boolean isCheckAccountBills(int chooseNumber) { // проверка на наличие счетов у аккуанта
        List<Account> accountList = new ArrayList<>();
        Account tempAccount;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Список счетов клиента " + chooseNumber + ":\n");
        int fl = stringBuilder.length();//для проверки пустой строки stringBuilder (если в цикле ничего не запишется)
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
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
                    return false;
                else
                    return true;
            }
        }
    }

    public void deleteAccount(int chooseAccount, int chooseClient) {
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "DELETE FROM heroku_b0fe3d77cdb9844.accounts WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
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

    public String selectHistory(int chooseAccount, int chooseClient, Account account) {
        account.setHistory(account.getHistory() + "Check history\n");
        System.err.println(account.getHistory());
        String history = null;
        try {
            connection = DriverManager.getConnection(getConnectionString(), getLogin(), getPassword());
            statement = connection.createStatement();
            String query = "UPDATE heroku_b0fe3d77cdb9844.accounts SET history = '" + account.getHistory() + "'"
                    + " WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
            ResultSet resultSet1 = statement.executeQuery(query);
            query = "SELECT * FROM heroku_b0fe3d77cdb9844.accounts WHERE id_account = " + chooseAccount + " AND id_customer = " + chooseClient;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                history = resultSet.getString(6);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
                return history;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                return history;
            }
        }
    }
}
