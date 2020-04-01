package bank;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Account {
    private int id;//Номер счета
    private int count;//текущий баланс
    private int countPlus;//приход
    private int countMinus;//расход
    private Boolean closeOpen;//закрыт/открыт счет
    private StringBuilder history = new StringBuilder();



    public Account() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCountPlus() {
        return countPlus;
    }

    public void setCountPlus(int countPlus) {
        this.countPlus = countPlus;
    }

    public int getCountMinus() {
        return countMinus;
    }

    public void setCountMinus(int countMinus) {
        this.countMinus = countMinus;
    }

    public Boolean getCloseOpen() {
        return closeOpen;
    }

    public void setCloseOpen(Boolean closeOpen) {
        this.closeOpen = closeOpen;
    }

    public String getHistory() {
        return history.toString();
    }

    public void setHistory(String history) {
        this.history.delete(0, this.history.length());
        this.history.append(history);
    }
}
