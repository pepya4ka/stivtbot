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
    private boolean flName = false;
    private boolean flAge = false;
    private boolean flPlaceWork = false;

    public Bot() {
        databaseClient = new DatabaseClient();
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
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
                    sendMsg(message, "Балыбердин, Билалов, Демидов, Дроздов ИВТ-414, Сетевые технологии");
                    break;
                case "Добавить клиента":
                    person = new Person();
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество на английском языке)");
                    break;
                case "Показать всех клиентов":
                    showAllClient(message);
                    break;
                case "Выбрать клиента":
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
                    String sTime = String.format("Текущее  время: " + gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":" + gcalendar.get(Calendar.SECOND));
                    sendMsg(message, sTime);
                    break;
                default:
                    if (!flName && !flAge && !flPlaceWork) {
                        flName = isMatchName(message, message.getText());
                    }
                    if (flName && !flAge && !flPlaceWork) {
                        flAge = isMatchAge(message, message.getText());
                    }
                    if (flName && flAge && !flPlaceWork) {
                        flPlaceWork = isMatchPlaceWork(message, message.getText());
                    }
                    if (flPlaceWork)
                        sendMsg(message, "Клиент успешно добавлен");
            }
        }
    }

    public void showAllClient(Message message) {

        //List<Person> personList = databaseClient.selectAllClient();
        List<Person> personList = new ArrayList<>();
        personList.addAll(databaseClient.selectAllClient());
        StringBuilder stringBuilder = new StringBuilder();


        stringBuilder.append("Список клиентов:\n");
        for (Person temp : personList) {
            stringBuilder.append(temp.getId());
            stringBuilder.append(". ");
            stringBuilder.append(temp.getName());
            stringBuilder.append("\n");
        }

        sendMsg(message, stringBuilder.toString());
    }

    public boolean isMatchName(Message message, String msg) {
        Pattern pattern = Pattern.compile("([A-Z][a-z]+[\\-\\s]?){3,}");

        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            person.setName(msg);
            sendMsg(message, "Введите возраст клиента (18-99)");
            return true;
        } else {
            sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            return false;
        }
    }

    public boolean isMatchAge(Message message, String msg) {
        int age = Integer.parseInt(msg);
        if ((age >= 18) && (age <= 99)) {
            person.setAge(age);
            sendMsg(message, "Введите место работы клиента (одним словом, заглавными буквами на английском языке)");
            return true;
        } else {
            sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            return false;
        }
    }

    public boolean isMatchPlaceWork(Message message, String msg) {
        Pattern pattern = Pattern.compile("^[A-Z]+$");

        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            person.setPlaceWork(msg);
            databaseClient.addClient(person);
            flName = false;
            flAge = false;
            flPlaceWork = false;
            return true;
        } else {
            sendMsg(message, "Неверно введены данные, попробуйте еще раз");
            return false;
        }
    }


    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Добавить клиента"));
        keyboardFirstRow.add(new KeyboardButton("Показать всех клиентов"));
        keyboardFirstRow.add(new KeyboardButton("Выбрать клиента"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "st_ivt_bot";
    }

    public String getBotToken() {
        return "991573534:AAEcq5DR7BE9uGagnNT0p2IfgPfwAnlfhZY";
    }
}