package ru.ylab.service;

import ru.ylab.model.User;

import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public void add(User user) {

    }

    @Override
    public User getUser(String email) {
        return null;
    }

    @Override
    public List<User> allUsers() {
        return null;
    }

    @Override
    public boolean updateUser(String email, User updatedUser) {
        return false;
    }

    @Override
    public boolean delete(String email) {
        return false;
    }
}
