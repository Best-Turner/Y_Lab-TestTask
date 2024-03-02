package ru.ylab.app.in.menu;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.WaterCounterValidator;

public class WaterMeterMenu extends Menu {

    private static final String CURRENT_VALUE = "ПОЛУЧИТЬ ТЕКУЩЕЕ ЗНАЧЕНИЕ";
    private static final String PASS_VALUE = "ПЕРЕДАТЬ ЗНАЧЕНИЕ";
    private static final String HISTORY_VALUES = "ПОЛУЧИТЬ ИСТОРИЮ ПЕРЕДАЧИ ДАННЫХ";
    private static final String BACK = "НАЗАД";

    private final static String WATER_METER_MENU =
            "---------------------------------\n"
                    + COMMAND_ONE + " - " + CURRENT_VALUE + "\n"
                    + COMMAND_TWO + " - " + PASS_VALUE + "\n"
                    + COMMAND_THREE + " - " + HISTORY_VALUES + "\n"
                    + COMMAND_ZERO + " - " + BACK +
                    "\n---------------------------------";

    private final User owner;
    private final WaterCounterValidator validator;

    public WaterMeterMenu(User owner, WaterCounterValidator validator) {
        this.owner = owner;
        this.validator = validator;
    }

    @Override
    public void start() {
        String inputCommand;
        while (true) {
            System.out.println(COMMAND_ONE + " - Получить счетчик по ID\n"
                    + COMMAND_TWO + " - Получить счетчик по серийному номеру\n" +
                    COMMAND_ZERO + " - Назад");

            inputCommand = readCommand("->");
            WaterMeter waterMeter;
            switch (inputCommand) {
                case COMMAND_ONE -> {
                    inputCommand = readCommand("Введите ID счетчика");
                    try {
                        waterMeter = validator.getWaterMeterById(inputCommand);
                    } catch (InvalidDataException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
                case COMMAND_TWO -> {
                    inputCommand = readCommand("Введите серийный номер счетчика");
                    try {
                        waterMeter = validator.getWaterMeterBySerialNumber(inputCommand);
                    } catch (InvalidDataException | WaterCounterNotFoundException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
                case COMMAND_ZERO -> {
                    return;
                }
                default -> {
                    System.out.println("Введена не верная команда");
                    return;
                }
            }
            mainMenuForWaterMeter(waterMeter);
        }
    }

    private void mainMenuForWaterMeter(WaterMeter waterMeter) {
        String command;
        while (true) {
            AuditLogger.log("Меню счетчика");
            printMenu(WATER_METER_MENU);
            command = readCommand("->");
            switch (command) {
                case COMMAND_ONE -> {
                    AuditLogger.log("Получить текущее значение");
                    printCurrentValue(waterMeter);
                }
                case COMMAND_TWO -> {
                    try {
                        AuditLogger.log("Передать значения");
                        passValue(waterMeter);
                    } catch (InvalidDataException | WaterCounterNotFoundException e) {
                        System.out.println(e.getMessage());
                        AuditLogger.log("Ошибка: " + e.getMessage());
                        break;
                    }
                    AuditLogger.log("Значение сохранено");
                    System.out.println("Новое значение сохранено!");
                }
                case COMMAND_THREE -> {
                    AuditLogger.log("Получить историю передачи данных");
                    printHistoryValues(waterMeter);
                }
                case COMMAND_ZERO -> {
                    AuditLogger.log("Назад");
                    return;
                }
                default -> {
                    System.out.println("Введена не верная команда");
                    AuditLogger.log("Ошибка: Введена не верная команда");
                    return;
                }
            }
        }
    }

    private void printHistoryValues(WaterMeter waterMeter) {
        StringBuilder accumulateHistory = new StringBuilder();
        accumulateHistory.append("История переданных значений для счетчика - ").append(waterMeter.getSerialNumber()).append("\n");
        validator.getHistoryValues(waterMeter.getId())
                .forEach(el -> accumulateHistory
                        .append("\t\tДата передачи показаний: " + el.getDate())
                        .append(" Значение - " + el.getValue() + "\n"));
        System.out.println(accumulateHistory);
    }

    private void printCurrentValue(WaterMeter waterMeter) {
        float currentValue = validator.getCurrentValue(waterMeter.getSerialNumber());
        System.out.printf("Счетчик - %s\n\tТекущее значение - %.2f%n",
                waterMeter.getSerialNumber(), currentValue);
    }

    private void passValue(WaterMeter waterMeter) throws InvalidDataException, WaterCounterNotFoundException {
        System.out.println("Введите новое значение: ");
        System.out.println(COMMAND_ZERO + " - " + BACK);
        String command = readCommand("\n->");
        if (command.equals("0")) {
            return;
        }
        validator.changeCurrentValue(waterMeter.getSerialNumber(), command);
    }
}
