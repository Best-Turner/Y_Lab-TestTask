package ru.ylab.service;

import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;

import java.util.List;
import java.util.Set;

public interface UserService {

    void saveUser(User user);

    User getUser(String email);

    List<User> allUsers();

    void updateUser(String email, User updatedUser);

    boolean delete(String email);

    boolean isExist(String email);

    boolean checkUserCredentials(User userByEmail, String password);

    boolean addWaterCounter(User user, WaterCounter waterCounter);
    Set<WaterCounter> waterCounters(User owner);
}
