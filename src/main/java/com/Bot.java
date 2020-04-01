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
    private boolean flEdit = false;

    private boolean flName = false;
    private boolean flAge = false;
    protected boolean flWorkPlace = false;

    private List<KeyboardRow> keyboardRowList;

    public Bot() {
        databaseClient = new DatabaseClient();
    }

    protected void setFlsACE(boolean flAdd, boolean flChoose, boolean flEdit) {
        this.flAdd = flAdd;
        this.flChoose = flChoose;
        this.flEdit = flEdit;
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
                    setFlsACE(false, false, false);
                    setFlsNA(false, false, false);
                    sendMsg(message, "Балыбердин, Билалов, Демидов, Дроздов ИВТ-414, Сетевые технологии");
                    break;
                case "Добавить клиента":
                    setFlsACE(true, false, false);
                    setFlsNA(false, false, false);
                    person = new Person();
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество транслитом)");
                    break;
                case "Показать всех клиентов":
                    setFlsACE(false, false, false);
                    setFlsNA(false, false, false);
                    showAllClient(message);
                    break;
                case "Выбрать клиента":
                    setFlsACE(false, true, false);
                    setFlsNA(false, false, false);
                    sendMsg(message, "Пожалуйста, пришлите номер выбранного клиента");
                    break;
                case "Вернуться назад":
                    previousMenu(message);
                    break;
                case "Удалить клиента":
                    deleteClient(message);
                    break;
                case "Редактировать клиента":
                    setFlsACE(false, false, true);
                    setFlsNA(false, false, false);
                    person = null;
                    person = databaseClient.selectClient(choosePerson);
                    sendMsg(message, "Введите ФИО клиента (в формате Фамилия Имя Отчество транслитом или \"-\", если нужно оставить без изменений)");
                    break;
                case "Открыть счет":
                    addAccount(message, choosePerson);
                    break;
                case "Информация о счетах":
                    sendMsg(message, databaseClient.selectAllAccount(choosePerson));
                    break;
                case "Ok":
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
                        if (isMatchName(message, message.getText()))
                            break;
                        if (isMatchAge(message, message.getText()))
                            break;
                        if (isMatchPlaceWork(message, message.getText()))
                            break;
                    }
                    if (flChoose) {
                        chooseClient(message);
                        break;
                    }
                    if (flEdit) {
                        if (isMatchEditName(message))
                            break;
                        if (isMatchEditAge(message))
                            break;
                        if (isMatchEditPlaceWork(message))
                            break;
                    }
                    if (!flAdd && !flChoose) {
                        sendMsg(message, "Пожалуйста, выберите нужный пункт в меню!");
                    }
                    break;
            }
        }
    }

    private void previousMenu(Message message) {
        String menu = "";
        if (!flMainMenu && flAccountMenu) {
            menu = "Меню клиента";
            setFlsMenu(false, true, false);
        }
        if (flClientMenu && !flAccountMenu) {
            menu = "Главное меню";
            setFlsMenu(true, false, false);
            choosePerson = 0;
        }
        sendMsg(message, menu);
    }


    public void deleteClient(Message message) {
        databaseClient.deleteClient(choosePerson);
//        sendMsg(message, "Клиент удален");
        sendMsg(message, "Клиент удален, введите \"Ok\"");
    }

    public void chooseClient(Message message) {
        choosePerson = Integer.parseInt(message.getText());
//        sendMsg(message, "Введите \"Ok\"");
        if (databaseClient.selectClient(Integer.parseInt(message.getText())) != null) {
            setFlsMenu(false, true, false);
            setFlsACE(false, false, false);
            sendMsg(message, "Выбран клиент с номер " + Integer.parseInt(message.getText()));
            return;
        } else {
            setFlsMenu(true, false, false);
            setFlsACE(false, false, false);
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
        if (databaseClient.addAccount(choosePerson))
            sendMsg(message, "Счет успешно добавлен");
        else
            sendMsg(message, "Что-то пошло не так, пожалуйста повторите попытку");
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


    public String getBotUsername() {
        return "st_ivt_bot";
    }

    public String getBotToken() {
        return "991573534:AAEcq5DR7BE9uGagnNT0p2IfgPfwAnlfhZY";
    }

    private void emptyMethod() {
    }
}