package ru.ylab.util.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;

public class UserValidatorImpl implements UserValidator {

    @Override
    public boolean validateEmail(String inputEmail) throws InvalidDataException {
        if (inputEmail == null || inputEmail.isBlank()) {
            AuditLogger.log("Ошибка: Адрес электронной почты не может быть пустым");
            throw new InvalidDataException("Адрес электронной почты не может быть пустым");
        } else if (inputEmail.endsWith("@mail.ru") || inputEmail.endsWith("@gmail.com")) {
            AuditLogger.log("Корректные данные");
            return true;
        }
        AuditLogger.log("Ошибка: Неверный формат электронной почты");
        throw new InvalidDataException("Неверный формат электронной почты");
    }

    @Override
    public boolean validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 4) {
            AuditLogger.log("Ошибка: Пароль должен содержать не менее 4 символов");
            System.out.println("Пароль должен содержать не менее 4 символов");
            return false;
        }
        return true;
    }

    @Override
    public void validUserId(String inputUserId) throws InvalidDataException {
        long userId;
        try {
            userId = Long.parseLong(inputUserId);
            if (userId <= 0) {
                throw new InvalidDataException("ID пользователя должно быть положительным");
            }
        } catch (NumberFormatException e) {
            AuditLogger.log("Ошибка: Ожидается число.");
            throw new InvalidDataException("Ожидается число.");
        }
    }

    @Override
    public boolean validUserName(String inputName) {
        return inputName != null && !inputName.isBlank();
    }

}
