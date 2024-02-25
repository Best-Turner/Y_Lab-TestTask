package ru.ylab.util.impl;

import ru.ylab.exception.UserNotFoundException;
import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.service.UserService;
import ru.ylab.util.UserValidator;

import java.util.List;

public class UserValidatorImpl implements UserValidator {

    private static long counter;

    private final UserService userService;

    public UserValidatorImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean createUser(String inputName, String inputEmail, String inputPassword) {

        if (!validate(inputEmail, inputPassword)) {
            return false;
        }
        if (inputName == null || inputName.isBlank()) {
            inputName = "<Anonymous>";
        }
        User user = new User(inputName, inputEmail, inputPassword, Role.USER);
        userService.saveUser(user);
        return true;
    }


    @Override
    public boolean isRegister(String email, String password) {
        User userByEmail = findUserByEmail(email);
        if (!validatePassword(password)) {
            return false;
        }
        try {
            return userService.checkUserCredentials(userByEmail, password);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean checkEmail(String email) {
        return validateEmail(email) && isUnique(email);
    }


    @Override
    public boolean delete(String email) {
        if (validateEmail(email)) {
            return userService.delete(email);
        }
        return false;
    }

    @Override
    public void updateUser(User owner, String newName, String newEmail, String newPassword) {

        String oldEmail = owner.getEmail();
        String oldPassword = owner.getPassword();
        if (!canChangeEmail(newEmail)) {
            newEmail = oldEmail;
        }
        if (!validatePassword(newPassword)) {
            System.out.println("Password remained unchanged");
            newPassword = oldPassword;
        }
        if (newName.isBlank() || newName == null) {
            newName = "<Anonymous>";
        }
        User newUser = new User(newName, newEmail, newPassword, Role.USER);
        userService.updateUser(oldEmail, newUser);
    }

    private boolean canChangeEmail(String newEmail) {
        boolean isChange = true;
        if (!validateEmail(newEmail) && !checkEmail(newEmail)) {
            System.out.println("Email remained unchanged");
            isChange = false;
        }
        return isChange;
    }

    @Override
    public User findUserByEmail(String email) {
        if (!validateEmail(email)) {
            System.out.println("Incorrect entered email");
            return null;
        }
        try {
            return userService.getUser(email);
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    @Override
    public List<User> allUsers() {
        List<User> users = userService.allUsers();
        return users;
    }


    @Override
    public boolean isAdmin(User user) {
        return user.getRole().equals(Role.ADMIN);
    }

//    @Override
//    public boolean addCounter(User user, WaterMeter waterCounter) {
//        return userService.addWaterCounter(user, waterCounter);
//    }

    @Override
    public List<WaterMeter> getWaterCounters(User owner) {
        return userService.waterCounters(owner);
    }

    @Override
    public User getById(int index) {
        return userService.getUserById(index);
    }


    private boolean isUnique(String email) {
        if (!userService.isExist(email)) {
            System.out.println("Такой адрес электронной почты уже зарегистрирован");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {
        if (email == null || email.isBlank()) {
            System.out.println("Адрес электронной почты не может быть пустым");
            return false;
        }
        if (email.endsWith("@mail.ru") || email.endsWith("@gmail.com")) {  // простая валидация email
            return true;
        }
        System.out.println("Неверный формат электронной почты");
        return false;
    }

    private boolean validate(String name, String password) {
        return validateEmail(name) && validatePassword(password);
    }

    private boolean validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 4) {
            System.out.println("Пароль должен содержать не менее 4 символов");
            return false;
        }
        return true;
    }
}
