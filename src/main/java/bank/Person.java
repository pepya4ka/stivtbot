package bank;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Person {
    private int numberPerson;//номер конкретного клиента
    private String name;//ФИО
    private int age;//возраст
    private String placeWork;//место работы
    private int i = 0;//кол-во счетов
    private List<Account> accountList = new ArrayList<>();

    public int go() {
        while (true) {
            int choice;
            choice = menu();
            switch (choice) {
                case 1:
                    changePersonInfo();
                    break;
                case 2:
                    return deletePerson();
                case 3:
                    printAllAccount();
                    break;
                case 4:
                    chooseCount();
                    break;
                case 5:
                    addAccount();
                    break;
                case 0:
                    return 0;
                case -1:
                    emptyFun();
                    break;
            }
        }
    }

    private int menu() {
        cls();
        System.out.println("Меню:");
        System.out.println("1. Редактировать клиента");
        System.out.println("2. Удалить клиента");
        System.out.println("3. Информация о счетах");
        System.out.println("4. Выбрать счет");
        System.out.println("5. Открыть новый счет");
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

    public void changePersonInfo() {
        Scanner scanner = new Scanner(System.in);
        String tempString;
        cls();
        System.out.println("Чтобы оставить тем же введите \"-\"");
        System.out.println("Введите ФИО");
        tempString = scanner.nextLine();
        if (!tempString.equals("-"))
            name = tempString;
        System.out.println("Введите возраст");
        tempString = scanner.nextLine();
        if (!tempString.equals("-"))
            age = Integer.parseInt(tempString);
        System.out.println("Введите место работы");
        tempString = scanner.nextLine();
        if (!tempString.equals("-"))
            placeWork = tempString;
        System.out.println("Данные изменены");
        continueProgram();
    }

    public int deletePerson() {
        cls();
        System.out.println("Вы уверены что хотите удалить клиентв?(д/н)");
        Scanner scanner = new Scanner(System.in);
        String yes = scanner.next();
        if (yes.equals("д") || yes.equals("y")) {
            return 1;
        } else {
            return 0;
        }
    }

    public void addAccount() {
        cls();
        accountList.add(new Account(((numberPerson + 1) * 10 + i), 100));
        i++;
        System.out.println("Новый счет успешно открыт");
        continueProgram();
    }


    public void chooseCount() {
        cls();
        System.out.println("Введите номер выбранного счета");
        Scanner scanner = new Scanner(System.in);
        int numberAccount;
        try {
            numberAccount = scanner.nextInt();
            numberAccount -= (numberPerson + 1) * 10;
        } catch (InputMismatchException e) {
            System.out.println("Вы ввели неверный символ, пожалуйста, повторите попытку");
            continueProgram();
            return;
        }
        if (accountList.get(numberAccount).go() == 1)
            if (getAccount(numberAccount) == null) {
                System.out.println("Счет не найдет, пожалуйста, повторите попытку");
                continueProgram();
                return;
            } else
                accountList.remove(getAccount(numberAccount));
    }

    public void printAllAccount() {
        cls();
        System.out.println("Список счетов клиента");
        for (Account tempAccount : accountList) {
            System.out.println("Номер счета " + tempAccount.getNumberCount());
            System.out.println("Баланс " + tempAccount.getCount());
            System.out.println("Приход/Расход " + tempAccount.getCountPlus() + "/" + tempAccount.getCountMinus());
            System.out.println();
        }
        continueProgram();
    }

    public Account getAccount(int numberAccount) {
        for (Account tempAccount : accountList) {
            if (tempAccount.getNumberCount() == numberAccount)
                return tempAccount;
        }
        return null;
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

    public Person(String name, int age, String placeWork, int numberPerson) {
        this.numberPerson = numberPerson;
        this.name = name;
        this.age = age;
        this.placeWork = placeWork;
    }

    public Person(int numberPerson) {
        this.numberPerson = numberPerson;
        name = "Example person";
        age = 0;
        placeWork = "Example person";
    }

    public int getNumberPerson() {
        return numberPerson;
    }

    public void setNumberPerson(int numberPerson) {
        this.numberPerson = numberPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPlaceWork() {
        return placeWork;
    }

    public void setPlaceWork(String placeWork) {
        this.placeWork = placeWork;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }
}
