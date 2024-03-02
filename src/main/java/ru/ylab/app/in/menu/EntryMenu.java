package ru.ylab.app.in.menu;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

public class EntryMenu extends Menu {

    private static final String REGISTRATION = "ЗАРЕГИСТРИРОВАТЬСЯ";
    private static final String AUTHENTICATE = "ВОЙТИ В УЧЕТНУЮ ЗАПИСЬ";
    private static final String ENTER_NAME = "Введите имя пользователя: ";
    private static final String ENTER_EMAIL = "Введите адрес электронной почты: ";
    private static final String ENTER_PASSWORD = "Введите пароль: ";
    private static final String CLOSE = "ЗАКРЫТЬ ПРИЛОЖЕНИЕ";
    private final static String ENTRY_MENU =
            "\n" + COMMAND_ONE + " - " + REGISTRATION + "\n"
                    + COMMAND_TWO + " - " + AUTHENTICATE + "\n"
                    + COMMAND_ZERO + " - " + CLOSE + "\n";
    private final UserValidator validator;
    private final WaterCounterValidator waterCounterValidator;

    public EntryMenu(UserValidator validator, WaterCounterValidator waterCounterValidator) {
        this.validator = validator;
        this.waterCounterValidator = waterCounterValidator;
    }

    @Override
    public void start() {
        AuditLogger.log("Начало работы");
        String command;
        while (true) {
            printMenu(ENTRY_MENU);
            command = readCommand("->");
            switch (command) {
                case COMMAND_ONE -> {
                    AuditLogger.log("Регистрация нового пользователя");
                    String message = registerUser() ? "Регистрация успешна." : "Регистрация не выполнена.";
                    AuditLogger.log(message);
                    System.out.println(message);
                }
                case COMMAND_TWO -> {
                    try {
                        new UserMenu(checkCredentials(), validator, waterCounterValidator).start();
                    } catch (UserNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case COMMAND_ZERO -> {
                    AuditLogger.log("Выход из программы");
                    System.exit(0);}
                default -> {
                    AuditLogger.log("Неверная команда " + command);
                    System.out.println("Неверная команда. Попробуйте еще раз");
                }
            }
        }
    }


    private boolean registerUser() {
        System.out.println("Регистрация нового пользователя.");
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
        while (true) {
            String inputEmail = readCommand(ENTER_EMAIL);
            String inputPassword = readCommand(ENTER_PASSWORD);
            boolean register = validator.isRegister(inputEmail, inputPassword);
            if (!register) {
                AuditLogger.log("Пользователь не зарегистрирован");
                throw new UserNotFoundException("Попробуйте еще раз или зарегистрируйтесь");
            }
            return validator.findUserByEmail(inputEmail);
        }
    }
}
