package com;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {


    private static final String driverName = "com.mysql.jdbc.Driver";
    private static final String connectionString = "jdbc:mysql://us-cdbr-iron-east-01.cleardb.net";
    private static final String login = "baab4a4396de37";
    private static final String password = "3f075f47";

    public static void main(String[] args) {

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't get class. No driver found");
            e.printStackTrace();
            return;
        }

        Connection connection = null;
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

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private void connectToDB() {

    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
//            sendMessage(sendMessage);
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
                    sendMsg(message, "Вырубай нафик");
                    break;
                case "Привет":
                    sendMsg(message, "Привет!");
                    break;
                case "привет":
                    String msg;
                    if (message.getFrom().getUserName().equals("sausageRoll") || message.getFrom().getUserName().equals("pepyachka"))
                        msg = "Привет, сосунок";
                    else
                        msg = "Привет, " + message.getFrom().getFirstName();
                    sendMsg(message, msg);
                    break;
                case "/help":
                    sendMsg(message, "Чем я могу помочь?");
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
            }
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

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/settings"));

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