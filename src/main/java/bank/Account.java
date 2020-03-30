package bank;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Account {
    private int numberCount;//Номер счета
    private int count;//текущий баланс
    private int countPlus;//приход
    private int countMinus;//расход
    private Boolean closeOpen;//закрыт/открыт счет
    private StringBuilder history = new StringBuilder();

    public int go() {
        while (true) {
            while (closeOpen) {
                int choice;
                choice = menu();
                switch (choice) {
                    case 1:
                        changeCloseOpen();
                        break;
                    case 2:
                        sumCount();
                        break;
                    case 3:
                        deductionCount();
                        break;
                    case 4:
                        checkBalance();
                        break;
                    case 5:
                        checkHistory();
                        break;
                    case 0:
                        return 0;
                    case -1:
                        emptyFun();
                        break;
                }
            }
            while (!closeOpen) {
                int choice;
                choice = closeMenu();
                switch (choice) {
                    case 1:
                        changeCloseOpen();
                        break;
                    case 0:
                        return 0;
                    case -1:
                        emptyFun();
                        break;
                }
            }
        }
    }

    public void changeCloseOpen() {
        if (closeOpen) {
            setCloseOpen(false);
            history.append("Счет закрыт\n");
            cls();
            System.out.println("Счет закрыт");
        } else {
            setCloseOpen(true);
            history.append("Счет открыт\n");
            cls();
            System.out.println("Счет открыт");
        }
        continueProgram();
        return;
    }

    public void sumCount() {
        cls();
        System.out.println("Введите нужную сумму");
        Scanner scanner = new Scanner(System.in);
        int sum;
        try {
            sum = scanner.nextInt();
            count += sum;
            countPlus += sum;
            history.append("Вклад " + sum + "\n");
            System.out.println("Транзакция успешно выполнена");
            continueProgram();
        } catch (InputMismatchException e) {
            System.out.println("Некорректно введена сумма, пожалуйста, повторите попытку");
            continueProgram();
            return;
        }
    }

    public void deductionCount() {
        cls();
        System.out.println("Введите нужную сумму");
        Scanner scanner = new Scanner(System.in);
        int sum;
        try {
            sum = scanner.nextInt();
            count -= sum;
            countMinus += sum;
            history.append("Снятие " + sum + "\n");
            System.out.println("Транзакция успешно выполнена");
            continueProgram();
        } catch (InputMismatchException e) {
            System.out.println("Некорректно введена сумма, пожалуйста, повторите попытку");
            continueProgram();
        }
    }

    public void checkBalance() {
        cls();
        System.out.println("Баланс на счету " + numberCount + ", составляет - " + count);
        history.append("Просмотр баланса\n");
        continueProgram();
    }

    public void checkHistory() {
        cls();
        System.out.println("История " + numberCount + " счета:");
        System.out.println(history.toString());
        continueProgram();
    }

    private int menu() {
        cls();
        System.out.println("Меню:");
        System.out.println("1. Закрыть счёт");
        System.out.println("2. Вклад денег");
        System.out.println("3. Снятие денег");
        System.out.println("4. Просмотр баланса");
        System.out.println("5. Просмотр истории");
        System.out.println("0. Вернуться назад");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Вы ввели неверный символ, пожалуйста, повторите попытку");
            continueProgram();
        } finally {
            return choice;
        }
    }

    private int closeMenu() {
        cls();
        System.out.println("Меню:");
        System.out.println("1. Открыть счёт");
        System.out.println("0. Вернуться назад");
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Вы ввели неверный символ, пожалуйста, повторите попытку");
            continueProgram();
        } finally {
            return choice;
        }
    }

    public void continueProgram() {
        System.out.println("Для продолжение введите любой символ");
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

    public void cls() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    public void emptyFun() {
    }

    public Account(int numberCount, int count) {
        this.numberCount = numberCount;
        this.count = count;
        this.countPlus = 0;
        this.countMinus = 0;
        this.closeOpen = true;
    }

    public int getNumberCount() {
        return numberCount;
    }

    public void setNumberCount(int numberCount) {
        this.numberCount = numberCount;
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
}
