package ru.ylab.service.impl;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.UserService;
import ru.ylab.util.UserValidator;

import java.util.Collections;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserValidator validator;

    public UserServiceImpl(UserRepository repository, UserValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public void saveUser(String name, String email, String password) throws InvalidDataException {
        validator.validateEmail(email);
        if (exists(email)) {
            throw new InvalidDataException("Такой адрес электронной почты уже зарегистрирован");
        }
        validator.validatePassword(password);
        if (validator.validUserName(name)) {
            name = "<<Anonymous>>";
        }
        repository.save(new User(name, email, password, Role.USER));
    }

    @Override
    public User getUserByEmail(String email) throws InvalidDataException, UserNotFoundException {
        validator.validateEmail(email);
        return repository.getUser(email).orElseThrow(() ->
                new UserNotFoundException("Пользователь с таким - " + email + " не зарегистрирован"));
    }

    @Override
    public User getUserById(String inputUserId) throws InvalidDataException {
        validator.validUserId(inputUserId);
        return repository.getUser(Long.parseLong(inputUserId));
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
    public boolean checkUserCredentials(String inputEmail, String inputPassword) throws UserNotFoundException, InvalidDataException {
        validator.validateEmail(inputEmail);
        validator.validatePassword(inputPassword);
        if (!exists(inputEmail)) {
            throw new UserNotFoundException("Пользователь с таким email - " + inputEmail + " не зарегистрирован.");
        }
        User userByEmail = getUserByEmail(inputEmail);
        if (!userByEmail.getPassword().equals(inputPassword)) {
            throw new InvalidDataException("Неверный пароль");
        }
        return true;
    }
    @Override
    public List<WaterMeter> getWaterCounters(User owner) {
        List<WaterMeter> waterCounters = repository.getWaterCounters(owner.getId());
        if (waterCounters.isEmpty()) {
            return Collections.emptyList();
        }
        return waterCounters;
    }

    private boolean exists(String email) {
        return repository.isExist(email);
    }
}
