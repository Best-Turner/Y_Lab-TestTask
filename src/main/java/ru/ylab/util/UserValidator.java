package ru.ylab.util;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface UserValidator {
    boolean createUser(String inputName, String inputEmail, String inputPassword);

    boolean isRegister(String email, String password);

    boolean checkEmail(String email);

    boolean delete(String email);

    User findUserByEmail(String email);

    List<User> allUsers();


    List<WaterMeter> getWaterCounters(User owner);

    User getById(String inputId) throws InvalidDataException;

}
