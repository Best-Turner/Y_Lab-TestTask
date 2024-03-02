package ru.ylab.app.in.menu;

import ru.ylab.exception.InvalidDataException;
import ru.ylab.model.User;
import ru.ylab.model.WaterMeter;
import ru.ylab.util.AuditLogger;
import ru.ylab.util.UserValidator;
import ru.ylab.util.WaterCounterValidator;

import java.util.List;

public class AdminMenu extends UserMenu {

    private static final String COMMAND_FIVE = "5";
    private static final String LIST_ALL_USERS = "ПОЛУЧИТЬ СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ.";
    private static final String INFO_USER = "ПОЛУЧИТЬ ИНФОРМАЦИЯ О ПОЛЬЗОВАТЕЛЕ.";

    private final static String ADMIN_MENU =
            "---------------------------------\n"
                    + COMMAND_ONE + " - " + REGISTER_COUNTER + "\n"
                    + COMMAND_TWO + " - " + COUNTERS + "\n"
                    + COMMAND_THREE + " - " + WATER_METER + "\n"
                    + COMMAND_FOUR + " - " + LIST_ALL_USERS + "\n"
                    + COMMAND_FIVE + " - " + INFO_USER + "\n"
                    + COMMAND_ZERO + " - " + EXIT +
                    "\n---------------------------------";

    public AdminMenu(User user, UserValidator userValidator, WaterCounterValidator waterCounterValidator) {
        super(user, userValidator, waterCounterValidator);
    }

    @Override
    public void start() {
        actions.put(COMMAND_FOUR, this::getAllUsers);
        actions.put(COMMAND_FIVE, this::getInfoAboutUser);
        while (true) {
            printMenu(ADMIN_MENU);
            AuditLogger.log("Вход в страницу администратора");
            if (execute()) return;
        }
    }

    private void getAllUsers() {
        AuditLogger.log("Получить список всех пользователей");
        StringBuilder accumAccumulateUsers = new StringBuilder();
        accumAccumulateUsers.append("Список всех зарегистрированных пользователей:");
        userValidator.allUsers().forEach(user ->
                accumAccumulateUsers.append("\n\tID пользователя - " + user.getId())
                        .append("\n\tЛогин пользователя - " + user.getEmail())
                        .append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));
        System.out.println(accumAccumulateUsers);
    }

    private void getInfoAboutUser() {
        String inputCommand = readCommand("Ведите ID пользователя: ");
        try {
            AuditLogger.log("Получить информацию о пользователе с ID - " + inputCommand);
            User userById = userValidator.getById(inputCommand);
            List<WaterMeter> waterCounters = userValidator.getWaterCounters(userById);
            System.out.println("------------------------------------------------");
            System.out.println("Подробная информация о пользователе с ID = " + userById.getId());
            System.out.println("Имя пользователя - " + userById.getName());
            System.out.println("Логин пользователя - " + userById.getEmail());
            System.out.println("Роль пользователя - " + userById.getRole());
            StringBuilder accumulateWaterMeter = new StringBuilder();
            accumulateWaterMeter.append("\tСписок зарегистрированных счетчиков:");
            waterCounters.forEach(waterMeter -> {
                accumulateWaterMeter
                        .append("\n\t\tID счетчика - " + waterMeter.getId())
                        .append("\n\t\tСерийный номер - " + waterMeter.getSerialNumber())
                        .append("\n\t\tТекущее значение - " + waterMeter.getCurrentValue());
                System.out.println(accumulateWaterMeter);
            });
        } catch (InvalidDataException e) {
            AuditLogger.log("Пользователь с таким ID" + inputCommand + " не найден");
            System.out.println(e.getMessage());
        }
    }
}
