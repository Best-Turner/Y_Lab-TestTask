package ru.ylab.app.in;

import java.util.Scanner;

public class ConsoleServiceMediator {
    private static final String COMMAND_ONE = "1";
    private static final String COMMAND_TWO = "2";
    private static final String COMMAND_THREE = "3";
    private static final String REGISTRATION = " - Registration account";
    private static final String AUTHENTICATE = " - Login to account";
    private static final String REGISTER_COUNTER = " - Register counter";
    private static final String COUNTERS = " - Get list counters";
    private static final String SETTINGS = " - Settings profile";
    private static final String USERS = " - Get all users";
    private static final String USER = " - Get user by email";
    private static final String CURRENT_VALUE = " - Get current value";
    private static final String HISTORY_VALUES = " - Get the history of transferred data";
    private static final String VALUES_BY_MONTH = " - Get data for a specific month";
    private static final String TRANSFER_DATA = " - Transfer counter data";



    private static final String COMMAND_BACK = "0 - Back";
    private static final String EXIT = "0 - Exit";
    private Scanner scanner;

    public ConsoleServiceMediator() {
        this.scanner = new Scanner(System.in);
    }

    public void startPage() {
        System.out.println(REGISTRATION);
        System.out.println(AUTHENTICATE);
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void mainMenu() {
        System.out.println(REGISTER_COUNTER);
        System.out.println(COUNTERS);
        System.out.println(SETTINGS);
    }

    public void menuForAdmin() {
        mainMenu();
        System.out.println(USERS);
        System.out.println(USER);
    }


}