package ru.ylab.app.in;

import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.util.Scanner;

public class ConsoleServiceMediator {
    private static final String COMMAND_ONE = "1";
    private static final String COMMAND_TWO = "2";
    private static final String COMMAND_THREE = "3";
    private static final String COMMAND_FOUR = "4";
    private static final String COMMAND_FIVE = "5";
    private static final String COMMAND_SIX = "6";
    private static final String COMMAND_SEVEN = "7";
    private static final String REGISTRATION = " - Registration account";
    private static final String AUTHENTICATE = " - Login to account";
    private static final String REGISTER_COUNTER = " - Register counter";
    private static final String COUNTERS = " - Get list my counters";
    private static final String SETTINGS = " - Settings profile";
    private static final String USERS = " - Get all users";
    private static final String USER = " - Get user by email";
    private static final String CURRENT_VALUE = " - Get current value";
    private static final String HISTORY_VALUES = " - Get the history of transferred data";
    private static final String VALUES_BY_MONTH = " - Get data for a specific month";
    private static final String TRANSFER_DATA = " - Transfer counter data";
    private static final String ENTER_NAME = " - Enter your name:";
    private static final String ENTER_EMAIL = " - Enter your email:";
    private static final String ENTER_PASSWORD = " - Enter your password:";
    private static final String WRONG_COMMAND = " Wrong command";


    private static final String COMMAND_BACK = "0 - Back";
    private static final String EXIT = "0 - Exit";
    private final UserValidator userValidator;
    private final WaterCounterValidator counterValidator;
    private Scanner scanner;

    public ConsoleServiceMediator(UserValidator userValidator, WaterCounterValidator counterValidator) {
        this.userValidator = userValidator;
        this.counterValidator = counterValidator;
        this.scanner = new Scanner(System.in);
    }

    private void startPage() {
        System.out.println(COMMAND_ONE + REGISTRATION);
        System.out.println(COMMAND_TWO + AUTHENTICATE);
    }

    private void mainMenu() {
        System.out.println(COMMAND_ONE + REGISTER_COUNTER);
        System.out.println(COMMAND_TWO + TRANSFER_DATA);
        System.out.println(COMMAND_THREE + COUNTERS);
        System.out.println(COMMAND_FOUR + COUNTERS);

        System.out.println(COMMAND_FIVE + SETTINGS);
    }


    private void mainMenuForAdmin() {
        mainMenu();
        System.out.println(COMMAND_SIX + USER);
        System.out.println(COMMAND_SEVEN + USERS);

    }

    public User processStartPageCommand(String command) {
        startPage();
        User registeredUser = null;
        switch (command) {
            case "1": {
                String message = registerUser() ? "Registration successful!" : "Registration failed!";
                System.out.println(message);
                break;
            }
            case "2": {
                String email = readEmail();
                User userByEmail = userValidator.findUserByEmail(email);
                if (userByEmail == null) {
                    System.out.println("Invalid email or you are not registered");
                    break;
                }
                registeredUser = isUserRegistered(email) ? userByEmail : null;
                break;
            }
            default:
                System.out.println("Wrong command");
        }
        return registeredUser;

    }

    public void pageForRegisteredUser(User owner) {
        if (owner.getRole().equals(Role.ADMIN)) {
            mainMenuForAdmin();
            System.out.println("Enter " + EXIT + " to exit");
            String command = readUserCommand();
            switch (command) {
                case COMMAND_ONE -> registerCounter(owner);

                case "2" -> getMyListCounters(owner);

                case "3" -> settingsMenu(owner);

                case "4" -> getUserByEmail();
                case "5" -> getUsers();
                case "0" -> {
                    break;
                }
                default -> System.out.println(WRONG_COMMAND);
            }
        } else {
            mainMenu();
            System.out.println("Enter " + EXIT + " to exit");

        }
    }

    private void getUsers() {
        userValidator.allUsers().forEach(user
                -> System.out.println(
                String.format("Name - %s\nEmail - $s\nRole - %s", user.getName(), user.getEmail(), user.getRole())));
    }

