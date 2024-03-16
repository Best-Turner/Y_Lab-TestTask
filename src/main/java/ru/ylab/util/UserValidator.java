package ru.ylab.util;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface UserValidator {

    boolean validateEmail(String inputEmail) throws InvalidDataException;
    boolean validatePassword(String password);
    void validUserId(String inputUserId) throws InvalidDataException;
    boolean validUserName(String inputName);

}
