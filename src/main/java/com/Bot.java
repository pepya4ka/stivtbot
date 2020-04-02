package com;

import bank.Account;
import bank.Person;
import database.DatabaseAccount;
import database.DatabaseClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bot extends TelegramLongPollingBot {

    DatabaseClient databaseClient;
    DatabaseAccount databaseAccount;
    Person person;
    Account account;
    int choosePerson;
    int chooseAccount;

    private boolean flMainMenu = false;
    private boolean flClientMenu = false;
    private boolean flAccountMenu = false;

    private boolean flAdd = false;
    private boolean flChooseClient = false;
    private boolean flEdit = false;
    private boolean flChooseAccount = false;
    private boolean flDel = false;

    private boolean flName = false;
    private boolean flAge = false;
    protected boolean flWorkPlace = false;

    private boolean flPlus = false;
    private boolean flMinus = false;

    private List<KeyboardRow> keyboardRowList;

    public Bot() {
        databaseClient = new DatabaseClient();
        databaseAccount = new DatabaseAccount();
    }

    protected void setFlsACEC(boolean flAdd, boolean flChoose, boolean flEdit, boolean flChooseAccount, boolean flDel) {
        this.flAdd = flAdd;
        this.flChooseClient = flChoose;
        this.flEdit = flEdit;
        this.flChooseAccount = flChooseAccount;
        this.flDel = flDel;
    }

    private void setFlsMenu(boolean flMainMenu, boolean flClientMenu, boolean flAccountMenu) {
        this.flMainMenu = flMainMenu;
        this.flClientMenu = flClientMenu;
        this.flAccountMenu = flAccountMenu;
    }

    protected void setFlsNA(boolean flName, boolean flAge, boolean flWorkPlace) {
        this.flName = flName;
        this.flAge = flAge;
        this.flWorkPlace = flWorkPlace;
    }

    private void setPM(boolean flPlus, boolean flMinus) {
        this.flPlus = flPlus;
        this.flMinus = flMinus;
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            if (flMainMenu)
                setButtonsMainMenu(sendMessage);
            if (flClientMenu)
                setButtonsClientMenu(sendMessage);
            if (flAccountMenu)
                setButtonsAccountMenu(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    setFlsMenu(true, false, false);
                    setFlsACEC(false, false, false, false, false);
                    setFlsNA(false, false, false);
                    setPM(false, false);
                    sendMsg(message, "Балыбердин, Билалов, Демидов, Дроздов ИВТ-414, Сетевые технологии");
                    break;
                case "Добавить клиента":
                    setFlsACEC(true, false, false, false, false);
                    setFlsNA(false, false, false);
                    person = new Person();
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество транслитом)");
                    break;
                case "Показать всех клиентов":
                    setFlsACEC(false, false, false, false, false);
                    setFlsNA(false, false, false);
                    showAllClient(message);
                    break;
                case "Выбрать клиента":
                    setFlsACEC(false, true, false, false, false);
                    setFlsNA(false, false, false);
                    sendMsg(message, "Пожалуйста, пришлите номер выбранного клиента");
                    break;
                case "Вернуться назад":
                    previousMenu(message);
                    break;
                case "Удалить клиента":
                    setFlsACEC(false, false, false, false, true);
                    deleteClient(message);
                    break;
                case "Редактировать клиента":
                    setFlsACEC(false, false, true, false, false);
                    setFlsNA(false, false, false);
                    person = null;
                    person = databaseClient.selectClient(choosePerson);
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество транслитом или \"-\", если нужно оставить без изменений)");
                    break;
                case "Открыть счет":
                    addAccount(message, choosePerson);
                    break;
                case "Информация о счетах":
                    sendMsg(message, databaseAccount.selectAllAccount(choosePerson));
                    break;
                case "Выбрать счет":
                    setFlsACEC(false, false, false, true, false);
                    setPM(false, false);
                    sendMsg(message, "Пожалуйста, пришлите номер выбранного счета");
                    break;
                case "Вклад денег":
                    setPM(true, false);
                    sendMsg(message, "Пожалуйста, введите сумму, которую хотите положить на счет");
                    break;
                case "Ok":
                    if (flDel) {
                        previousMenu(message);
                    }
                    emptyMethod();
                    break;
                case "/settings":
                    sendMsg(message, "Что будем настраивать?");
                    break;
                case "Скажи полную дату":
                    Date date = new Date();
                    String sDate = String.format("Текущая дата и время: %tc", date);
                    sendMsg(message, sDate);
                    break;
                case "Скажи время":
                    GregorianCalendar gcalendar = new GregorianCalendar();
                    String sTime = "Текущее  время: " + gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":" + gcalendar.get(Calendar.SECOND);
                    sendMsg(message, sTime);
                    break;
                default:
                    if (flPlus) {
                        sendMsg(message, "Обновление баланса...");
                        account = databaseAccount.selectAccount(chooseAccount, choosePerson);
                        if (databaseAccount.editCountPlus(chooseAccount, account, Integer.parseInt(message.toString()))) {
                            setPM(false, false);
                            sendMsg(message, "Счет пополнен");
                        }
                        else {
                            setPM(false, false);
                            sendMsg(message, "Что-то пошло не так, пожалуйста, попробуйте еще раз");
                        }
                    }
                    if (flAdd) {
                        if (isMatchName(message, message.getText()))
                            break;
                        if (isMatchAge(message, message.getText()))
                            break;
                        if (isMatchPlaceWork(message, message.getText()))
                            break;
                    }
                    if (flChooseClient) {
                        chooseClient(message);
                        break;
                    }
                    if (flEdit) {
                        if (isMatchEditName(message))
                            break;
                        if (isMatchEditAge(message))
                            break;
                        if (isMatchEditPlaceWork(message)) {
                            break;
                        }
                    }
                    if (flChooseAccount) {
                        chooseAccount(message);
                        break;
                    }
                    if (!flAdd && !flChooseClient) {
                        sendMsg(message, "Пожалуйста, выберите нужный пункт в меню!");
                    }
                    break;
            }
        }
    }

    private void previousMenu(Message message) {
        setFlsACEC(false, false, false, false, false);
        String menu = "";
        if (flClientMenu && !flAccountMenu) {
            menu = "Главное меню";
            setFlsMenu(true, false, false);
            choosePerson = 0;
        }
        if (!flMainMenu && flAccountMenu) {
            menu = "Меню клиента";
            setFlsMenu(false, true, false);
            chooseAccount = 0;
        }
        sendMsg(message, menu);
    }


    public void deleteClient(Message message) {
        if (databaseAccount.isCheckAccountBills(choosePerson)) {
            setFlsACEC(false, false, false, false, false);
            sendMsg(message, "Проверть клиента на наличие счетов, если они есть, то сначала закройте их!");
        } else {
            databaseClient.deleteClient(choosePerson);
//        sendMsg(message, "Клиент удален");
            sendMsg(message, "Клиент удален, введите \"Ok\"");
        }
    }

    public void chooseClient(Message message) {
        choosePerson = Integer.parseInt(message.getText());
//        sendMsg(message, "Введите \"Ok\"");
        if (databaseClient.selectClient(Integer.parseInt(message.getText())) != null) {
            setFlsMenu(false, true, false);
            setFlsACEC(false, false, false, false, false);
            sendMsg(message, "Выбран клиент с номером " + Integer.parseInt(message.getText()));
            return;
        } else {
            setFlsMenu(true, false, false);
            setFlsACEC(false, false, false, false, false);
            sendMsg(message, "Неверный номер клиента, попробуй еще раз");
        }

    }

    public void showAllClient(Message message) {
        sendMsg(message, databaseClient.selectAllClient());
    }

    public boolean isMatchName(Message message, String msg) {
        if (!flName && !flAge && !flWorkPlace) {
            Pattern pattern = Pattern.compile("([A-Z][a-z]+[\\-\\s]?){3,}");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                person.setName(msg);
                flName = true;
                sendMsg(message, "Введите возраст клиента (18-99)");
                return true;
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз (NAME)");
                return false;
            }
        }
        return false;
    }

    public boolean isMatchEditName(Message message) {
        if (!flName && !flAge && !flWorkPlace) {
            if (!message.getText().equals("-")) {
                Pattern pattern = Pattern.compile("([A-Z][a-z]+[\\-\\s]?){3,}");
                Matcher matcher = pattern.matcher(message.getText());
                if (matcher.find()) {
                    person.setName(message.getText());
                    flName = true;
                    sendMsg(message, "Введите возраст клиента (18-99 или \"-\", если нужно оставить без изменений");
                    return true;
                } else {
                    sendMsg(message, "Неверно введены данные, попробуйте еще раз (NAME)");
                    return false;
                }
            } else {
                flName = true;
                sendMsg(message, "Введите возраст клиента (18-99 или \"-\", если нужно оставить без изменений");
                return true;
            }
        }
        return false;
    }

    public boolean isMatchAge(Message message, String msg) {
        if (flName && !flAge && !flWorkPlace) {
            if (matchAge(message)) {
                sendMsg(message, "Введите место работы клиента (одним словом, заглавными буквами транслитом");
                return true;
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз (AGE)");
                return false;
            }
        }
        return false;
    }

    public boolean isMatchEditAge(Message message) {
        if (flName && !flAge && !flWorkPlace) {
            if (!message.getText().equals("-")) {
                if (matchAge(message)) {
                    sendMsg(message, "Введите место работы клиента (одним словом, заглавными буквами транслитом или \"-\", если нужно оставить без изменений)");
                    return true;
                } else {
                    sendMsg(message, "Неверно введены данные, попробуйте еще раз (AGE)");
                    return false;
                }
            } else {
                flAge = true;
                sendMsg(message, "Введите место работы клиента (одним словом, заглавными буквами транслитом или \"-\", если нужно оставить без изменений)");
                return true;
            }
        }
        return false;
    }

    public boolean matchAge(Message message) {
        int age = Integer.parseInt(message.getText());
        if ((age >= 18) && (age <= 99)) {
            person.setAge(age);
            flAge = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean isMatchPlaceWork(Message message, String msg) {
        if (flName && flAge && !flWorkPlace) {
            Pattern pattern = Pattern.compile("^[A-Z]+$");

            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                person.setPlaceWork(msg);
                if (databaseClient.addClient(person, this)) {
                    flAdd = false;
                    sendMsg(message, "Клиент добавлен");
                    return true;
                } else {
                    sendMsg(message, "Что-то пошло не так, попробуйте еще раз!");
                    return false;
                }
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз (PW)");
                return false;
            }
        }
        return false;
    }

    public boolean isMatchEditPlaceWork(Message message) {
        if (flName && flAge && !flWorkPlace) {
            if (!message.getText().equals("-")) {
                Pattern pattern = Pattern.compile("^[A-Z]+$");
                Matcher matcher = pattern.matcher(message.getText());
                if (matcher.find()) {
                    person.setPlaceWork(message.getText());
                    return editClientDB(message, person);
                } else {
                    sendMsg(message, "Неверно введены данные, попробуйте еще раз (PW)");
                    return false;
                }
            } else {
                return editClientDB(message, person);
            }
        }
        return false;
    }

    private boolean editClientDB(Message message, Person person) {
        if (databaseClient.editClient(person)) {
            flEdit = false;
            sendMsg(message, "Данные клиента обновлены");
            return true;
        } else {
            sendMsg(message, "Что-то пошло не так, попробуйте еще раз!");
            return false;
        }
    }


    public void addAccount(Message message, int choosePerson) {
        if (databaseAccount.addAccount(choosePerson))
            sendMsg(message, "Счет успешно добавлен");
        else
            sendMsg(message, "Что-то пошло не так, пожалуйста повторите попытку");
    }

    private void chooseAccount(Message message) {
        chooseAccount = Integer.parseInt(message.getText());
//        sendMsg(message, "Введите \"Ok\"");
        if (databaseAccount.selectAccount(chooseAccount, choosePerson) != null) {
            setFlsMenu(false, false, true);
            setFlsACEC(false, false, false, false, false);
            sendMsg(message, "Выбран счет с номером " + Integer.parseInt(message.getText()));
            return;
        } else {
            setFlsMenu(false, true, false);
            setFlsACEC(false, false, false, false, false);
            sendMsg(message, "Неверный номер счета, попробуй еще раз");
        }

    }


    private ReplyKeyboardMarkup getReplyKeyboardMarkup(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        return replyKeyboardMarkup;
    }

    public void setButtonsMainMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(sendMessage);

        keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Добавить клиента"));
        keyboardFirstRow.add(new KeyboardButton("Показать всех клиентов"));
        keyboardFirstRow.add(new KeyboardButton("Выбрать клиента"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void setButtonsClientMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(sendMessage);

        keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Редактировать клиента"));
        keyboardFirstRow.add(new KeyboardButton("Удалить клиента"));
        keyboardFirstRow.add(new KeyboardButton("Информация о счетах"));
        keyboardSecondRow.add(new KeyboardButton("Выбрать счет"));
        keyboardSecondRow.add(new KeyboardButton("Открыть счет"));
        keyboardSecondRow.add(new KeyboardButton("Вернуться назад"));
        keyboardThirdRow.add(new KeyboardButton("Ok"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void setButtonsAccountMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup(sendMessage);


        keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Закрыть счет"));
        keyboardFirstRow.add(new KeyboardButton("Вклад денег"));
        keyboardFirstRow.add(new KeyboardButton("Снятие денег"));
        keyboardSecondRow.add(new KeyboardButton("Просмотр баланса"));
        keyboardSecondRow.add(new KeyboardButton("Просмотр истории"));
        keyboardSecondRow.add(new KeyboardButton("Вернуться назад"));
        keyboardThirdRow.add(new KeyboardButton("Ok"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }


    public String getBotUsername() {
        return "st_ivt_bot";
    }

    public String getBotToken() {
        return "991573534:AAEcq5DR7BE9uGagnNT0p2IfgPfwAnlfhZY";
    }

    private void emptyMethod() {
    }
}