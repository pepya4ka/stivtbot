package bank;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Bank {
    static int numberCounts = 0;//Счетчик номеров
    static int numberPersons = 0;//Счетчик клиентов
    static List<Person> persons;

    public void go() {
        persons = new ArrayList<>();
        while (true) {
            int choice;
            choice = menu();
            switch (choice) {
                case 1:
                    addPersons();
                    break;
                case 2:
                    printAllPersons();
                    break;
                case 3:
                    choosePerson();
                    break;
                case 0:
                    return;
                case -1:
                    emptyFun();
                    break;
            }
        }
    }

    public void cls() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    private int menu() {
        cls();
        System.out.println("Меню:");
        System.out.println("1. Добавить клиента");
        System.out.println("2. Показать всех клиентов");
        System.out.println("3. Выбрать клиента");
        System.out.println("0. Выход");
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

    public void addPersons() {
        Scanner scanner = new Scanner(System.in);
        cls();
        System.out.println("Введите ФИО");
        String name = scanner.nextLine();
        System.out.println("Введите возраст");
        int age = scanner.nextInt();
        System.out.println("Введите место работы");
        scanner.nextLine();
        String placeWork = scanner.nextLine();
        persons.add(new Person(name, age, placeWork, numberPersons));
        numberPersons++;
        continueProgram();
    }

    public void printAllPersons() {
        cls();
        System.out.println("Список клиентов");
        for (Person tempPerson : persons) {
            System.out.println(tempPerson.getNumberPerson() + ". " + tempPerson.getName() + " " + tempPerson.getAge() + " " + tempPerson.getPlaceWork() + " (количество счетов - " + tempPerson.getI());
        }
        continueProgram();
    }

    public void choosePerson() {
        cls();
        System.out.println("Введите номер выбранного клиента");
        Scanner scanner = new Scanner(System.in);
        int numberPerson;
        try {
            numberPerson = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Вы ввели неверный символ, пожалуйста, повторите попытку");
            continueProgram();
            return;
        }
        if (getPerson(numberPerson) == null) {
            System.out.println("Клиент не найдет, пожалуйста, повторите попытку");
            continueProgram();
            return;
        } else {
            if (persons.get(numberPerson).go() == 1)
                persons.remove(getPerson(numberPerson));
        }
    }

    public Person getPerson(int numberPerson) {
        for (Person tempPerson : persons) {
            if (tempPerson.getNumberPerson() == numberPerson)
                return tempPerson;
        }
        return null;
    }

    public void continueProgram() {
        System.out.println("Для продолжение введите любой символ");
        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

    public void emptyFun() {
    }


}
