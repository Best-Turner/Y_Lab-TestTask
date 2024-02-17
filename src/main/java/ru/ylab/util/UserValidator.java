package ru.ylab.util;

import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;

import java.util.List;

public interface UserValidator {
    boolean createUser(String inputName, String inputEmail, String inputPassword);

    boolean isRegister(String email, String password);

    boolean isUnique(String email);

    boolean delete(String email);

    void updateUser(User owner, String name, String email, String password);

    User findUserByEmail(String email);

    List<User> allUsers();

    boolean isAdmin(User user);

//    boolean addCounter(User user, WaterMeter waterCounter);

    List<WaterMeter> getWaterCounters(User owner);

    User getById(int index);

}
