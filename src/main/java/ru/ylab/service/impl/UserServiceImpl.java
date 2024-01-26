package ru.ylab.service.impl;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void add(User user) {
        if (!repository.isExist(user.getEmail())) {
            repository.addUser(user);
        }
    }

    @Override
    public User getUser(String email) {
        try {
            return repository.getUser(email).orElseThrow(() ->
                    new UserNotFoundException("User with this" + email + " is not registered"));
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<User> allUsers() {
        return repository.userList();
    }

    @Override
    public boolean updateUser(String email, User updatedUser) {
        User user = repository.getUser(email).get();
        repository.addUser(updatedUser);
        repository.delete(user);
        return true;
    }

    @Override
    public boolean delete(String email) {
        return repository.delete(repository.getUser(email).get());
    }
}
