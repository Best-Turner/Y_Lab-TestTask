package ru.ylab.app.in;

import org.jetbrains.annotations.NotNull;
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
    private static final String COMMAND_ZERO = "0";
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

    private void messageForStartPage() {
        AuditLogger.log("Start program");
        System.out.println(COMMAND_ONE + REGISTRATION);
        System.out.println(COMMAND_TWO + AUTHENTICATE);
    }

    private void printMainMenuForUser() {
        AuditLogger.log("User has entered the main menu");
        System.out.println("\t\t" + COMMAND_ONE + REGISTER_COUNTER);
        System.out.println("\t\t" + COMMAND_TWO + COUNTERS);
    }


    private void printMainMenuForAdmin() {
        AuditLogger.log("Admin has entered the main menu");
        printMainMenuForUser();
        System.out.println("\t\t" + COMMAND_THREE + USERS);
        System.out.println("\t\t" + COMMAND_ZERO + " - to exit");

    }

    public User processStartPageCommand() {
        messageForStartPage();
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
        boolean flag = true;
        System.out.println("Welcome, " + owner.getName() + "!");
        while (flag) {
            if (owner.getRole().equals(Role.ADMIN)) {
                printMainMenuForAdmin();
                String command = readUserCommand();
                switch (command) {
                    case COMMAND_ONE -> {
                        AuditLogger.log("Admin" + owner.getEmail() + " entered command - 1");
                        registerCounter(owner);
                    }

                    case COMMAND_TWO -> {
                        AuditLogger.log("Admin" + owner.getEmail() + " entered command - 2");
                        List<WaterCounter> listWaterCounters = getListWaterCounters(owner);
                        if (!listWaterCounters.isEmpty()) {
                            waterCounterMenu(userValidator.getWaterCounters(owner));
                        } else {
                            System.out.println("Your water meters are not registered\n");
                        }
                    }
                    case COMMAND_THREE -> {
                        AuditLogger.log("Admin" + owner.getEmail() + " entered command - 5");
                        getUsers();
                    }
                    case COMMAND_ZERO -> {
                        AuditLogger.log("Admin " + owner.getEmail() + " entered command - 0");
                        flag = false;
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
                printMainMenuForUser();
                System.out.println("\t\t" + EXIT);
                String command = readUserCommand();
                switch (command) {
                    case COMMAND_ONE -> {
                        AuditLogger.log("User " + owner.getEmail() + " entered command - 1");
                        registerCounter(owner);
                    }

                    case COMMAND_TWO -> {
                        AuditLogger.log("User " + owner.getEmail() + " entered command - 2");
                        List<WaterCounter> listWaterCounters = getListWaterCounters(owner);
                        if (!listWaterCounters.isEmpty()) {
                            waterCounterMenu(userValidator.getWaterCounters(owner));
                        } else {
                            System.out.println("Your water meters are not registered\t");
                        }
                    }

                    case "0" -> {
                        AuditLogger.log("User " + owner.getEmail() + " entered command - 0");
                        flag = false;
                    }
                    default -> {
                        AuditLogger.log("User " + owner.getEmail() + " entered command - " + WRONG_COMMAND);
                        System.out.println(WRONG_COMMAND);
                    }
                }
            }
        }
    }

    private void getUsers() {
        AuditLogger.log("Get all users");
        List<User> users = userValidator.allUsers();

        users.forEach(user ->
                System.out.printf("~~~~~~~~~~~~~~~~~~\n"
                                + "ID - %d\n\tName - %s\n\tEmail - %s\n\tRole - %s" + "\n~~~~~~~~~~~~~~~~~~\n",
                        user.getId(), user.getName(), user.getEmail(), user.getRole()));
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
        User user = users.get(index - 1);
        System.out.println("List of user water meters with id " + user.getId());
        getListWaterCounters(user);
        getCounterById(userValidator.getWaterCounters(user));
    }



    private List<WaterCounter> getListWaterCounters(User owner) {
        int id = 1;
        List<WaterCounter> waterCounters = userValidator.getWaterCounters(owner);
        StringBuilder printInfoAboutCounter = new StringBuilder();
        for (WaterCounter counter : waterCounters) {

            printInfoAboutCounter
                    .append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n")
                    .append("ID - ")
                    .append(id++)
                    .append("\n\tSerialNumber - ")
                    .append(counter.getSerialNumber())
                    .append("\n\tType: ")
                    .append(counter.getType())
                    .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        System.out.println(printInfoAboutCounter);
        return waterCounters;
    }

    private void waterCounterMenu(List<WaterCounter> waterCounters) {
        boolean flag = true;
        while (flag) {
            AuditLogger.log("Get info about water counters");
            System.out.println(COMMAND_ONE + COUNTER);
            System.out.println(COMMAND_BACK);
            String command = readUserCommand();
            switch (command) {
                case COMMAND_ONE -> {
                    AuditLogger.log("Get info about water counter");
                    getCounterById(waterCounters);
                }
                case COMMAND_ZERO -> {
                    AuditLogger.log("Exit");
                    flag = false;
                }
                default -> {
                    AuditLogger.log(WRONG_COMMAND + " " + command);
                    System.out.println(WRONG_COMMAND);
                }
            }
        }
    }

    private void getCounterById(List<WaterCounter> waterCounters) {
        System.out.println("Enter ID water counter");
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
        if (index == 0 || index > waterCounters.size()) {
            AuditLogger.log("Exit");
            return;
        }
        WaterCounter counterById = waterCounters.get(index - 1);
        getInfoAboutCounter(counterById);

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
                Float currentValue = counterValidator.getCurrentValue(counter.getSerialNumber());
                System.out.printf("\nSerial number counter - %s\n\tCurrent value = %.2f\n\n", counter.getSerialNumber(), currentValue);
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
            System.out.println("\tDate: " + map.getKey() + "\n\tValue: " + map.getValue());
            System.out.println("~~~~~~~~~~~~~~~~~");
        }
    }

    private void registerCounter(@NotNull User owner) {
        AuditLogger.log("Admin " + owner.getEmail() + " entered command - " + COMMAND_ONE);
        String inputSerialNumber = readUserCommand("Enter serial number the counter or 0 to go back");
        if (inputSerialNumber.equals("0")) {
            pageForRegisteredUser(owner);
        } else {
            String initialValue = readUserCommand("Enter initial value");
            Float inputValue;
            try {
                inputValue = Float.valueOf(initialValue);
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid format. Please repeat again or press 0 to go back\n");
                return;
            }
            String message = counterValidator
                    .createCounter(inputSerialNumber, inputValue, owner) ?
                    "\n\tRegistered successful\n" : "\n\tFailed to register\n";
            //counterValidator.transferData(inputSerialNumber, inputValue);
            System.out.println(message);
        }
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
        return readUserCommand(ENTER_EMAIL);
    }

    private String readInputPassword() {
        return readUserCommand(ENTER_PASSWORD);
    }

    private boolean registerUser() {
        System.out.println("New user registration");
        String name = readUserCommand(ENTER_NAME);
        String email = readUserCommand(ENTER_EMAIL);
        boolean isUniqueEmail = userValidator.isUnique(email);
        if (isUniqueEmail) {
            return false;
        }
        String password = readUserCommand(ENTER_PASSWORD);
        return userValidator.createUser(name, email, password);
    }

    private boolean checkCredentials(String email, String password) {
        return userValidator.isRegister(email, password);
    }

    private String readUserCommand(String message) {
        System.out.println(message);
        return scanner.nextLine();
    }

    private String readUserCommand() {
        System.out.print("\nEnter your choice:\n");
        return scanner.nextLine();
    }
}