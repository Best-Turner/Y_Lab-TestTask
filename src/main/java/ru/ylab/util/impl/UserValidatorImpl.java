package ru.ylab.util.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.service.UserService;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;

import java.util.List;

public class UserValidatorImpl implements UserValidator {

    private final UserService userService;

    public UserValidatorImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean createUser(String inputName, String inputEmail, String inputPassword) {

        if (!authenticateUser(inputEmail, inputPassword)) {
            return false;
        }
        if (inputName == null || inputName.isBlank()) {
            inputName = "<Anonymous>";
        }
        User user = new User(inputName, inputEmail, inputPassword, Role.USER);
        userService.saveUser(user);
        AuditLogger.log("Новый пользователь сохранен");
        return true;
    }


    @Override
    public boolean isRegister(String email, String password) {
        boolean isRegisterResult = false;
        boolean isCorrectEmail = validateEmail(email);
        if (!isCorrectEmail) {
            return isRegisterResult;
        }
        if (!validatePassword(password)) {
            return isRegisterResult;
        }
        try {
            isRegisterResult = userService.checkUserCredentials(email, password);
        } catch (UserNotFoundException | InvalidDataException e) {
            System.out.println(e.getMessage());
        }
        return isRegisterResult;
    }

    @Override
    public boolean checkEmail(String email) {
        return validateEmail(email) && isUnique(email);
    }


    @Override
    public boolean delete(String email) {
        if (validateEmail(email)) {
            return userService.delete(email);
        }
        return false;
    }


    @Override
    public User findUserByEmail(String email) {
        if (!validateEmail(email)) {
            AuditLogger.log("Ошибка: Не корректно введенный email");
            System.out.println("Не корректно введенный email");
            return null;
        }
        try {
            return userService.getUser(email);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public List<User> allUsers() {
        return userService.allUsers();
    }

    @Override
    public List<WaterMeter> getWaterCounters(User owner) {
        return userService.waterCounters(owner);
    }

    @Override
    public User getById(String inputUserId) throws InvalidDataException {

        long userId = 0;
        try {
            userId = Long.parseLong(inputUserId);
        } catch (NumberFormatException e) {
            AuditLogger.log("Ошибка: Ожидается число.");
            throw new InvalidDataException("Ожидается число.");
        }

        return userService.getUserById(userId);
    }


    private boolean isUnique(String email) {
        if (userService.isExist(email)) {
            AuditLogger.log("Ошибка: Такой адрес электронной почты уже зарегистрирован");
            System.out.println("Такой адрес электронной почты уже зарегистрирован");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            AuditLogger.log("Ошибка: Адрес электронной почты не может быть пустым");
            System.out.println("Адрес электронной почты не может быть пустым");
            return false;
        }
        if (email.endsWith("@mail.ru") || email.endsWith("@gmail.com")) {
            AuditLogger.log("Корректные данные");
            return true;
        }
        System.out.println("Неверный формат электронной почты");
        AuditLogger.log("Ошибка: Неверный формат электронной почты");
        return false;
    }

    private boolean authenticateUser(String name, String password) {
        return validateEmail(name) && validatePassword(password);
    }

    private boolean validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 4) {
            AuditLogger.log("Ошибка: Пароль должен содержать не менее 4 символов");
            System.out.println("Пароль должен содержать не менее 4 символов");
            return false;
        }
        return true;
    }
}
