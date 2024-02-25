package ru.ylab.app.in.menu;

import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.util.List;

public class UserMenu extends Menu {

    private final static String REGISTER_COUNTER = "Добавить новый счетчик";
    private final static String COUNTERS = "Получить мой список счетчиков";
    private final static String WATER_METER = "Получить/передать данные для счетчика";
    private final static String EXIT = "Выход";
    private final User owner;
    private final UserValidator userValidator;
    private final WaterCounterValidator waterCounterValidator;


    private final static String USER_MENU =
            "---------------------------------\n"
                    + Menu.COMMAND_ONE + " - " + REGISTER_COUNTER + "\n"
                    + Menu.COMMAND_TWO + " - " + COUNTERS + "\n"
                    + Menu.COMMAND_THREE + " - " + WATER_METER + "\n"
                    + Menu.COMMAND_ZERO + " - " + EXIT +
                    "\n---------------------------------";

    public UserMenu(User user, UserValidator userValidator, WaterCounterValidator waterCounterValidator) {
        this.owner = user;
        this.userValidator = userValidator;
        this.waterCounterValidator = waterCounterValidator;
    }


    @Override
    public void start() {
        while (true) {
            printMenu(USER_MENU);
            String command = readCommand("Введите команду:\n-> ");
            switch (command) {
                case Menu.COMMAND_ONE -> {
                    AuditLogger.log("User " + owner.getEmail() + " entered command - 1");
                    addNewWaterMeter(owner);
                }
                case Menu.COMMAND_TWO -> {
                    AuditLogger.log("User" + owner.getEmail() + " entered command - 2");
                    printAllWaterMeters(owner);
                }
                case COMMAND_THREE -> {
                    new WaterMeterMenu(owner, waterCounterValidator).start();
                }
                case COMMAND_ZERO -> {
                    return;
                }
                default -> System.out.println("Неверная команда, попробуйте еще раз");
            }
        }
    }


    private void addNewWaterMeter(User owner) {
        while (true) {
            AuditLogger.log("User " + owner.getEmail() + " entered command - " + COMMAND_ONE);
            String inputSerialNumber = readCommand("Введите серийный номер счетчика или 0 для выхода");
            if (inputSerialNumber.equals("0")) {
                break;
            } else {
                String initialValue = readCommand("Введите начальное значение");
                String message = waterCounterValidator
                        .createCounter(inputSerialNumber, initialValue, owner) ?
                        "\n\tСчетчик успешно зарегистрирован\n" : "\n\tНе удалось зарегистрировать\n";
                System.out.println(message);
                break;
            }
        }
    }

    private void printAllWaterMeters(User owner) {
        List<WaterMeter> waterCounters = userValidator.getWaterCounters(owner);
        if (waterCounters.isEmpty()) {
            System.out.println("У вас нет сохраненных счетчиков");
            return;
        }
        StringBuilder accumulatedWaterMetersInfo = new StringBuilder();
        for (WaterMeter counter : waterCounters) {

            accumulatedWaterMetersInfo
                    .append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n")
                    .append("ID - ")
                    .append(counter.getId())
                    .append("\n\tСерийный номер - ")
                    .append(counter.getSerialNumber())
                    .append("\n\tТип: ")
                    .append(counter.getType())
                    .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
        System.out.println("Список ваших счетчиков: \n\t" + accumulatedWaterMetersInfo);
    }

}