    private void getUserByEmail() {
        System.out.println("Enter the email of the user you want to receive:\n");
        String inputEmail = readUserCommand();
        User userByEmail = userValidator.findUserByEmail(inputEmail);
        if (userByEmail == null) {

            System.out.println(String.format("Name - %s\nEmail - %s\nRole - %s",
                    userByEmail.getName(), userByEmail.getEmail(), userByEmail.getRole()));
            System.out.println("List water counters:\n");
            userByEmail
                    .getWaterCounterList()
                    .forEach(counter -> System.out.println(counter.getSerialNumber()));
        }

    }

    private void settingsMenu(User owner) {
        System.out.println(COMMAND_ONE + "Change your data");
        System.out.println(COMMAND_TWO + "Delete counter");
        String command = readUserCommand();
        switch (command) {
            case "1" -> changeMyData(owner);
        }

    }

    private void changeMyData(User owner) {
        System.out.println(COMMAND_ONE + "Change name");
        System.out.println(COMMAND_TWO + "Change email");
        System.out.println(COMMAND_THREE + "Change password");
        System.out.println(COMMAND_BACK + "Back");
        String command = readUserCommand();
        switch (command) {
            case "1" -> changeName(owner);
            case "2" -> changeEmail(owner);
            case "3" -> changePassword(owner);
            case "0" -> {
                return;
            }
            default -> System.out.println("Wrong command");
        }
    }

    private void changePassword(User userToUpdate) {
        String newPassword = readInputPassword();
        userValidator.updateUser(userToUpdate, null, null, newPassword);
    }

    private void changeEmail(User userToUpdate) {
        String newEmail = readEmail();
        userValidator.updateUser(userToUpdate, null, newEmail, null);
    }

    private void changeName(User userToUpdate) {
        String newName = readUserCommand();
        userValidator.updateUser(userToUpdate, newName, null, null);
    }

    private void getMyListCounters(User owner) {
        userValidator.getWaterCounters(owner).forEach(counter
                -> System.out.println("List your counters: \n" +
                "Serial number: " + counter.getSerialNumber() +
                "\nCurrent value: " + counter.getCurrentValue()));
    }

    private void registerCounter(User owner) {
        System.out.println("Enter serial number the counter");
        String inputSerialNumber = readUserCommand();
        System.out.println("Enter initial value");
        String initialValue = readUserCommand();
        Float inputValue;
        try {
            inputValue = Float.valueOf(initialValue);
        } catch (NumberFormatException e) {
            System.out.println("Invalid format");
        }
        inputValue = 0f;
        String message = counterValidator
                .createCounter(inputSerialNumber, inputValue, owner) ? "Registered successful" : "Failed to register";
        System.out.println(message);
    }

    private boolean isUserRegistered(String email) {
        boolean isRegister = false;
        while (true) {
            String password = readInputPassword();
            if (password.equals(EXIT)) {
                return isRegister;
            }
            if (!checkCredentials(email, password)) {
                System.out.println("Incorrect password!");
                System.out.println("Enter " + EXIT + "to exit");
                continue;
            } else {
                isRegister = true;
            }
            return isRegister;
        }
    }

    private String readEmail() {
        System.out.println(ENTER_EMAIL);
        return readUserCommand();
    }

    private String readInputPassword() {
        System.out.println(ENTER_PASSWORD);
        return readUserCommand();
    }

    private boolean registerUser() {
        System.out.println("New user registration");
        System.out.println(ENTER_NAME);
        String name = readUserCommand();
        System.out.println(ENTER_EMAIL);
        String email = readUserCommand();
        System.out.println(ENTER_PASSWORD);
        String password = readUserCommand();
        return userValidator.createUser(name, email, password);
    }

    private boolean checkCredentials(String email, String password) {
        return userValidator.isRegister(email, password);
    }

    private String readUserCommand() {
        System.out.print("Enter your choice: ");
        return scanner.nextLine();
    }

}