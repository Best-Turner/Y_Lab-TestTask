package ru.ylab.app.in.menu;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.exception.WaterCounterNotFoundException;
import ru.ylab.model.MeterData;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.util.WaterCounterValidator;

import java.util.List;

public class WaterMeterMenu extends Menu {

    private static final String CURRENT_VALUE = "Получить текущее значение";
    private static final String PASS_VALUE = "Передать значение";
    private static final String HISTORY_VALUES = "Получить историю передачи данных";
    private static final String BACK = "Назад";

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
            System.out.println(COMMAND_ONE + " - получить счетчик по ID\n"
                    + COMMAND_TWO + " - получить счетчик по серийному номеру\n" +
                    COMMAND_ZERO + " - назад");

            inputCommand = readCommand("->");
            WaterMeter waterMeter = null;
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
            printMenu(WATER_METER_MENU);
            command = readCommand("->");
            switch (command) {
                case COMMAND_ONE -> printCurrentValue(waterMeter);
                case COMMAND_TWO -> {
                    try {
                        passValue(waterMeter);
                    } catch (InvalidDataException | WaterCounterNotFoundException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.println("Новое значение сохранено!");
                }
                case COMMAND_THREE -> {
                    printHistoryValues(waterMeter);
                }
                case COMMAND_ZERO -> {
                    return;
                }
                default -> {
                    System.out.println("Введена не верная команда");
                    return;
                }
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }

    private void printHistoryValues(WaterMeter waterMeter) {
        StringBuilder builder = new StringBuilder();
        builder.append("История переданных значений для счетчика - ").append(waterMeter.getSerialNumber()).append("\n\t\t");
        validator.getHistoryValues(waterMeter.getId())
                .forEach(el -> builder
                        .append("Дата: " + el.getDate() + "\n")
                        .append("Значение - " + el.getValue()));
    }

    private void printCurrentValue(WaterMeter waterMeter) {
        System.out.printf("Счетчик - %s\n\tТекущее значение - %.2f%n",
                waterMeter.getSerialNumber(), waterMeter.getCurrentValue());
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
