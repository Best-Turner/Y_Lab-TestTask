package ru.ylab.service;

import ru.ylab.model.User;

import java.util.List;

public interface UserService {

    void add(User user);

    User getUser(String email);

    List<User> allUsers();

    boolean updateUser(String email, User updatedUser);

    boolean delete(String email);
}
