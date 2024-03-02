package ru.ylab.app.in.menu;

import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMenu extends Menu {

    protected final static String REGISTER_COUNTER = "ДОБАВИТЬ НОВЫЙ СЧЕТЧИК";
    protected final static String COUNTERS = "ПОЛУЧИТЬ МОЙ СПИСОК СЧЕТЧИКОВ";
    protected final static String WATER_METER = "ПОЛУЧИТЬ/ПЕРЕДАТЬ ДАННЫЕ ДЛЯ СЧЕТЧИКА";
    protected final static String EXIT = "ВЫХОД";
    private final static String USER_MENU =
            "---------------------------------\n"
                    + COMMAND_ONE + " - " + REGISTER_COUNTER + "\n"
                    + COMMAND_TWO + " - " + COUNTERS + "\n"
                    + COMMAND_THREE + " - " + WATER_METER + "\n"
                    + COMMAND_ZERO + " - " + EXIT +
                    "\n---------------------------------";
    protected final User owner;
    protected final UserValidator userValidator;
    protected final WaterCounterValidator waterCounterValidator;
    protected final Map<String, Runnable> actions = new HashMap<>();

    public UserMenu(User user, UserValidator userValidator, WaterCounterValidator waterCounterValidator) {
        this.owner = user;
        this.userValidator = userValidator;
        this.waterCounterValidator = waterCounterValidator;
        initActions();
    }

    private void initActions() {
        actions.put(COMMAND_ONE, () -> addNewWaterMeter(owner));
        actions.put(COMMAND_TWO, () -> printAllWaterMeters(owner));
        actions.put(COMMAND_THREE, () -> new WaterMeterMenu(owner, waterCounterValidator).start());
    }

    @Override
    public void start() {
        if (owner.getRole().name().equals("ADMIN")) {
            new AdminMenu(owner, userValidator, waterCounterValidator).start();
            return;
        }
        while (true) {
            printMenu(USER_MENU);
            if (execute()) return;
        }
    }

    protected boolean execute() {
        String command = readCommand("Введите команду:\n-> ");
        if (command.equals("0")) {
            return true;
        }
        if (actions.containsKey(command)) {
            actions.get(command).run();
        } else {
            System.out.println("Неверная команда, попробуйте еще раз");
        }
        return false;
    }

    protected void addNewWaterMeter(User owner) {
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

    protected void printAllWaterMeters(User owner) {
        List<WaterMeter> waterCounters = userValidator.getWaterCounters(owner);
        if (waterCounters.isEmpty()) {
            System.out.println("У вас нет сохраненных счетчиков");
            return;
        }
        StringBuilder accumulatedWaterMetersInfo = new StringBuilder();
        accumulatedWaterMetersInfo.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        for (WaterMeter counter : waterCounters) {
            accumulatedWaterMetersInfo
                    .append("ID - ")
                    .append(counter.getId())
                    .append("\n\tСерийный номер - ")
                    .append(counter.getSerialNumber())
                    .append("\n\tТип: ")
                    .append(counter.getType())
                    .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        }
        System.out.println("Список ваших счетчиков: \n" + accumulatedWaterMetersInfo);
    }
}
