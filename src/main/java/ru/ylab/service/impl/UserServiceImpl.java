package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;


    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveUser(User user) {
        if (!isExist(user.getEmail())) {
            Set<WaterMeter> counters = new HashSet<>();
            user.setWaterCounters(counters);
            repository.save(user);
        }
    }

    @Override
    public User getUser(String email) throws UserNotFoundException {
        return repository.getUser(email).orElseThrow(() ->
                new UserNotFoundException("Пользователь с таким - " + email + " не зарегистрирован"));
    }

    @Override
    public List<User> allUsers() {
        List<User> users = repository.usersGroup();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }

    @Override
    public void updateUser(String email, User updatedUser) {
        User oldUser = repository.getUser(email).get();
        repository.save(updatedUser);
        repository.delete(oldUser);
    }

    @Override
    public boolean delete(String email) {
        return repository.delete(repository.getUser(email).get());
    }

    @Override
    public boolean isExist(String email) {
        return repository.isExist(email);
    }

    @Override
    public boolean checkUserCredentials(String inputEmail, String inputPassword) throws UserNotFoundException, InvalidDataException {

        if (!isExist(inputEmail)) {
            throw new UserNotFoundException("Пользователь с таким email - " + inputEmail + " не зарегистрирован.");
        }
        User userByEmail = getUser(inputEmail);
        if (!userByEmail.getPassword().equals(inputPassword)) {
            throw new InvalidDataException("Неверный пароль");
        }
        return true;
    }

    @Override
    public List<WaterMeter> waterCounters(User owner) {
        List<WaterMeter> waterCounters = repository.getWaterCounters(owner.getId());
        if (waterCounters.isEmpty()) {
            return Collections.emptyList();
        }
        return waterCounters;
    }

    @Override
    public User getUserById(long index) {
        return repository.getUser(index);
    }

}
