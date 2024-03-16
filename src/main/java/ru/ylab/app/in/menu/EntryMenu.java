package ru.ylab.app.in.menu;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.service.UserService;
import ru.ylab.util.AuditLogger;
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
    private final WaterCounterValidator waterCounterValidator;
    private final UserService userService;

    public EntryMenu(WaterCounterValidator waterCounterValidator, UserService userService) {
        this.waterCounterValidator = waterCounterValidator;
        this.userService = userService;
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
                        new UserMenu(getUserByLoginAndPassword(), userService, waterCounterValidator).start();
                    } catch (UserNotFoundException | InvalidDataException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case COMMAND_ZERO -> {
                    AuditLogger.log("Выход из программы");
                    System.exit(0);
                }
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
        String password = readCommand(ENTER_PASSWORD);
        try {
            userService.saveUser(name, email, password);
            AuditLogger.log("Пользователь зарегистрирован");
            return true;
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
            AuditLogger.log("Пользователь не зарегистрирован");
            return false;
        }
    }

    private User getUserByLoginAndPassword() throws UserNotFoundException, InvalidDataException {
        while (true) {
            String inputEmail = readCommand(ENTER_EMAIL);
            String inputPassword = readCommand(ENTER_PASSWORD);
            boolean register = userService.checkUserCredentials(inputEmail, inputPassword);
            if (!register) {
                AuditLogger.log("Пользователь не зарегистрирован");
                throw new UserNotFoundException("Попробуйте еще раз или зарегистрируйтесь");
            }
            return userService.getUserByEmail(inputEmail);
        }
    }
}
