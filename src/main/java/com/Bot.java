package com;

import bank.Person;
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
    Person person;
    int choosePerson;
    private boolean flMainMenu = false;
    private boolean flClientMenu = false;
    private boolean flAccountMenu = false;

    private boolean flAdd = false;
    private boolean flChoose = false;

    private boolean flName = false;
    private boolean flAge = false;
    protected boolean flWorkPlace = false;

    private List<KeyboardRow> keyboardRowList;

    public Bot() {
        databaseClient = new DatabaseClient();
    }

    protected void setFlsAC(boolean flAdd, boolean flChoose) {
        this.flAdd = flAdd;
        this.flChoose = flChoose;
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
                    setFlsAC(false, false);
                    setFlsNA(false, false, false);
                    sendMsg(message, "Балыбердин, Билалов, Демидов, Дроздов ИВТ-414, Сетевые технологии");
                    break;
                case "Добавить клиента":
                    setFlsAC(true, false);
                    setFlsNA(false, false, false);
                    person = new Person();
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество на английском языке)");
                    break;
                case "Показать всех клиентов":
                    setFlsAC(false, false);
                    setFlsNA(false, false, false);
                    showAllClient(message);
                    break;
                case "Выбрать клиента":
                    setFlsAC(false, true);
                    setFlsNA(false, false, false);
                    sendMsg(message, "Пожалуйста, пришлите номер выбранного клиента");
                    break;
                case "Вернуться назад":
                    previousMenu(message);
                    break;
                case "Удалить клиента":
                    deleteClient(message);
                    break;
                case "ok":
                    previousMenu(message);
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
                    if (flAdd) {
                        isMatchName(message, message.getText());
                        isMatchAge(message, message.getText());
                        isMatchPlaceWork(message, message.getText());
                    }
                    if (flChoose) {
                        chooseClient(message);
                    }
                    if (!flAdd && !flChoose) {
                        sendMsg(message, "Пожалуйста, выберите нужный пункт в меню!");
                    }
                    break;
            }
        }
    }

    private void emptyMethod() {
    }

    private void previousMenu(Message message) {
        if (!flMainMenu && flAccountMenu) {
            setFlsMenu(false, true, false);
        }
        if (flClientMenu && !flAccountMenu) {
            setFlsMenu(true, false, false);
            choosePerson = 0;
        }
    }

    public void deleteClient(Message message) {
        databaseClient.deleteClient(choosePerson);
        sendMsg(message, "Клиент удален, введите \"ok\"");
    }

    public void chooseClient(Message message) {
        sendMsg(message, "Выбран клиент с номер " + Integer.parseInt(message.getText()));
        choosePerson = Integer.parseInt(message.getText());
//        sendMsg(message, "Введите \"ok\"");
        if (databaseClient.selectClient(Integer.parseInt(message.getText())) != null) {
            setFlsMenu(false, true, false);
            setFlsAC(false, false);
            return;
        } else {
            sendMsg(message, "Неверный номер клиента, попробуй еще раз");
            setFlsMenu(true, false, false);
            setFlsAC(false, false);
        }

    }

    public void showAllClient(Message message) {
        sendMsg(message, databaseClient.selectAllClient());
    }

    public void isMatchName(Message message, String msg) {
        if (!flName && !flAge && !flWorkPlace) {
            Pattern pattern = Pattern.compile("([A-Z][a-z]+[\\-\\s]?){3,}");

            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                person.setName(msg);
                flName = true;
                sendMsg(message, "Введите возраст клиента (18-99)");
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            }
        }
    }

    public void isMatchAge(Message message, String msg) {
        if (flName && !flAge && !flWorkPlace) {
            int age = Integer.parseInt(msg);
            if ((age >= 18) && (age <= 99)) {
                person.setAge(age);
                flAge = true;
                sendMsg(message, "Введите место работы клиента (одним словом, заглавными буквами на английском языке)");
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            }
        }
    }

    public void isMatchPlaceWork(Message message, String msg) {
        if (flName && flAge && !flWorkPlace) {
            Pattern pattern = Pattern.compile("^[A-Z]+$");

            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {
                person.setPlaceWork(msg);
                flAdd = false;
                if (databaseClient.addClient(person, this))
                    sendMsg(message, "Клиент добавлен");
                else
                    sendMsg(message, "Что-то пошло не так, попробуйте еще раз!");
            } else {
                sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            }
        }
    }


    public void setButtonsMainMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Добавить клиента"));
        keyboardFirstRow.add(new KeyboardButton("Показать всех клиентов"));
        keyboardFirstRow.add(new KeyboardButton("Выбрать клиента"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void setButtonsClientMenu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Редактировать клиента"));
        keyboardFirstRow.add(new KeyboardButton("Удалить клиента"));
        keyboardFirstRow.add(new KeyboardButton("Информация о счетах"));
        keyboardSecondRow.add(new KeyboardButton("Выбрать счет"));
        keyboardSecondRow.add(new KeyboardButton("Открыть счет"));
        keyboardSecondRow.add(new KeyboardButton("Вернуться назад"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "st_ivt_bot";
    }

    public String getBotToken() {
        return "991573534:AAEcq5DR7BE9uGagnNT0p2IfgPfwAnlfhZY";
    }
}