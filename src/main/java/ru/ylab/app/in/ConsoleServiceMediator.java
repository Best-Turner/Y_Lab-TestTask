package ru.ylab.app.in;

import ru.ylab.model.Role;
import ru.ylab.model.User;
import ru.ylab.model.WaterCounter;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleServiceMediator {
    private static final String COMMAND_ONE = "1";
    private static final String COMMAND_TWO = "2";
    private static final String COMMAND_THREE = "3";
    private static final String COMMAND_FOUR = "4";
    private static final String COMMAND_FIVE = "5";
    private static final String REGISTRATION = " - Registration account";
    private static final String AUTHENTICATE = " - Login to account";
    private static final String REGISTER_COUNTER = " - Register counter";
    private static final String COUNTERS = " - Get list my counters";
    private static final String COUNTER = " - Get my counter";
    private static final String SETTINGS = " - Settings profile";
    private static final String USERS = " - Get all users";
    private static final String USER = " - Get user by email";
    private static final String CURRENT_VALUE = " - Get current value";
    private static final String HISTORY_VALUES = " - Get the history of transferred data";
    private static final String VALUES_BY_MONTH = " - Get data for a specific month";
    private static final String TRANSFER_DATA = " - Transfer counter data";
    private static final String ENTER_NAME = "Enter your name:";
    private static final String ENTER_EMAIL = "Enter your email:";
    private static final String ENTER_PASSWORD = "Enter your password:";
    private static final String WRONG_COMMAND = "Wrong command";


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
        AuditLogger.log("Start program");
        System.out.println(COMMAND_ONE + REGISTRATION);
        System.out.println(COMMAND_TWO + AUTHENTICATE);
    }

    private void mainMenu() {
        AuditLogger.log("User has entered the main menu");
        System.out.println(COMMAND_ONE + REGISTER_COUNTER);
        System.out.println(COMMAND_TWO + COUNTERS);
        System.out.println(COMMAND_THREE + SETTINGS);
    }


    private void mainMenuForAdmin() {
        AuditLogger.log("Admin has entered the main menu");
        mainMenu();
        System.out.println(COMMAND_FOUR + USER);
        System.out.println(COMMAND_FIVE + USERS);
    }

    public User processStartPageCommand() {
        startPage();
        User registeredUser = null;
        String command = readUserCommand();
        switch (command) {
            case COMMAND_ONE: {
                AuditLogger.log("User entered command - 1");
                String message = registerUser() ? "Registration successful!\n" : "Registration failed!\n";
                System.out.println(message);
                break;
            }
            case COMMAND_TWO: {
                AuditLogger.log("User entered command - 2");
                String email = readEmail();
                User userByEmail = userValidator.findUserByEmail(email);
                if (userByEmail == null) {
                    System.out.println("Invalid email or you are not registered");
                    break;
                }
                registeredUser = isUserRegistered(email) ? userByEmail : null;
                break;
            }
            default: {
                AuditLogger.log(WRONG_COMMAND + " - " + command);
                System.out.println("Wrong command");
            }
        }
        return registeredUser;

    }

    public void pageForRegisteredUser(User owner) {

        if (owner.getRole().equals(Role.ADMIN)) {
            mainMenuForAdmin();
            System.out.println("Enter " + EXIT + " to exit");

            String command = readUserCommand();
            switch (command) {
                case COMMAND_ONE -> {
                    AuditLogger.log("Admin" + owner.getEmail() + " entered command - 1");
                    registerCounter(owner);
                }

                case "2" -> {
                    AuditLogger.log("Admin" + owner.getEmail() + " entered command - 2");
                    getListCounters(owner);
                }

                case "3" -> {
                    AuditLogger.log("Admin" + owner.getEmail() + " entered command - 3");
                    settingsMenu(owner);
                }

                case "4" -> {
                    AuditLogger.log("Admin" + owner.getEmail() + " entered command - 4");
                    getUserByEmail();
                }
                case "5" -> {
                    AuditLogger.log("Admin" + owner.getEmail() + " entered command - 5");
                    getUsers();
                }
                case "0" -> {
                    AuditLogger.log("Admin " + owner.getEmail() + " entered command - 0");
                    break;
                }
                default -> {
                    AuditLogger.log("Admin "
                            + owner.getEmail() +
                            " entered command - "
                            + WRONG_COMMAND + " - " + command);
                    System.out.println(WRONG_COMMAND);
                }
            }
        } else {
            AuditLogger.log("User has entered the main menu");
            mainMenu();
            System.out.println("Enter " + EXIT + " to exit");
            String command = readUserCommand();
            switch (command) {
                case COMMAND_ONE -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - 1");
                    registerCounter(owner);
                }

                case COMMAND_TWO -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - 2");
                    getListCounters(owner);
                }

                case COMMAND_THREE -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - 3");
                    settingsMenu(owner);
                }
                case "0" -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - 0");
                    break;
                }
                default -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - " + WRONG_COMMAND);
                    System.out.println(WRONG_COMMAND);
                }
            }


        }
    }

    private void getUsers() {
        AuditLogger.log("Get all users");
        List<User> users = userValidator.allUsers();

        users.forEach(user ->
                System.out.println(
                        String.format("Id - %d\nName - %s\nEmail - $s\nRole - %s",
                                user.getId(), user.getName(), user.getEmail(), user.getRole())));
        getInfoAboutCounterThisUser(users);
    }

    private void getInfoAboutCounterThisUser(List<User> users) {
        AuditLogger.log("Get user information");
        int index;
        System.out.println("Enter the ID of the user you want to receive");
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        try {
            index = Integer.parseInt(command);
            if (index == 0) {
                AuditLogger.log("Exit menu");
                return;
            }
        } catch (NumberFormatException e) {
            AuditLogger.log(WRONG_COMMAND);
            System.out.println(WRONG_COMMAND);
            return;
        }
        AuditLogger.log("Get user information with id = " + index);
        User user = users.get(index);
        getListCounters(user);

    }

    private void getUserByEmail() {

        System.out.println("Enter the email of the user you want to receive:\n");
        String inputEmail = readUserCommand();
        AuditLogger.log("Get info about user " + inputEmail);
        User userByEmail = userValidator.findUserByEmail(inputEmail);
        if (userByEmail != null) {
            System.out.println(String.format("Name - %s\nEmail - %s\nRole - %s",
                    userByEmail.getName(), userByEmail.getEmail(), userByEmail.getRole()));
            System.out.println("List water counters:\n");
            userByEmail
                    .getWaterCounterList()
                    .forEach(counter -> System.out.println(counter.getSerialNumber()));
        }

    }

    private void settingsMenu(User owner) {
        System.out.println(COMMAND_ONE + "Change name");
        System.out.println(COMMAND_TWO + "Change email");
        System.out.println(COMMAND_THREE + "Change password");
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        switch (command) {
            case COMMAND_ONE -> changeName(owner);
            case COMMAND_TWO -> changeEmail(owner);
            case COMMAND_THREE -> changePassword(owner);
            case COMMAND_BACK -> {
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

    private void getListCounters(User owner) {
        int count = 1;
        List<WaterCounter> waterCounters = userValidator.getWaterCounters(owner);
        for (WaterCounter counter : waterCounters) {
            System.out.println(count + " SerialNumber - " + counter.getSerialNumber()
                    + "\nType: " + counter.getType());
        }
        waterCounterMenu(waterCounters);
    }

    private void waterCounterMenu(List<WaterCounter> waterCounters) {
        AuditLogger.log("Get info about water counters");
        System.out.println(COMMAND_ONE + COUNTER);
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        switch (command) {
            case COMMAND_ONE -> {
                AuditLogger.log("Get info about water counter");
                getCounterById(waterCounters);
            }
            case COMMAND_BACK -> {
                AuditLogger.log("Exit");
                return;
            }
            default -> {
                AuditLogger.log(WRONG_COMMAND + " " + command);
                System.out.println(WRONG_COMMAND);
            }
        }

    }

    private void getCounterById(List<WaterCounter> waterCounters) {
        System.out.println("Enter id water counter");
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        AuditLogger.log("Get info about water counter with id = " + command);
        int index;
        try {
            index = Integer.parseInt(command);
        } catch (NumberFormatException e) {
            AuditLogger.log(WRONG_COMMAND);
            System.out.println(WRONG_COMMAND);
            return;
        }
        if (index == 0) {
            AuditLogger.log("Exit");
            return;
        }
        WaterCounter counter = waterCounters.get(index - 1);

        getInfoAboutCounter(counter);

    }

    private void getInfoAboutCounter(WaterCounter counter) {
        System.out.println(COMMAND_ONE + CURRENT_VALUE);
        System.out.println(COMMAND_TWO + TRANSFER_DATA);
        System.out.println(COMMAND_THREE + HISTORY_VALUES);
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        switch (command) {
            case COMMAND_ONE -> {
                AuditLogger.log("Get info about current value");
                System.out.println("Current value = " +
                        counterValidator.getCurrentValue(counter.getSerialNumber()));
            }
            case COMMAND_TWO -> {
                AuditLogger.log("Pass value");
                passValue(counter);
            }
            case COMMAND_THREE -> {
                AuditLogger.log("Show values");
                printValues(counter.getSerialNumber());
            }
            case COMMAND_BACK -> {
                AuditLogger.log("Exit");
                return;
            }
            default -> {
                AuditLogger.log(WRONG_COMMAND + " - " + command);
                System.out.println(WRONG_COMMAND);
            }
        }
    }

    private void passValue(WaterCounter counter) {
        System.out.println("Enter new value: ");
        System.out.println(COMMAND_BACK);
        String command = readUserCommand();
        if (command.equals("0")) {
            return;
        }
        float newValue;
        try {
            newValue = Float.parseFloat(command);
        } catch (NumberFormatException e) {
            System.out.println(WRONG_COMMAND);
            return;
        }
        counterValidator.transferData(counter.getSerialNumber(), newValue);
    }

    private void printValues(String serialNumber) {
        Map<String, Float> historyValues = counterValidator.getHistoryValues(serialNumber);
        System.out.println("History values for water counter with serial number - " + serialNumber);
        for (Map.Entry<String, Float> map : historyValues.entrySet()) {
            System.out.println(map.getKey() + " - " + map.getValue());
        }
    }

    private void registerCounter(User owner) {
        AuditLogger.log("Admin " + owner.getEmail() + " entered command - 1");
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
        boolean unique = userValidator.isUnique(email);
        if (unique) {
            return false;
        }
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