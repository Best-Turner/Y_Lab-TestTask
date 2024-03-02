package ru.ylab.app.in.menu;

import java.util.Scanner;

public abstract class Menu {

    protected static final String COMMAND_ZERO = "0";
    protected static final String COMMAND_ONE = "1";
    protected static final String COMMAND_TWO = "2";
    protected static final String COMMAND_THREE = "3";
    protected static final String COMMAND_FOUR = "4";
    private final Scanner scanner = new Scanner(System.in);

    protected static void printMenu(String nameMenu) {
        System.out.println(nameMenu);
    }

    public abstract void start();

    protected String readCommand(String infoMessage) {
        System.out.println(infoMessage);
        return scanner.nextLine();
    }
}
