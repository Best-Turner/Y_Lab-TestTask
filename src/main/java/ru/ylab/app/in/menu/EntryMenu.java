package ru.ylab.app.in.menu;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.io.IOException;

public class EntryMenu extends Menu {

    private static final String REGISTRATION = "Зарегистрироваться";
    private static final String AUTHENTICATE = "Войти в учетную запись";
    private static final String ENTER_NAME = "Введите имя пользователя";
    private static final String ENTER_EMAIL = "Введите адрес электронной почты";
    private static final String ENTER_PASSWORD = "Введите пароль";
    private static final String CLOSE = "Закрыть приложение";

    private final UserValidator validator;
    private final WaterCounterValidator waterCounterValidator;


    private final static String ENTRY_MENU = COMMAND_ONE + " - " + REGISTRATION + "\n"
            + COMMAND_TWO + " - " + AUTHENTICATE + "\n"
            + COMMAND_ZERO + " - " + CLOSE + "\n";

    public EntryMenu(UserValidator validator, WaterCounterValidator waterCounterValidator) {
        this.validator = validator;
        this.waterCounterValidator = waterCounterValidator;
    }

    @Override
    public void start() {
        String command = "";
        while (true) {
            printMenu(ENTRY_MENU);
            command = readCommand("->");
            switch (command) {
                case COMMAND_ONE -> {
                    String message = registerUser() ? "Регистрация успешна" : "Регистрация не выполнена";
                    System.out.println(message);
                }
                case COMMAND_TWO -> {
                    try {
                        new UserMenu(checkCredentials(), validator, waterCounterValidator);
                    } catch (UserNotFoundException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                }
                case COMMAND_ZERO -> {
                    System.exit(0);
                }
                default -> System.out.println("Неверная команда. Попробуйте еще раз");
            }
        }
    }


    private boolean registerUser() {
        System.out.println("Регистрация нового пользователя");
        String name = readCommand(ENTER_NAME);
        String email = readCommand(ENTER_EMAIL);
        boolean isValid = validator.checkEmail(email);
        if (!isValid) {
            return false;
        }
        String password = readCommand(ENTER_PASSWORD);
        return validator.createUser(name, email, password);
    }

    private User checkCredentials() throws UserNotFoundException {
        String inputEmail = readCommand(ENTER_EMAIL);
        String inputPassword = readCommand(ENTER_PASSWORD);
        boolean register = validator.isRegister(inputEmail, inputPassword);
        if (!register) {
            throw new UserNotFoundException("Неверный адрес электронной почты или пароль");
        }
        return validator.findUserByEmail(inputEmail);
    }
}
